package com.comicslibrary.model;

import java.util.List;
public record Comic(
        String id,
        String title,
        List<String> authors,
        String publisher,
        String publishedDate,
        String description,
        String coverUrl
) {

    public Comic {
        id = normalize(id);
        title = normalize(title);
        publisher = normalize(publisher);
        publishedDate = normalize(publishedDate);
        description = normalize(description);
        coverUrl = normalize(coverUrl); 
        authors = (authors == null) ? List.of() : List.copyOf(authors);
    }

    private static String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    @Override
    public String toString() {
        String a = authors.isEmpty() ? "N/A" : String.join(", ", authors);
        String date = (publishedDate == null || publishedDate.isBlank()) ? "N/A" : publishedDate;
        String idStr = (id == null) ? "N/A" : id;
        String titleStr = (title == null) ? "Sans titre" : title;
        return "[" + idStr + "] " + titleStr + " — " + a + " (" + date + ")";
    }
}
