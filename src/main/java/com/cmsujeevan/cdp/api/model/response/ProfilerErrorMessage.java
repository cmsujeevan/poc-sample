package com.cmsujeevan.cdp.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilerErrorMessage {

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("error_message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;

}

