package org.serialize;

import org.serialize.model.Holiday;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NagerApiClient {
    private static final String BASE_URL = "https://date.nager.at/api/v3";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Holiday> getPublicHolidays(int year, String countryCode) throws Exception {
        String url = BASE_URL + "/PublicHolidays/" + year + "/" + countryCode;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<>() {
        });
    }
}