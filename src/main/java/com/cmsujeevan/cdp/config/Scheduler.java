package com.cmsujeevan.cdp.config;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.cmsujeevan.cdp.api.dao.entity.*;
import com.cmsujeevan.cdp.api.service.*;
import com.cmsujeevan.cdp.common.util.TimeUtil;
import com.cmsujeevan.cdp.exception.exceptions.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.cmsujeevan.cdp.common.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static com.cmsujeevan.cdp.common.constants.Constants.PATH_SLASH;
import static com.cmsujeevan.cdp.common.constants.Constants.TEMP_FOLDER;
import static com.cmsujeevan.cdp.common.util.Util.deleteFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final String activateFolder;
    private final String sourceBucket;
    private final S3Service s3Service;

    private final Contact360Service contact360Service;
    private final Contact360TempService contact360TempService;
    private final ContactAccount360Service contactAccount360Service;
    private final ContactAccount360TempService contactAccount360TempService;
    private final CDTContactsAccountService cdtContactsAccountService;
    private final CDTContactsAccountTempService cdtContactsAccountTempService;
    private final DynamicTableService dynamicTableService;
    private final DynamicTableTempService dynamicTableTempService;
    private final DynamicTableListService dynamicTableListService;

    // @Scheduled(fixedDelay = 20000)
    @Scheduled(cron = "${interval-in-cron}")
    public void processDataFiles() {
            process(activateFolder);
    }

    /**
     * Get the latest (only today) files from the S3 source folder, save them locally one by one,
     * and uncompress them if they are compressed. Then, read them and save it in database tables.
     *
     *
     * @param sourceFolder
     */
    public void process(String sourceFolder) {
        log.info("Process start at, , {}", new Date());
        var tempDownloadFilePath = "";
        var uncompressFilePath = "";

        log.info("check folder {} in bucket {}", sourceFolder, sourceBucket);
        boolean isDataTypeFolderExists = s3Service.isFolderExists(sourceBucket, sourceFolder + PATH_SLASH);

        if (!isDataTypeFolderExists) {
            log.error("data does not found in the source folder {}", sourceFolder);
            return;
        }

        List<String> s3Records = s3Service.readListS3Objects(sourceBucket, sourceFolder);
        log.info("{} s3 records found for data", s3Records.size());

        var today = LocalDate.now();

        List<String> s3FilesWithinDates = filterFilesWithinDays(s3Records, today, today);
        log.info("Filtered file withing from and to dates {}", s3FilesWithinDates);

        if (s3FilesWithinDates.isEmpty()) {
            log.error("No file found today: {}", today);
            return;
        }

        for (int i = 0; i < s3FilesWithinDates.size(); i++) {
            log.info("latest file {}", s3FilesWithinDates.get(i));
            var fileName = FilenameUtils.getName(s3FilesWithinDates.get(i));

            String[] parts = s3FilesWithinDates.get(i).split("/");
            String dataType = parts[1].toLowerCase();

            boolean requiredColumnsExist = true;

            if (!isCompressed(s3FilesWithinDates.get(i)))
                requiredColumnsExist = isColumnsExistFromS3File(s3FilesWithinDates.get(i));

            if (requiredColumnsExist) {
                //Temporary file download path
                tempDownloadFilePath = getTempDownloadPath(fileName);

                //Download file to local path(tempDownloadFilePath)
                downloadFile(sourceBucket, s3FilesWithinDates.get(i), tempDownloadFilePath);

                if(isCompressed(tempDownloadFilePath)) {
                    uncompressFilePath = uncompressFile(tempDownloadFilePath);
                    //Delete compressed file from local
                    deleteLocalFile(tempDownloadFilePath);
                    tempDownloadFilePath = "";
                } else {
                    uncompressFilePath = tempDownloadFilePath;
                }

                if (!isColumnsExistInFile(uncompressFilePath)) continue;

                readAndProcessFile(uncompressFilePath, dataType);

                deleteLocalFile(uncompressFilePath);
                uncompressFilePath = "";

                updateTargetTableAndDeleteTempTable(dataType);
                log.info("files are saved into the database successfully.");
            } else {
                log.info("Required column not exist: {}", s3FilesWithinDates.get(i));
            }

        }
        // Make sure all the local files are deleted
        if (!uncompressFilePath.isBlank()) {
            deleteLocalFile(uncompressFilePath);
        } else if(!tempDownloadFilePath.isBlank()){
            deleteLocalFile(tempDownloadFilePath);
        }

    }

    /**
     * Read the csv file header from s3 bucket and very the required column exist or not
     *
     * @param filePath
     * @return boolean
     */
    private boolean isColumnsExistFromS3File(String filePath) {
        String columns = readFileFirstLine(sourceBucket, filePath);
        return isColumnExist(columns);
    }

    /**
     * Read the csv header and verify the required column exist or not
     *
     * @param filePath
     * @return boolean
     */
    private boolean isColumnsExistInFile(String filePath) {
        BufferedReader reader= null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            return isColumnExist(reader.readLine());
        } catch (IOException e) {
            return false;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Unable to close the file: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Check specific column exist or not
     *
     * @param columns
     * @return boolean
     */
    private boolean isColumnExist(String columns) {
        String[] specificColumns = { Constants.AMPERITY_ID, Constants.SYSCO_CUSTOMER_ID };
        for (String word : specificColumns) {
            if (!columns.contains(word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Update the records from temporary table and truncate the temporary table
     *
     * @param dataType
     */
    private void updateTargetTableAndDeleteTempTable(String dataType) {
        switch (dataType) {
            case Constants.CONTACTS_360:
                log.info("save contact 360 data into main table.");
                contact360Service.insertRecordsFromTempTable();
                contact360TempService.truncateTable();
                break;
            case Constants.CONTACT_ACCOUNT_360:
                log.info("save contact account 360 data into main table.");
                contactAccount360Service.insertRecordsFromTempTable();
                contactAccount360TempService.truncateTable();
                break;
            case Constants.CDT_CONTACTS_ACCOUNT:
                log.info("save cdt contacts account data into main table.");
                cdtContactsAccountService.insertRecordsFromTempTable();
                cdtContactsAccountTempService.truncateTable();
                break;
            default:
                log.info("save active files data into main table.");
                dynamicTableService.insertRecordsFromTempTable(dataType);
                dynamicTableListService.updateTableDate(dataType);
                dynamicTableTempService.truncateTable();
        }
    }

    /**
     * Read the file, handle the exception and close it properly
     *
     * @param uncompressFilePath
     * @param dataType
     */
    private void readAndProcessFile(String uncompressFilePath, String dataType) {
        log.info("save csv file to database table: {}", dataType);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(uncompressFilePath));
            readAndSaveFileIntoDbTable(reader, uncompressFilePath, dataType);
        } catch (FileNotFoundException e) {
            log.error("File not exist in local: {}", e.getMessage());
            throw new CustomException("File not exist in local path {}.", uncompressFilePath);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Unable to close the file: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Read each line from the csv file and save it into relevant temporary tables
     *
     * @param reader
     * @param uncompressFilePath
     * @param dataType
     */
    private void readAndSaveFileIntoDbTable(BufferedReader reader, String uncompressFilePath, String dataType) {
        String line, tableName = null;
        ArrayList<String> headers = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<=^|,)(\"(?:[^\"]+|\"\")*\"|[^,]*)");
        int count = 0; // using count for batch process
        List<Contact360Temp> contact360TempList = new ArrayList<>();
        List<ContactAccount360Temp> contactAccount360TempList = new ArrayList<>();
        List<CDTContactsAccountTemp> cdtContactsAccountTempList = new ArrayList<>();
        List<DynamicTableTemp> dynamicTableTempList = new ArrayList<>();

        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                log.error("Error occurred while reading the file lines {}", e.getMessage());
                throw new CustomException("Exception occurs while reading the file {}.", uncompressFilePath);
            }
            Matcher matcher = pattern.matcher(line);
            if (headers.size() == 0) {
                parseHeaders(line, headers);
            } else {

                String[] values = parseValues(matcher, headers.size());
                if (values == null || values.length != headers.size()) {
                    log.info("Mismatched row length. Skipping row.");
                    continue;
                }

                JsonNode rowNode = createRowNode(headers, values);
                count++; // using count for batch process

                switch (dataType) {
                    case Constants.CONTACTS_360 -> {
                        addJsonNodeToContactList(rowNode, contact360TempList);
                        if (count == Constants.LIST_BATCH_SIZE) {
                            log.info("save contact 360 data into database.");
                            contact360TempService.createBatchContact360(contact360TempList);
                            contact360TempList.clear();
                            count=0;
                        }
                    }
                    case Constants.CONTACT_ACCOUNT_360 -> {
                        addJsonNodeToAccountList(rowNode, contactAccount360TempList);
                        if (count == Constants.LIST_BATCH_SIZE) {
                            log.info("save contact account 360 data into database.");
                            contactAccount360TempService.createBatchContactAccount360Temp(contactAccount360TempList);
                            contactAccount360TempList.clear();
                            count=0;
                        }
                    }
                    case Constants.CDT_CONTACTS_ACCOUNT -> {
                        addJsonNodeToCDTContactsAccountList(rowNode, cdtContactsAccountTempList);
                        if (count == Constants.LIST_BATCH_SIZE) {
                            log.info("save cdt contacts account data into database.");
                            cdtContactsAccountTempService.createBatchCDTContactsAccount(cdtContactsAccountTempList);
                            cdtContactsAccountTempList.clear();
                            count=0;
                        }
                    }
                    default -> {
                        addJsonNodeToActivateList(rowNode, dynamicTableTempList);
                        if (count == Constants.LIST_BATCH_SIZE) {
                            log.info("save active files data into database.");
                            // if table exist in DynamicTableList, it's already created
                            tableName = (tableName == null) ? dynamicTableListService.isTableExist(dataType) : tableName;
                            createDynamicTableIfNotExist(tableName, dataType);
                            dynamicTableTempService.createBatchData(dynamicTableTempList);
                            dynamicTableTempList.clear();
                            count=0;
                        }
                    }
                }

            }
        }
        // Save the data, which are less than the LIST_BATCH_SIZE
        if (!contact360TempList.isEmpty()) contact360TempService.createBatchContact360(contact360TempList);
        if (!contactAccount360TempList.isEmpty()) contactAccount360TempService.createBatchContactAccount360Temp(contactAccount360TempList);
        if (!cdtContactsAccountTempList.isEmpty()) cdtContactsAccountTempService.createBatchCDTContactsAccount(cdtContactsAccountTempList);
        if (!dynamicTableTempList.isEmpty()) dynamicTableTempService.createBatchData(dynamicTableTempList);
    }

    /**
     * Remove double quotes from the line and add it to the header list
     *
     * @param line
     * @param headers
     */
    private void parseHeaders(String line, List<String> headers) {
        String[] headerList = line.split(",");
        for (String header: headerList) {
            headers.add(header.replaceAll("\"",""));
        }
    }

    /**
     * Extract the column values using matcher
     *
     * @param matcher
     * @param size
     * @return String[]
     */
    private String[] parseValues(Matcher matcher, int size) {
        String[] values = new String[size];
        int index = 0;

        while (matcher.find()) {
            String value = matcher.group();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            // replace all occurrences of the backslash character (\\) with the forward slash character (/)
            values[index++] = value.replaceAll("\\\\", Matcher.quoteReplacement("/"));
        }
        return values;
    }

    /**
     * Create the Object Node / Json Node with headers and values from csv
     *
     * @param headers - list of headers / column values from csv
     * @param values - rows / column data from csv
     * @return JsonNode
     */
    private ObjectNode createRowNode(List<String> headers, String[] values) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rowNode = objectMapper.createObjectNode();

        for (int i = 0; i < headers.size(); i++) {
            rowNode.put(headers.get(i), values[i]);
        }

        return rowNode;
    }

    /**
     * Create the dynamic table, if it's not exist.
     *
     * @param tableName
     * @param dataType
     */
    private void createDynamicTableIfNotExist(String tableName, String dataType) {
        if (tableName==null || tableName.isEmpty()) {
            dynamicTableService.createDynamicTable(dataType);
            dynamicTableListService.saveDynamicTable(DynamicTableList.builder()
                    .tableName(dataType)
                    .status(Constants.ACTIVE)
                    .updatedTime(TimeUtil.getCurrentTimestampInTimezone(TimeUtil.DEFAULT_TIME_ZONE))
                    .build());
        }
    }

    /**
     * Add Json Node to dynamic temporary contact 360 table List
     *
     * @param rowNode - row json node
     * @param Contact360TempList - contact 360 temporary table List
     */
    private void addJsonNodeToContactList(JsonNode rowNode, List<Contact360Temp> Contact360TempList) {
        Contact360Temp contact360Temp = new Contact360Temp();
        contact360Temp.setData(rowNode);
        contact360Temp.setAmpId(rowNode.get("amperity_id").toString().replaceAll("\"", ""));
        contact360Temp.setEmail(rowNode.get("email").toString().replaceAll("\"", ""));
        contact360Temp.setOkta_user_id(rowNode.get("okta_user_id") != null ? rowNode.get("okta_user_id").toString().replaceAll("\"", "") : "");
        contact360Temp.setContactSfId(rowNode.get("id") != null ? rowNode.get("id").toString().replaceAll("\"", "") : "");
        Contact360TempList.add(contact360Temp);
    }

    /**
     * Add Json Node to dynamic temporary contact Account 360 table List
     *
     * @param rowNode - row json node
     * @param contactAccount360TempList - contact Account 360 temporary table List
     */
    private void addJsonNodeToAccountList(JsonNode rowNode, List<ContactAccount360Temp> contactAccount360TempList) {
        ContactAccount360Temp contactAccount360Temp = new ContactAccount360Temp();
        contactAccount360Temp.setData(rowNode);
        contactAccount360Temp.setAmpId(rowNode.get("amperity_id").toString().replaceAll("\"", ""));
        contactAccount360Temp.setAcct_sysco_cust_id(rowNode.get("account_sysco_customer_id") != null ?
                rowNode.get("account_sysco_customer_id").toString().replaceAll("\"", "") : "");
        contactAccount360Temp.setContact_sf_id(rowNode.get("contact_salesforce_id") != null ?
                rowNode.get("contact_salesforce_id").toString().replaceAll("\"", "") : "");
        contactAccount360Temp.setAccount_sf_id(rowNode.get("account_salesforce_id") != null ?
                rowNode.get("account_salesforce_id").toString().replaceAll("\"", "") : "");
        contactAccount360TempList.add(contactAccount360Temp);
    }

    /**
     * Add Json Node to dynamic temporary CDT Contacts Account table List
     *
     * @param rowNode - row json node
     * @param cdtContactsAccountTempList - cdt Contacts Account temporary table List
     */
    private void addJsonNodeToCDTContactsAccountList(JsonNode rowNode, List<CDTContactsAccountTemp> cdtContactsAccountTempList) {
        CDTContactsAccountTemp cdtContactsAccountTemp = new CDTContactsAccountTemp();
        cdtContactsAccountTemp.setData(rowNode);
        cdtContactsAccountTemp.setAmpId(rowNode.get("amperity_id").toString().replaceAll("\"", ""));
        cdtContactsAccountTemp.setEmail(rowNode.get("email").toString().replaceAll("\"", ""));
        cdtContactsAccountTemp.setOkta_user_id(rowNode.get("okta_user_id") != null ? rowNode.get("okta_user_id").toString().replaceAll("\"", "") : "");
        cdtContactsAccountTemp.setContactSfId(rowNode.get("contactid") != null ? rowNode.get("contactid").toString().replaceAll("\"", "") : "");
        cdtContactsAccountTemp.setContact_account_pk(rowNode.get("contact_account_pk") != null ? rowNode.get("contact_account_pk").toString().replaceAll("\"", "") : "");
        cdtContactsAccountTempList.add(cdtContactsAccountTemp);
    }

    /**
     * Add Json Node to dynamic temporary table List
     *
     * @param rowNode - row json node
     * @param dynamicTableTempList - dynamic temporary table List
     */
    private void addJsonNodeToActivateList(JsonNode rowNode, List<DynamicTableTemp> dynamicTableTempList) {
        DynamicTableTemp dynamicTableTemp = new DynamicTableTemp();
        dynamicTableTemp.setAmpId(rowNode.get(Constants.AMPERITY_ID) != null ?
                rowNode.get(Constants.AMPERITY_ID).toString().replaceAll("\"", "") :
                rowNode.get("amperityid") != null ? rowNode.get("amperityid").toString().replaceAll("\"", "") :
                "");
        dynamicTableTemp.setSyscoCustomerId(rowNode.get(Constants.SYSCO_CUSTOMER_ID) != null ?
                rowNode.get(Constants.SYSCO_CUSTOMER_ID).toString().replaceAll("\"", "") :
                rowNode.get("sysco_cust_id") != null ? rowNode.get("sysco_cust_id").toString().replaceAll("\"", "") :
                        "");
        dynamicTableTempList.add(dynamicTableTemp);
    }

    /**
     * Uncompress the file in the same (compressed file) location
     *
     * @param tempDownloadPath - file path
     * @return file path
     */
    private String uncompressFile(String tempDownloadPath) {
        log.info("uncompress file {}", tempDownloadPath);
        try (GZIPInputStream gzInputStream = new GZIPInputStream(new FileInputStream(tempDownloadPath));
             FileOutputStream outputStream = new FileOutputStream(tempDownloadPath.replaceAll(".gz",""))) {

            byte[] buffer = new byte[10 * 1024 * 1024];
            int bytesRead;

            while ((bytesRead = gzInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return tempDownloadPath.replaceAll(".gz","");
        } catch (IOException e) {
            log.error("Error occurred while uncompressing the file : {}", e.getMessage());
            throw new CustomException("Exception occurs while uncompressing file %s.", tempDownloadPath);
        }
    }

    /**
     * Check whether the file is compressed or not
     *
     * @param tempDownloadPath - file path
     * @return boolean
     */
    private boolean isCompressed(String tempDownloadPath) {
        boolean isCompressedFile = tempDownloadPath.endsWith(".gz");
        return isCompressedFile;
    }

    /**
     * Filter files within the date range
     *
     * @param s3records - list of s3 records
     * @param from     - start date
     * @param to       - end date
     * @return
     */
    private List<String> filterFilesWithinDays(List<String> s3records, LocalDate from, LocalDate to) {
        return s3records.stream()
                .filter(s -> !s.endsWith(PATH_SLASH) && withingDays(s, from, to))
                .collect(Collectors.toList());
    }

    /**
     * Filter files created withing from and to dates
     *
     * @param filePath - file path
     * @param from     - start date
     * @param to       - end date
     * @return
     */
    private boolean withingDays(String filePath, LocalDate from, LocalDate to) {
        LocalDate fileDate = extractDateFromPath(filePath);
        return (fileDate.isAfter(from) || fileDate.isEqual(from)) &&
                (fileDate.isBefore(to) || fileDate.isEqual(to));
    }

    /**
     * extract date from the file path
     *
     * @param filePath - file path (folder1/folder2/folder3/file)
     * @return
     */
    private LocalDate extractDateFromPath(String filePath) {
        //remove source folder name from file path and remove "/"
        String[] elements = filePath.split("/");
        String subPath = null;
        // Check if the string has at least two elements
        if (elements.length >= 2) {
            // Remove the first two elements
            subPath = String.join("/", Arrays.copyOfRange(elements, 2, elements.length));
        }

        String[] splits = subPath.split(PATH_SLASH);
        var year = Integer.parseInt(splits[0]);
        var month = Integer.parseInt(splits[1]);
        var days = Integer.parseInt(splits[2]);

        return LocalDate.of(year, month, days);
    }

    /**
     * Get temporary download file path
     *
     * @param fileName          - S3 bucket
     * @return temporary file path
     */
    private String getTempDownloadPath(String fileName) {
        return TEMP_FOLDER + fileName;
    }

    /**
     * Download file from S3 bucket to local file path
     *
     * @param sourceBucket          - S3 bucket
     * @param s3Key                 - s3 file path
     * @param localDownloadFilePath - local download file path
     */
    protected void downloadFile(String sourceBucket, String s3Key, String localDownloadFilePath) {

        try {
            log.info("start downloading {} to the local path {}", s3Key, localDownloadFilePath);
            S3Object s3object = s3Service.getS3Object(sourceBucket, s3Key);
            //create parent directories for the file
            FileUtils.createParentDirectories(new File(localDownloadFilePath));

            try (InputStream input = new BufferedInputStream(
                    s3object.getObjectContent());
                 OutputStream output = new BufferedOutputStream(new FileOutputStream(localDownloadFilePath))) {

                // At a time, write 10MB buffer
                byte[] buffer = new byte[10 * 1024 * 1024];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
            }
            log.info("download complete");
        } catch (Exception ex) {
            log.error("error occurred while downloading file : {}", ex.getMessage());
            throw new CustomException("Exception occurs while downloading file %s.", FilenameUtils.getName(s3Key));
        }
    }

    /**
     * Read file from S3 bucket without load the full file.
     *
     * @param sourceBucket          - S3 bucket
     * @param s3Key                 - s3 file path
     */
    protected String readFileFirstLine(String sourceBucket, String s3Key) {

        try {
            log.info("start reading {} the file..", s3Key);
            S3Object s3object = s3Service.getS3Object(sourceBucket, s3Key);

            // Read the first line of the object
            S3ObjectInputStream objectInputStream = s3object.getObjectContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(objectInputStream, "UTF-8"));

            // return the first line
            return reader.readLine();

        } catch (Exception ex) {
            log.error("error occurred while reading the file : {}", ex.getMessage());
            throw new CustomException("Exception occurs while reading the file %s.", FilenameUtils.getName(s3Key));
        }
    }

    /**
     * Delete file from specific location
     *
     * @param tempDownloadPath - file path
     * @void
     */
    private void deleteLocalFile(String tempDownloadPath) {
        boolean deleted = deleteFile(tempDownloadPath);

        if (deleted)
            log.info("successfully deleted file {}", tempDownloadPath);
        else
            log.error("file {} deletion unsuccessfully", tempDownloadPath);
    }
}