package com.cmsujeevan.cdp.api.dao.entity;

import com.cmsujeevan.cdp.exception.NotEmptyJsonNodeValidator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import static com.cmsujeevan.cdp.exception.constants.ErrorConstants.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@ToString
@Table(name = "contact_account_360")
public class ContactAccount360 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "data")
    @NotEmptyJsonNodeValidator.NotEmptyJsonNode
    private JsonNode data;

    @Column(name = "amperity_id")
    @NotBlank(message = EXCEPTION_REQUIRED_AMPERITY_ID)
    @JsonProperty(value = "amperity_id")
    private String ampId;

    @Column(name = "account_sysco_customer_id")
    @NotBlank(message = EXCEPTION_REQUIRED_ACC_SYSCO_CUST_ID)
    @JsonProperty(value = "account_sysco_customer_id")
    private String acct_sysco_cust_id;

    @Column(name = "contact_salesforce_id")
    @NotBlank(message = EXCEPTION_REQUIRED_CONTACT_SF_ID)
    @JsonProperty(value = "contact_salesforce_id")
    private String contact_sf_id;

    @Column(name = "account_salesforce_id")
    @NotBlank(message = EXCEPTION_REQUIRED_ACCOUNT_SF_ID)
    @JsonProperty(value = "account_salesforce_id")
    private String account_sf_id;
}
