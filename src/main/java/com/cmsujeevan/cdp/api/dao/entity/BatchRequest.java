package com.cmsujeevan.cdp.api.dao.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import static com.cmsujeevan.cdp.common.constants.Constants.MAX_ERROR_MSG_LENGTH;
import static com.cmsujeevan.cdp.common.constants.Constants.MAX_URL_LENGTH;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "batch_request")
public class BatchRequest implements Serializable {

    @Id
    @Column(name = "job_id")
    private String jobId;

    @Column(name = "status")
    private String status;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "pre_signed_url", length = MAX_URL_LENGTH)
    private String preSignedUrl;

    @Column(name = "error", length = MAX_ERROR_MSG_LENGTH)
    private String error;

    @Column(name = "submission_timestamp")
    private Timestamp submissionTimestamp;

    @Column(name = "completion_timestamp")
    private Timestamp completionTimestamp;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() != obj.getClass())
            return false;
        var batchRequest = (BatchRequest) obj;

        return batchRequest != null && this.getJobId().equals(batchRequest.getJobId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }
}
