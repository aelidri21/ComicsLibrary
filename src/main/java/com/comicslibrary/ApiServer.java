package com.comicslibrary;

import com.comicslibrary.api.ComicApiClient;
import com.comicslibrary.api.ComicVineClient;
import com.comicslibrary.model.Comic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.cdimascio.dotenv.Dotenv;

import static spark.Spark.*;

import java.util.List;

public class ApiServer {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String apiKey = dotenv.get("COMICVINE_API_KEY", System.getenv("COMICVINE_API_KEY"));
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Missing COMICVINE_API_KEY in .env or environment");
            System.err.println("   -> Create ./ComicsLibrary/.env with COMICVINE_API_KEY=...");
            System.exit(1);
        }
        System.out.println("COMICVINE_API_KEY loaded (length = " + apiKey.length() + ")");

        port(8080);

        final ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        final ComicApiClient client = new ComicVineClient(apiKey);

        get("/api/health", (req, res) -> {
            res.type("application/json");
            return "{\"status\":\"ok\"}";
        });

        get("/api/search", (req, res) -> {
            res.type("application/json");

            String q = req.queryParams("q");
            if (q == null || q.isBlank()) {
                res.status(400);
                return "{\"error\":\"Missing q\"}";
            }

            try {
                List<Comic> results = client.search(q);
                res.status(200);
                return mapper.writeValueAsString(results);
            } catch (IllegalArgumentException ise) {
                res.status(500);
                return "{\"error\":\"" + ise.getMessage().replace("\"", "\\\"") + "\"}";
            } catch (Exception e) {
                res.status(502);
                return "{\"error\":\"Upstream error: " + e.getMessage().replace("\"", "\\\"") + "\"}";
            }
        });

        System.out.println(" API running on http://localhost:8080 (GET /api/search?q=batman)");
    }
}
