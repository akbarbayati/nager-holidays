package org.serialize;

import org.serialize.model.Holiday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HolidayServiceTest {

    private NagerApiClient apiClient;
    private HolidayService service;

    @BeforeEach
    void setUp() {
        apiClient = Mockito.mock(NagerApiClient.class);
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 30)
                .atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        service = new HolidayService(apiClient, fixedClock);
    }

    private Holiday holiday(String date, String localName, String name) {
        Holiday h = new Holiday();
        h.setDate(date);
        h.setLocalName(localName);
        h.setName(name);
        return h;
    }

    @Test
    void testGetLast3Holidays() throws Exception {
        int year = LocalDate.now().getYear();
        List<Holiday> holidays = Arrays.asList(
                holiday(year + "-01-01", "New Year", "New Year's Day"),
                holiday(year + "-05-01", "Labour Day", "Labour Day"),
                holiday(year + "-07-04", "Independence", "Independence Day"),
                holiday(year + "-12-25", "Christmas", "Christmas Day")
        );

        when(apiClient.getPublicHolidays(eq(year), eq("US"))).thenReturn(holidays);

        List<Holiday> result = service.getLast3Holidays("US");
        assertEquals(3, result.size());
        assertTrue(result.getFirst().getLocalName().contains("Christmas")); // most recent past
    }

    @Test
    void testCountWeekdayHolidays() throws Exception {
        List<Holiday> usHolidays = Arrays.asList(
                holiday("2024-01-01", "New Year", "New Year's Day"), // Monday
                holiday("2024-01-06", "WeekendHoliday", "WeekendHoliday") // Saturday
        );
        List<Holiday> deHolidays = List.of(
                holiday("2024-05-01", "Tag der Arbeit", "Labour Day") // Wednesday
        );

        when(apiClient.getPublicHolidays(2024, "US")).thenReturn(usHolidays);
        when(apiClient.getPublicHolidays(2024, "DE")).thenReturn(deHolidays);

        Map<String, Long> counts = service.countWeekdayHolidays(2024, Arrays.asList("US", "DE"));

        assertEquals(1, counts.get("US")); // only 1 not weekend
        assertEquals(1, counts.get("DE"));
        assertEquals(2, counts.size());
    }

    @Test
    void testGetCommonHolidays() throws Exception {
        List<Holiday> usHolidays = Arrays.asList(
                holiday("2024-07-04", "Independence Day", "Independence Day"),
                holiday("2024-12-25", "Christmas Day", "Christmas")
        );
        List<Holiday> caHolidays = Arrays.asList(
                holiday("2024-07-01", "Canada Day", "Canada Day"),
                holiday("2024-12-25", "Noël", "Christmas")
        );

        when(apiClient.getPublicHolidays(2024, "US")).thenReturn(usHolidays);
        when(apiClient.getPublicHolidays(2024, "CA")).thenReturn(caHolidays);

        List<Holiday> common = service.getCommonHolidays(2024, "US", "CA");

        assertEquals(1, common.size());
        assertTrue(common.getFirst().getLocalName().contains("Christmas"));
    }
}