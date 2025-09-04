package org.serialize.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateUtils {
    public static boolean isWeekend(String isoDate) {
        LocalDate date = LocalDate.parse(isoDate);
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}