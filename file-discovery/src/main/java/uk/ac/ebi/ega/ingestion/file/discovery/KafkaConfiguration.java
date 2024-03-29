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
package uk.ac.ebi.ega.ingestion.file.discovery;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.ac.ebi.ega.ingestion.commons.messages.FileEvent;
import uk.ac.ebi.ega.ingestion.commons.messages.IngestionEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<Integer, FileEvent> fileEventProducerFactory() {
        DefaultKafkaProducerFactory<Integer, FileEvent> factory =
                new DefaultKafkaProducerFactory<>(fileEventProducerConfigs());
        factory.setValueSerializer(new JsonSerializer<>(getObjectMapper()));
        return factory;
    }

    @Bean
    public ProducerFactory<Integer, IngestionEvent> ingestionEventProducerFactory() {
        DefaultKafkaProducerFactory<Integer, IngestionEvent> factory =
                new DefaultKafkaProducerFactory<>(fileIngestionProducerConfigs());
        factory.setValueSerializer(new JsonSerializer<>(getObjectMapper()));
        return factory;
    }

    @Bean
    public KafkaTemplate<Integer, FileEvent> fileEventKafkaTemplate() {
        return new KafkaTemplate<>(fileEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<Integer, IngestionEvent> ingestionEventKafkaTemplate() {
        return new KafkaTemplate<>(ingestionEventProducerFactory());
    }

    @Bean
    public Map fileIngestionProducerConfigs() {
        Map properties = new HashMap();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // introduce a delay on the send to allow more messages to accumulate
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        return properties;
    }

    @Bean
    public Map fileEventProducerConfigs() {
        Map properties = new HashMap();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        // introduce a delay on the send to allow more messages to accumulate
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        return properties;
    }

    private ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        objectMapper.getFactory().enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
        return objectMapper;
    }

}
