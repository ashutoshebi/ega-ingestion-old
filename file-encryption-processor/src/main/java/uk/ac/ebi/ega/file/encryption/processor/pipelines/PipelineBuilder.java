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
package uk.ac.ebi.ega.file.encryption.processor.pipelines;

import uk.ac.ebi.ega.file.encryption.processor.models.FileType;
import uk.ac.ebi.ega.file.encryption.processor.services.PipelineService;

import java.io.File;
import java.io.IOException;

public class PipelineBuilder implements PipelineService {

    private File secretRing;
    private File secretRingPassphrase;
    private File encryptPassword;

    public PipelineBuilder(File secretRing, File secretRingPassphrase, File encryptPassword) {
        this.secretRing = secretRing;
        this.secretRingPassphrase = secretRingPassphrase;
        this.encryptPassword = encryptPassword;
    }

    @Override
    public IngestionPipeline getPipeline(File fileOrigin) throws IOException {
        switch (FileType.fromExtension(fileOrigin)) {
            case BAM:
                return buildWithSamToolsIndex(fileOrigin);
            default:
                return buildDefault(fileOrigin);
        }
    }

    private IngestionPipeline buildDefault(File file) throws IOException {
        File output = changeExtension(file, "gpg", "cip");
        return new DefaultIngestionPipeline(file, secretRing, secretRingPassphrase, output, encryptPassword);
    }

    private IngestionPipeline buildWithSamToolsIndex(File file) throws IOException {
        File output = changeExtension(file, "gpg", "cip");
        File index = changeExtension(file, "bam.gpg", "bai.cip");
        return new IngestionSamToolsIndex(file, secretRing, secretRingPassphrase, output, index, encryptPassword);
    }

    private static File changeExtension(File file, String currentExtension, String newExtension) {
        String absolutePath = file.getAbsolutePath();
        return new File(absolutePath.substring(0, absolutePath.length() - currentExtension.length()) + newExtension);
    }

}
