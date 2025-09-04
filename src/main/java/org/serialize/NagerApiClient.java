package org.serialize;

import org.serialize.model.Holiday;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NagerApiClient {
    private static final Logger logger = LoggerFactory.getLogger(NagerApiClient.class);
    private static final String BASE_URL = "https://date.nager.at/api/v3";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Cache<String, List<Holiday>> cache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(java.time.Duration.ofHours(1))
            .build();

    public List<Holiday> getPublicHolidays(int year, String countryCode) throws Exception {
        String key = year + "-" + countryCode;
        List<Holiday> holidays = cache.getIfPresent(key);
        if (holidays != null) {
            logger.info("Cache hit for key: {}", key);
            return holidays;
        }
        logger.info("Cache miss for key: {}. Fetching from API.", key);
        String url = BASE_URL + "/PublicHolidays/" + year + "/" + countryCode;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug("Received response: {}", response.body());
        holidays = mapper.readValue(response.body(), new TypeReference<>() {});
        cache.put(key, holidays);
        logger.info("Cached holidays for key: {}", key);
        return holidays;
    }
}