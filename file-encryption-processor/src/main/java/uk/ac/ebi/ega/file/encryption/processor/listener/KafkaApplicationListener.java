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
package uk.ac.ebi.ega.file.encryption.processor.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.event.KafkaEvent;
import org.springframework.stereotype.Component;

@Component
public class KafkaApplicationListener implements ApplicationListener<KafkaEvent> {

    private final static Logger logger = LoggerFactory.getLogger(KafkaApplicationListener.class);

    @Override
    public void onApplicationEvent(KafkaEvent event) {
        logger.error("Kafka Event: {}", event);
    }

}
