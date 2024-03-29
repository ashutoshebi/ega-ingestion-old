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
package uk.ac.ebi.ega.ingestion.file.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.ega.ingestion.file.manager.persistence.entities.DownloadBox;
import uk.ac.ebi.ega.ingestion.file.manager.persistence.repository.DownloadBoxRepository;

import javax.validation.Valid;

@RepositoryRestController
public class DownloadBoxController implements ApplicationEventPublisherAware {

    @Autowired
    private DownloadBoxRepository repository;

    private ApplicationEventPublisher publisher;

    @PostMapping(value = "/downloadBoxes")
    @ResponseBody
    public PersistentEntityResource postDownloadBox(@Valid @RequestBody DownloadBox downloadBox,
                                                    PersistentEntityResourceAssembler assembler) {
        downloadBox.persist();
        publisher.publishEvent(new BeforeCreateEvent(downloadBox));
        downloadBox = repository.save(downloadBox);
        publisher.publishEvent(new AfterCreateEvent(downloadBox));
        return assembler.toFullResource(downloadBox);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

}
