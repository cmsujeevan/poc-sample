package com.cmsujeevan.cdp.common.constants;

public class Constants {

    public static final String JOB_ID_PREFIX_NAME = "job-id_";

    public enum JobStatus {
        PENDING("Pending"),
        STARTED("Started"),

        COMPLETED("Completed"),

        DATA_TYPE_NOT_FOUND("DataTypeNotFound"),

        EMPTY_DATA("NoNewFileCreated"),

        FAILED("Failed");

        final String status;

        JobStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    public static final String PATH_SLASH = "/";

    public static final int MAX_ERROR_MSG_LENGTH = 400;

    public static final int MAX_URL_LENGTH = 2000;

    public static final int BLOCK_SIZE = 1024;

    public static final String TEMP_FOLDER = "/tmp/";
    
    public static final String FILE_EXTENSION_SEPARATOR = ".";

    public static final String DATA_TYPE = "data_type";

    public static final String BATCH_API_PREFIX = "batch";

    public static final String CORRELATION_ID = "SYY-Correlation-ID";

    public static final String JOB_ID = "job_id";

    public static final String MDC_CORRELATION_ID = "correlationId";

    public static final String CORRELATIONID_REGEX = "[a-zA-Z0-9-]+";

    public static final int LIST_BATCH_SIZE = 100000;

    public static final String CONTACTS_360 = "contacts_360";

    public static final String CONTACT_ACCOUNT_360 = "contact_account_360";

    public static final String CDT_CONTACTS_ACCOUNT = "cdt_contacts_account";

    public static final String STITCHED_CONTACT = "stitched_contact";

    public static final String ACTIVE_ACCOUNTS = "active_accounts";

    public static final String AMPERITY_ID = "amperity_id";

    public static final String SYSCO_CUSTOMER_ID = "sysco_customer_id";

    public static final String ACTIVE = "active";

    public static final String NOT_ACTIVE = "not_active";


}
