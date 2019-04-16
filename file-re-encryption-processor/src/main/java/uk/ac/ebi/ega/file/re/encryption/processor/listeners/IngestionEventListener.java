/*
 *
 * Copyright 2019 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.ega.file.re.encryption.processor.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import uk.ac.ebi.ega.file.re.encryption.processor.messages.DownloadBoxFileProcess;
import uk.ac.ebi.ega.file.re.encryption.processor.services.ProcessService;

@Component
public class IngestionEventListener {

    private final Logger logger = LoggerFactory.getLogger(IngestionEventListener.class);

    @Autowired
    private ProcessService processService;

    @KafkaListener(id = "${spring.kafka.client-id}", topics = "${spring.kafka.file.ingestion.queue.name}", groupId =
            "${spring.kafka.consumer.group-id}", clientIdPrefix = "executor", autoStartup = "false")
    public void listen(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, DownloadBoxFileProcess data,
                       Acknowledgment acknowledgment) throws InterruptedException {
        logger.info("New event - key: {} data {}", key, data);
        try {
            processService.lock(key, data);
            acknowledgment.acknowledge();
        } catch (DataIntegrityViolationException e) {
            logger.info("key: {} is being processed, skip event", key);
            acknowledgment.acknowledge();
            return;
        }
        // Do process
        processService.unlock(key);

    }

}