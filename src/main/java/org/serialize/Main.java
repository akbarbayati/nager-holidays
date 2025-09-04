package org.serialize;

import org.serialize.model.Holiday;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        HolidayService service = new HolidayService();

        System.out.print("Enter country code (e.g., US): ");
        String country = scanner.nextLine().trim().toUpperCase();
        if (country.isEmpty() || country.length() != 2) {
            System.out.println("Invalid country code.");
            return;
        }

        System.out.print("Enter year (e.g., 2024): ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year.");
            return;
        }

        System.out.println("=== Last 3 Holidays in " + country + " ===");
        List<Holiday> last3 = service.getLast3Holidays(country);
        last3.forEach(System.out::println);

        System.out.print("Enter comma-separated country codes for weekday holiday count (e.g., US,DE,FR): ");
        String[] codes = scanner.nextLine().trim().toUpperCase().split(",");
        List<String> codeList = Arrays.asList(codes);

        System.out.println("\n=== Weekday Holiday Count in " + year + " (" + String.join(", ", codeList) + ") ===");
        Map<String, Long> counts = service.countWeekdayHolidays(year, codeList);
        counts.forEach((k, v) -> System.out.println(k + " -> " + v));

        System.out.print("Enter two country codes for common holidays (e.g., US,CA): ");
        String[] commonCodes = scanner.nextLine().trim().toUpperCase().split(",");
        if (commonCodes.length != 2) {
            System.out.println("Please enter exactly two country codes.");
            return;
        }
        System.out.println("\n=== Common Holidays in " + year + " (" + commonCodes[0] + ", " + commonCodes[1] + ") ===");
        List<Holiday> common = service.getCommonHolidays(year, commonCodes[0], commonCodes[1]);
        common.forEach(System.out::println);
    }
}
