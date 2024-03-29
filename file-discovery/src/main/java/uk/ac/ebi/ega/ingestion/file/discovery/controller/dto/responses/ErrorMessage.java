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
package uk.ac.ebi.ega.ingestion.file.discovery.controller.dto.responses;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class ErrorMessage {

    private long timestamp;

    private int status;

    private String error;

    private String exception;

    private String message;

    public ErrorMessage(HttpStatus status, Exception ex, String message) {
        timestamp = new Date().getTime();
        this.status = status.value();
        this.error = status.getReasonPhrase();

        this.exception = ex.getClass().getCanonicalName();
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }
}
