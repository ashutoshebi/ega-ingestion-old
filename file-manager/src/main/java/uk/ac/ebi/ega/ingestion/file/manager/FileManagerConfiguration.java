/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.ega.ingestion.file.manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import uk.ac.ebi.ega.encryption.core.services.IPasswordEncryptionService;
import uk.ac.ebi.ega.encryption.core.services.PasswordEncryptionService;
import uk.ac.ebi.ega.ingestion.file.manager.message.DownloadBoxFileProcess;
import uk.ac.ebi.ega.ingestion.file.manager.persistence.repository.DownloadBoxFileJobRepository;
import uk.ac.ebi.ega.ingestion.file.manager.persistence.repository.DownloadBoxJobRepository;
import uk.ac.ebi.ega.ingestion.file.manager.persistence.repository.HistoricDownloadBoxFileJobRepository;
import uk.ac.ebi.ega.ingestion.file.manager.persistence.repository.HistoricDownloadBoxJobRepository;
import uk.ac.ebi.ega.ingestion.file.manager.services.DatasetService;
import uk.ac.ebi.ega.ingestion.file.manager.services.DownloadBoxJobService;
import uk.ac.ebi.ega.ingestion.file.manager.services.IDatasetService;
import uk.ac.ebi.ega.ingestion.file.manager.services.IDownloadBoxJobService;
import uk.ac.ebi.ega.ingestion.file.manager.services.IKeyGenerator;
import uk.ac.ebi.ega.ingestion.file.manager.services.IMailingService;
import uk.ac.ebi.ega.ingestion.file.manager.services.MailingService;
import uk.ac.ebi.ega.ingestion.file.manager.services.key.RandomKeyGenerator;

@Configuration
public class FileManagerConfiguration {

    @Value("${spring.kafka.file.events.queue.name}")
    private String fileEventQueueName;

    @Value("${spring.kafka.file.ingestion.queue.name}")
    private String fileIngestionQueueName;

    @Value("${spring.kafka.file.re-encryption.queue.name}")
    private String fileReEncryptionQueueName;

    @Value("${spring.kafka.download-box.queue.name}")
    private String downloadBoxQueueName;

    @Value("${file.manager.download.box.password.size}")
    private int passwordKeySize;

    @Value("${file.manager.encryption.password.encryption.key}")
    private char[] passwordEncryptionKey;

    @Bean
    public IKeyGenerator keyGenerator() {
        return new RandomKeyGenerator(passwordKeySize);
    }

    @Bean
    public IMailingService mailingService(JavaMailSender javaMailSender) {
        return new MailingService(javaMailSender);
    }

    @Bean
    public IDownloadBoxJobService downloadBoxJobService(DownloadBoxJobRepository downloadBoxJobRepository,
                                                        DownloadBoxFileJobRepository downloadBoxFileJobRepository,
                                                        HistoricDownloadBoxJobRepository historicBoxJobRepository,
                                                        HistoricDownloadBoxFileJobRepository historicBoxFileJobRepository,
                                                        IDatasetService datasetService, IMailingService mailingService,
                                                        KafkaTemplate<String, DownloadBoxFileProcess> kafkaTemplate) {
        return new DownloadBoxJobService(downloadBoxJobRepository, downloadBoxFileJobRepository,
                historicBoxJobRepository, historicBoxFileJobRepository,
                datasetService, mailingService, kafkaTemplate, downloadBoxQueueName);
    }

    @Bean
    public IDatasetService datasetService(NamedParameterJdbcTemplate jdbcTemplate) {
        return new DatasetService(jdbcTemplate);
    }

    @Bean
    public IPasswordEncryptionService passwordEncryptionService(){
        return new PasswordEncryptionService(passwordEncryptionKey);
    }

}
