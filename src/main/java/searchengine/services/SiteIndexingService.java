package searchengine.services;

import java.util.concurrent.atomic.AtomicBoolean;

public interface SiteIndexingService {

    void startIndexing(AtomicBoolean isIndexing) throws InterruptedException;
}
