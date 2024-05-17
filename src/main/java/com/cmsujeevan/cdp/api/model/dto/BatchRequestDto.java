package com.cmsujeevan.cdp.api.model.dto;

import static com.cmsujeevan.cdp.common.constants.Constants.DATA_TYPE;
import static com.cmsujeevan.cdp.common.constants.FormatRegexps.PATTERN_DATE;
import static com.cmsujeevan.cdp.exception.constants.ErrorConstants.EXCEPTION_MSG_FROM_DATE;
import static com.cmsujeevan.cdp.exception.constants.ErrorConstants.EXCEPTION_MSG_TO_DATE;
import static com.cmsujeevan.cdp.exception.constants.ErrorConstants.EXCEPTION_REQUIRED_DATA_TYPE;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequestDto {

    @JsonProperty(value = DATA_TYPE)
    @Schema(required = true, example = "email_engaged")
    @NotBlank(message = EXCEPTION_REQUIRED_DATA_TYPE)
    private String dataType;

    @JsonProperty(value = "filter_from_date")
    @DateTimeFormat(iso = DATE)
    @Schema(format = "yyyy-MM-dd", required = true, example = "2022-05-01")
    @Pattern(regexp = PATTERN_DATE, message = EXCEPTION_MSG_FROM_DATE)
    private String fromDate;

    @JsonProperty(value = "filter_to_date")
    @DateTimeFormat(iso = DATE)
    @Schema(format = "yyyy-MM-dd", required = true, example = "2022-05-01")
    @Pattern(regexp = PATTERN_DATE, message = EXCEPTION_MSG_TO_DATE)
    private String toDate;

    @Override
    public String toString() {
        return String.format("{ \"dataType\" : \"%s\", \"fromDate\" : \"%s\", \"toDate\" : \"%s\"}", dataType, fromDate, toDate);
    }
}
