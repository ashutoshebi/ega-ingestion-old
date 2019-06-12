/*
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
 */
package uk.ac.ebi.ega.ukbb.temp.ingestion.properties;

public class ReEncryptProperties {

    private String stagingPath;

    private String relativePathInsideStaging;

    private String ukbbPath;

    private boolean storeFileInFire = false;

    public String getStagingPath() {
        return stagingPath;
    }

    public void setStagingPath(final String stagingPath) {
        this.stagingPath = stagingPath;
    }

    public String getRelativePathInsideStaging() {
        return relativePathInsideStaging;
    }

    public void setRelativePathInsideStaging(final String relativePathInsideStaging) {
        this.relativePathInsideStaging = relativePathInsideStaging;
    }

    public boolean isStoreFileInFire() {
        return storeFileInFire;
    }

    public boolean shouldStoreFileInFire() {
        return isStoreFileInFire();
    }

    public void setStoreFileInFire(final boolean storeFileInFire) {
        this.storeFileInFire = storeFileInFire;
    }

    public String getUkbbPath() {
        return ukbbPath;
    }

    public void setUkbbPath(String ukbbPath) {
        this.ukbbPath = ukbbPath;
    }
}