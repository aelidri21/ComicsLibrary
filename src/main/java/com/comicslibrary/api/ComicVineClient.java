package com.comicslibrary.api;

import com.comicslibrary.model.Comic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ComicVine API Client (https://comicvine.gamespot.com/api/)
 */
public class ComicVineClient implements ComicApiClient {

    private static final String BASE_URL = "https://comicvine.gamespot.com/api/search/";
    private static final String USER_AGENT = "ComicsLibrary/1.0 (+https://github.com/anas)";
    private static final int MAX_RESULTS = 10;

    private static final String RESOURCES = "volume,issue";
    private static final String FIELD_LIST =
            "id,name,deck,start_year,cover_date,publisher,person_credits,description,site_detail_url";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String apiKey;

    public ComicVineClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Missing COMICVINE_API_KEY");
        }
        this.apiKey = apiKey;
    }

    public ComicVineClient() {
        this(System.getenv("COMICVINE_API_KEY"));
    }

    @Override
    public List<Comic> search(String query) throws IOException, InterruptedException {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        String url = buildSearchUrl(query);
        HttpResponse<String> res = sendRequest(url);

        if (res.statusCode() != 200) {
            System.err.printf("ComicVine returned HTTP %d%n", res.statusCode());
            return Collections.emptyList();
        }

        return parseResults(res.body());
    }

    private String buildSearchUrl(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String encodedFields = URLEncoder.encode(FIELD_LIST, StandardCharsets.UTF_8);
        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        return BASE_URL + "?"
                + "api_key=" + encodedKey
                + "&format=json"
                + "&query=" + encodedQuery
                + "&resources=" + RESOURCES
                + "&field_list=" + encodedFields
                + "&limit=" + MAX_RESULTS;
    }

    private HttpResponse<String> sendRequest(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private List<Comic> parseResults(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode results = root.path("results");
        if (!results.isArray()) return Collections.emptyList();

        List<Comic> comics = new ArrayList<>();
        for (JsonNode item : results) {
            Comic comic = parseComic(item);
            if (comic != null) {
                comics.add(comic);
            }
        }
        return comics;
    }

    private Comic parseComic(JsonNode item) {
        String id = safeText(item, "id");
        String name = safeText(item, "name");
        if (id == null || name == null) return null;

        String deck = safeText(item, "deck");
        String desc = deck != null ? deck : safeText(item, "description");

        String publisher = null;
        if (item.has("publisher") && item.get("publisher").has("name")) {
            publisher = item.get("publisher").get("name").asText(null);
        }

        String publishedDate = safeText(item, "cover_date");
        if (publishedDate == null) {
            publishedDate = safeText(item, "start_year");
        }

        List<String> authors = new ArrayList<>();
        if (item.has("person_credits") && item.get("person_credits").isArray()) {
            for (JsonNode person : item.get("person_credits")) {
                String personName = safeText(person, "name");
                if (personName != null && !personName.isBlank()) {
                    authors.add(personName);
                }
            }
        }

        return new Comic(
                id,
                name,
                authors.isEmpty() ? null : authors,
                publisher,
                publishedDate,
                desc
        );
    }

    private static String safeText(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText(null)
                : null;
    }
}
