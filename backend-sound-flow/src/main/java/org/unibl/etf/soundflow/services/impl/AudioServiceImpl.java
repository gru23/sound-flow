package org.unibl.etf.soundflow.services.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.soundflow.config.RabbitConfig;
import org.unibl.etf.soundflow.models.dto.SeparationMessage;
import org.unibl.etf.soundflow.models.dto.SeparationStatusResponse;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.models.enums.SeparationStatus;
import org.unibl.etf.soundflow.models.requests.SeparationRequest;
import org.unibl.etf.soundflow.services.AudioService;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.SeparationJobService;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AudioServiceImpl implements AudioService {
    private final ClientService clientService;
    private final SeparationJobService separationJobService;
    private final RabbitTemplate rabbitTemplate;

    public AudioServiceImpl(ClientService clientService, SeparationJobService separationJobService, RabbitTemplate rabbitTemplate) {
        this.clientService = clientService;
        this.separationJobService = separationJobService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void uploadAudio(MultipartFile file) throws IOException {
        String rootDirectory = System.getProperty("user.dir");
        String uploadDirectory = rootDirectory + File.separator + "uploads" + File.separator;
//        String uploadDir = "C:/Users/Administrator/uploads/";
        String filenameDecoded = URLDecoder.decode(file.getOriginalFilename(), StandardCharsets.UTF_8);
        File targetFile = new File(uploadDirectory + filenameDecoded);

        // create folder if not exists
        targetFile.getParentFile().mkdirs();

        // save file
        file.transferTo(targetFile);
        System.out.println("File saved to: " + targetFile.getAbsolutePath());
    }

    @Override
    public SeparationStatusResponse submitSeparationRequest(SeparationRequest request) {
        ClientEntity client = clientService.findById(request.getClientId());
        String storagePath = generateUploadPath(client.getUsername());
        String filePath = storageAudioFile(request.getFile(), storagePath);

        SeparationJobEntity job = new SeparationJobEntity(request.getOption(), filePath, client);
        job = separationJobService.save(job);

        // RabbitMQ
        SeparationMessage message = new SeparationMessage(job.getId(), job.getSourcePath(), job.getOption());
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, message);

        return new SeparationStatusResponse(job.getId(), job.getStatus(), null);
    }

    @Override
    public SeparationStatusResponse getSeparationStatus(String jobId) {
        SeparationJobEntity job = separationJobService.getSeparationJob(jobId);

        String resultUrl = null;
        if(job.getStatus() == SeparationStatus.DONE && job.getSeparatedPath() != null)
            resultUrl = "http://localhost:8080/audio/separations/" + job.getId();

        return new SeparationStatusResponse(job.getId(), job.getStatus(), resultUrl);
    }

    @Override
    public ResponseEntity<Resource> downloadSeparatedZip(String jobId) {
        FileSystemResource resource = separationJobService.getSeparatedZip(jobId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }

    private String storageAudioFile(MultipartFile audioFile, String storagePath) {
        String filenameDecoded = URLDecoder.decode(audioFile.getOriginalFilename(), StandardCharsets.UTF_8);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = storagePath + File.separator + timestamp + "_" + filenameDecoded;
        File targetFile = new File(filePath);

        try {
            audioFile.transferTo(targetFile);
            System.out.println("File saved to: " + targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException("Error saving file", e);
        }
    }

    private String generateUploadPath(String clientUsername) {
        String tempFolder = "C:\\Users\\Administrator\\Desktop\\Fakultet\\SoundFlow\\separation\\uploads\\" + clientUsername;
        File clientFolder = new File(tempFolder);
        if (!clientFolder.exists())
            clientFolder.mkdirs();
        return tempFolder;
    }
}
