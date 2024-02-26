package dev.poncio.todolistmeisters.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

public class DateUtils {

    public static boolean isWeekEnd(LocalDateTime localDateTime) {
        Set<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        return weekend.contains(localDateTime.getDayOfWeek());
    }

    public static boolean todayIsWeekEnd() {
        return isWeekEnd(LocalDateTime.now());
    }

}
