package org.serialize;

import org.serialize.model.Holiday;
import org.serialize.util.DateUtils;

import java.time.LocalDate;
import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

public record HolidayService(NagerApiClient apiClient, Clock clock) {
    public HolidayService() {
        this(new NagerApiClient(), Clock.systemDefaultZone());
    }


    public List<Holiday> getLast3Holidays(String countryCode) throws Exception {
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
        return result.reversed();
    }

    public Map<String, Long> countWeekdayHolidays(int year, List<String> countryCodes) throws Exception {
        var result = new HashMap<String, Long>();
        for (String code : countryCodes) {
            var holidays = apiClient.getPublicHolidays(year, code);
            long count = holidays.stream()
                    .filter(h -> !DateUtils.isWeekend(h.getDate()))
                    .count();
            result.put(code, count);
        }
        return result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, _) -> a, LinkedHashMap::new));
    }

    public List<Holiday> getCommonHolidays(int year, String firstCountry, String secondCountry) throws Exception {
        List<Holiday> firstHolidays = apiClient.getPublicHolidays(year, firstCountry);
        List<Holiday> secondHolidays = apiClient.getPublicHolidays(year, secondCountry);

        Map<String, Holiday> firstCountryHolidaysByDate = firstHolidays.stream()
                .collect(Collectors.toMap(Holiday::getDate, h -> h, (a, _) -> a));

        return secondHolidays.stream()
                .filter(h -> firstCountryHolidaysByDate.containsKey(h.getDate()))
                .map(h -> {
                    Holiday combined = new Holiday();
                    combined.setDate(h.getDate());
                    combined.setLocalName(firstCountryHolidaysByDate.get(h.getDate()).getLocalName() + " / " + h.getLocalName());
                    combined.setName(firstCountryHolidaysByDate.get(h.getDate()).getName() + " / " + h.getName());
                    return combined;
                })
                .collect(Collectors.toList());
    }
}
