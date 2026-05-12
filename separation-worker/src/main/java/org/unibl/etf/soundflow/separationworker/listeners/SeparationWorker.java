package org.unibl.etf.soundflow.separationworker.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.separationworker.config.RabbitConfig;
import org.unibl.etf.soundflow.separationworker.models.dto.SeparationMessage;
import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.separationworker.services.AudioService;
import org.unibl.etf.soundflow.separationworker.services.SeparationService;

@Service
public class SeparationWorker {
    private final SeparationService separationService;
    private final AudioService audioService;

    public SeparationWorker(SeparationService separationService, AudioService audioService) {
        this.separationService = separationService;
        this.audioService = audioService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleSeparationMessage(SeparationMessage message) {
        System.out.println("Primljena poruka: " + message);
        SeparationJobEntity job = separationService.getById(message.getJobId());

        separationService.separate(job);
        try {
            audioService.archiveStems(job);
            System.out.println("Separation completed for job with id: " + job.getId());
        } catch(Exception e) {
            separationService.failSeparationJob(job, e.getMessage());
            System.err.println("Separation failed for job with id: " + job.getId());
            e.printStackTrace();
        }
    }
}
