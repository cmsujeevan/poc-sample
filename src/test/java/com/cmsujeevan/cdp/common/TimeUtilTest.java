package com.cmsujeevan.cdp.common;

import com.cmsujeevan.cdp.common.util.TimeUtil;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TimeUtilTest {

    @Test
    void testGetTimestamp() {
        String timestamp = TimeUtil.currentDate(TimeUtil.DATE_TIME_FORMAT_JOB_ID
                , TimeUtil.DEFAULT_TIME_ZONE);
        assertNotNull(timestamp);
    }

    @Test
    void testGetCurrentTimestampInTimezone() {
        Timestamp timestamp = TimeUtil.getCurrentTimestampInTimezone(TimeUtil.DEFAULT_TIME_ZONE);
        assertNotNull(timestamp);
    }

    @Test
    void testGetTimeInMillis(){
        long minuteInMillis = TimeUtil.getTimeInMillis(1, "minute");
        assertEquals(minuteInMillis, 60000L);

        long hourInMillis = TimeUtil.getTimeInMillis(1, "hour");
        assertEquals(hourInMillis, 3600000L);

        long dayInMillis = TimeUtil.getTimeInMillis(1, "day");
        assertEquals(dayInMillis, 86400000L);

        long def = TimeUtil.getTimeInMillis(1, "minutes");
        assertEquals(def, 1000);
    }

}
