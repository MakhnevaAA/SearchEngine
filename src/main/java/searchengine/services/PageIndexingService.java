package searchengine.services;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public interface PageIndexingService {
    void indexPage(String url, AtomicReference<String> siteName, AtomicReference<String> siteUrl) throws IOException;
}
