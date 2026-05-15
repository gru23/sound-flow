package org.unibl.etf.soundflow.separationworker.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.separationworker.exceptions.AudioOperationException;
import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.separationworker.services.AudioService;
import org.unibl.etf.soundflow.separationworker.services.SeparationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AudioServiceImpl implements AudioService {
    private final SeparationService separationService;

    public AudioServiceImpl(SeparationService separationService) {
        this.separationService = separationService;
    }

    @Override
    public void archiveStems(SeparationJobEntity job) throws AudioOperationException {
        File jobFile = new File(job.getSourcePath());
        String stemsFolderPath = jobFile.getParent() + File.separator +
                "htdemucs" + File.separator +
                jobFile.getName().substring(0, jobFile.getName().lastIndexOf('.'));
        File stemsFolder = new File(stemsFolderPath);
        if (!stemsFolder.exists() || stemsFolder.listFiles() == null) {
            throw new AudioOperationException("No stems found to archive (separation failed)");
        }
        File zipFile = new File(jobFile.getParent() + "_separation.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            for (File file : stemsFolder.listFiles()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        } catch(IOException e) {
            throw new AudioOperationException("Unable to archive separation job");
        }
        job.setSeparatedPath(zipFile.getAbsolutePath());
        separationService.update(job);
    }
}
