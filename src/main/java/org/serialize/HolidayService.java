package org.serialize;

import org.serialize.model.Holiday;
import org.serialize.util.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

public record HolidayService(NagerApiClient apiClient, Clock clock) {
    private static final Logger log = LoggerFactory.getLogger(HolidayService.class);
    public HolidayService() {
        this(new NagerApiClient(), Clock.systemDefaultZone());
    }


    public List<Holiday> getLast3Holidays(String countryCode) throws Exception {
        log.info("Fetching last 3 holidays for country: {}", countryCode);
        int currentYear = LocalDate.now(clock).getYear();
        var holidays = apiClient.getPublicHolidays(currentYear, countryCode);
        var today = LocalDate.now(clock);

        PriorityQueue<Holiday> pq = new PriorityQueue<>(3,
                Comparator.comparing(h -> LocalDate.parse(h.getDate())));
        for (Holiday h : holidays) {
            LocalDate date = LocalDate.parse(h.getDate());
            if (date.isBefore(today)) {
                pq.offer(h);
                if (pq.size() > 3) {
                    pq.poll();
                }
            }
        }
        List<Holiday> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }
        log.debug("Last 3 holidays found: {}", result);
        return result.reversed();
    }

    public Map<String, Long> countWeekdayHolidays(int year, List<String> countryCodes) throws Exception {
        log.info("Counting weekday holidays for year: {} and countries: {}", year, countryCodes);
        var result = new HashMap<String, Long>();
        for (String code : countryCodes) {
            var holidays = apiClient.getPublicHolidays(year, code);
            long count = holidays.stream()
                    .filter(h -> !DateUtils.isWeekend(h.getDate()))
                    .count();
            result.put(code, count);
            log.debug("Country: {}, Weekday holidays: {}", code, count);
        }
        Map<String, Long> sortedResult = result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, _) -> a, LinkedHashMap::new));
        log.debug("Sorted weekday holidays count: {}", sortedResult);
        return sortedResult;
    }

    public List<Holiday> getCommonHolidays(int year, String firstCountry, String secondCountry) throws Exception {
        log.info("Getting common holidays for year: {}, countries: {} & {}", year, firstCountry, secondCountry);
        List<Holiday> firstHolidays = apiClient.getPublicHolidays(year, firstCountry);
        List<Holiday> secondHolidays = apiClient.getPublicHolidays(year, secondCountry);

        Map<String, Holiday> firstCountryHolidaysByDate = firstHolidays.stream()
                .collect(Collectors.toMap(Holiday::getDate, h -> h, (a, _) -> a));

        List<Holiday> common = secondHolidays.stream()
                .filter(h -> firstCountryHolidaysByDate.containsKey(h.getDate()))
                .map(h -> {
                    Holiday combined = new Holiday();
                    combined.setDate(h.getDate());
                    combined.setLocalName(firstCountryHolidaysByDate.get(h.getDate()).getLocalName() + " / " + h.getLocalName());
                    combined.setName(firstCountryHolidaysByDate.get(h.getDate()).getName() + " / " + h.getName());
                    return combined;
                })
                .collect(Collectors.toList());
        log.debug("Common holidays found: {}", common);
        return common;
    }
}
