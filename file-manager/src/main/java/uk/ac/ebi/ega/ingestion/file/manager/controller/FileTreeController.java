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

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.ega.ingestion.file.manager.dto.FileTreeDTO;
import uk.ac.ebi.ega.ingestion.file.manager.dto.FileTreeWrapper;
import uk.ac.ebi.ega.ingestion.file.manager.dto.resources.assemblers.FileHierarchyResourceAssembler;
import uk.ac.ebi.ega.ingestion.file.manager.models.FileHierarchyModel;
import uk.ac.ebi.ega.ingestion.file.manager.persistence.entities.FileHierarchy;
import uk.ac.ebi.ega.ingestion.file.manager.services.IFileManagerService;
import uk.ac.ebi.ega.ingestion.file.manager.utils.FileStructureType;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static uk.ac.ebi.ega.ingestion.file.manager.controller.ControllerUtils.extractVariablePath;

@RequestMapping(value = "/file/tree")
@RestController
public class FileTreeController {

    private static final String REL = "self";
    private final IFileManagerService fileManagerService;

    public FileTreeController(final IFileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }

    @GetMapping(value = "/{accountId}/{locationId}/**", produces = MediaTypes.HAL_JSON_VALUE)
    public Resource<FileTreeWrapper> getFileHierarchy(@PathVariable String accountId,
                                                      @PathVariable String locationId, HttpServletRequest request) throws FileNotFoundException {

        final Link link = linkTo(FileTreeController.class).withSelfRel();
        final Path path = Paths.get(extractVariablePath(request));
        final String baseURI = new StringBuilder(link.getHref()).append("/").append(accountId).append("/")
                .append(locationId).toString();
        final FileTreeWrapper fileTreeWrapper = new FileTreeWrapper();
        final List<FileHierarchyModel> fileHierarchyModels = fileManagerService.findAllFilesAndFoldersInPathNonRecursive(accountId, locationId, path);

        for (FileHierarchyModel fileHierarchyModel : fileHierarchyModels) {
            final Link selfLink = new Link(new StringBuilder(baseURI).
                    append(fileHierarchyModel.getOriginalPath()).toString(), REL);

            if (FileStructureType.FILE.equals(fileHierarchyModel.getFileType())) {
                final FileTreeDTO fileTreeDTO = FileTreeDTO.file(fileHierarchyModel);
                fileTreeDTO.add(selfLink);
                fileTreeWrapper.addFile(fileTreeDTO);
            } else {
                final FileTreeDTO fileTreeDTO = FileTreeDTO.folder(fileHierarchyModel);
                fileTreeDTO.add(selfLink);
                fileTreeWrapper.addFolder(fileTreeDTO);
            }
        }
        return new Resource<>(fileTreeWrapper, new Link(baseURI, REL));
    }

    @GetMapping(value = "/list/{accountId}/{locationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResources<FileTreeDTO> getAllFiles(@PathVariable String accountId,
                                                   @PathVariable String locationId,
                                                   @QuerydslPredicate(root = FileHierarchy.class)
                                                           Predicate predicate,
                                                   Pageable pageable,
                                                   PagedResourcesAssembler assembler,
                                                   FileHierarchyResourceAssembler fileHierarchyResourceAssembler) throws FileNotFoundException {
        return assembler.toResource(fileManagerService.findAllFilesInRootPathRecursive(accountId, locationId, predicate, pageable), fileHierarchyResourceAssembler);
    }
}
