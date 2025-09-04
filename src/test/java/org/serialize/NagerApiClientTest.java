package org.serialize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.serialize.model.Holiday;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NagerApiClientTest {
    @Mock
    private NagerApiClient apiClient;

    private HolidayService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 30)
                .atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        service = new HolidayService(apiClient, fixedClock);
    }

    @Test
    void getPublicHolidays_throwsException_whenApiResponseIsInvalid() throws Exception {
        when(apiClient.getPublicHolidays(2025, "US")).thenThrow(new RuntimeException("Invalid response"));

        Exception exception = assertThrows(RuntimeException.class, () -> service.getLast3Holidays("US"));

        assertEquals("Invalid response", exception.getMessage());
    }

    @Test
    void getPublicHolidays_returnsEmptyList_whenCountryCodeIsInvalid() throws Exception {
        when(apiClient.getPublicHolidays(2024, "INVALID")).thenReturn(List.of());

        List<Holiday> result = service.getLast3Holidays("INVALID");

        assertTrue(result.isEmpty());
    }

    @Test
    void getLast3Holidays_returnsOnlyPastHolidays_whenMixedDatesExist() throws Exception {
        int year = LocalDate.now().getYear();
        List<Holiday> holidays = List.of(
                holiday(year + "-01-01", "New Year", "New Year"),
                holiday(year + "-12-31", "Future Holiday", "Future Holiday")
        );

        when(apiClient.getPublicHolidays(year, "US")).thenReturn(holidays);

        List<Holiday> result = service.getLast3Holidays("US");

        assertEquals(1, result.size());
        assertEquals("New Year", result.getFirst().getLocalName());
    }

    private Holiday holiday(String date, String localName, String name) {
        Holiday h = new Holiday();
        h.setDate(date);
        h.setLocalName(localName);
        h.setName(name);
        return h;
    }
}
