package org.unibl.etf.soundflow.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.soundflow.services.AudioService;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class AudioServiceImpl implements AudioService {

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
}
