package com.comicslibrary.model;

import java.util.List;
import java.util.Objects;

public class Comic {
    private final String id;
    private final String title;
    private final List<String> authors;
    private final String publisher;
    private final String publishedDate;
    private final String description;

    public Comic(String id, String title, List<String> authors, String publisher, String publishedDate, String description) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public List<String> getAuthors() { return authors; }
    public String getPublisher() { return publisher; }
    public String getPublishedDate() { return publishedDate; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        String a = (authors == null || authors.isEmpty()) ? "N/A" : String.join(", ", authors);
        String date = (publishedDate == null || publishedDate.isBlank()) ? "N/A" : publishedDate;
        return "[" + (id == null ? "N/A" : id) + "] " + (title == null ? "Sans titre" : title)
                + " — " + a + " (" + date + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comic)) return false;
        Comic comic = (Comic) o;
        return Objects.equals(id, comic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

