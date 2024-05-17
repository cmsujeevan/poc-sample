package com.cmsujeevan.cdp.api.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    @JsonProperty("job_id")
    private String jobId;
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("pre_signed_url")
    private String url;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("error_message")
    private String errorMessage;
}
