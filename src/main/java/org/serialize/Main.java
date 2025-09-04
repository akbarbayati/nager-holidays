package org.serialize;

import org.serialize.model.Holiday;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        HolidayService service = new HolidayService();

        System.out.println("=== Last 3 Holidays in US ===");
        List<Holiday> last3 = service.getLast3Holidays("US");
        last3.forEach(System.out::println);

        System.out.println("\n=== Weekday Holiday Count in 2024 (US, DE, FR) ===");
        Map<String, Long> counts = service.countWeekdayHolidays(2024, Arrays.asList("US", "DE", "FR"));
        counts.forEach((k, v) -> System.out.println(k + " -> " + v));

        System.out.println("\n=== Common Holidays in 2024 (US, CA) ===");
        List<Holiday> common = service.getCommonHolidays(2024, "US", "CA");
        common.forEach(System.out::println);
    }
}
