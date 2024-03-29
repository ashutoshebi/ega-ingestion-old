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
package uk.ac.ebi.ega.ingestion.commons.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class FileStatic implements Comparable<FileStatic> {

    private String absolutePath;

    @JsonProperty
    private long length;

    @JsonProperty
    private long lastModified;

    public FileStatic() {
    }

    public FileStatic(File file) {
        this(file.getAbsolutePath(), file.length(), file.lastModified());
    }

    public FileStatic(String absolutePath, long length, long lastModified) {
        this.absolutePath = absolutePath;
        this.length = length;
        this.lastModified = lastModified;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public long length() {
        return length;
    }

    public long lastModified() {
        return lastModified;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int compareTo(FileStatic fileStatic) {
        int path = absolutePath.compareTo(fileStatic.absolutePath);
        if (path != 0) {
            return path;
        }
        return Long.compare(lastModified, fileStatic.lastModified);
    }

    @Override
    public String toString() {
        return "FileStatic{" +
                "absolutePath='" + absolutePath + '\'' +
                ", length=" + length +
                ", lastModified=" + lastModified +
                '}';
    }

}
