package com.cmsujeevan.cdp.common.util;

import com.cmsujeevan.cdp.common.constants.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;

public class Util {

    public static long getFileSize(String path) throws IOException {
        Path filePath = Paths.get(path);
        return Files.size(filePath);
    }

    public static boolean deleteFile(String path){
        File file = new File(path);
        return FileUtils.deleteQuietly(file);
    }

    public static final Supplier<String> jobIdSupplier = () ->
            Constants.JOB_ID_PREFIX_NAME + TimeUtil.getCurrentTimestampInTimezone(TimeUtil.DEFAULT_TIME_ZONE)
                    + UUID.randomUUID().toString().substring(0, 3);

}
