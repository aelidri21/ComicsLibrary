package com.comicslibrary.api;

import com.comicslibrary.model.Comic;
import java.io.IOException;
import java.util.List;

public interface ComicApiClient {
    List<Comic> search(String query) throws IOException, InterruptedException;
}

