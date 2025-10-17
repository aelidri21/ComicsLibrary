package com.comicslibrary;

import com.comicslibrary.api.ComicApiClient;
import com.comicslibrary.api.ComicVineClient;
import com.comicslibrary.model.Comic;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ComicApiClient api = new ComicVineClient();

        if (args == null || args.length == 0) {
            System.out.println("Usage: ./gradlew run --args=\"<keyword>\"");
            System.out.println("Example: ./gradlew run --args=\"Batman\"");
            return;
        }

        String query = String.join(" ", args).trim();
        if (query.isEmpty()) {
            System.out.println("No query provided.");
            return;
        }

        System.out.println("🔎 Searching ComicVine for: " + query);
        try {
            List<Comic> results = api.search(query);
            if (results.isEmpty()) {
                System.out.println("No results found.");
                return;
            }

            for (int i = 0; i < results.size(); i++) {
                System.out.printf("%2d) %s%n", i + 1, results.get(i));
            }
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
