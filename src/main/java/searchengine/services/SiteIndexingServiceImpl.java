package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.RequestSettings;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.StatusType;
import searchengine.model.WebSite;
import searchengine.repositories.IndexForSearchRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteIndexingServiceImpl implements SiteIndexingService {
    private final PageRepository pages;
    private final SiteRepository sites;
    private final SitesList sitesList;
    private final RequestSettings requestSettings;
    private AtomicBoolean isIndexing;
    private final LemmaRepository lemmas;
    private final IndexForSearchRepository indexes;
    @Override
    public void startIndexing(AtomicBoolean isIndexing) throws InterruptedException {
        this.isIndexing = isIndexing;
        List<Site> parentSites = new ArrayList<>(sitesList.getSites());
        List<WebSite> webSites = new ArrayList<>();
        for (Site element : parentSites) {
            WebSite site = new WebSite(StatusType.INDEXING, LocalDateTime.now(), null,
                element.getUrl(), element.getName());
            site.setUrl(element.getUrl());
            webSites.add(site);
        }
        deleteLemmasAndIndexes();
        deleteSitesAndPages();
        indexingSitePages(webSites);
        isIndexing.set(false);
    }

    public void indexingSitePages(List<WebSite> webSites) throws InterruptedException {
        List<Thread> indexingThreadList = new ArrayList<>();
        for (WebSite element : webSites) {
            Runnable siteIndexing = () -> {
                sites.save(element);
                MapOfSite mapOfSite = new MapOfSite(element.getUrl(), requestSettings);
                ConcurrentHashMap<String, CopyOnWriteArrayList<String>> sitesMap = new ConcurrentHashMap<>();
                RecursiveActionForMapOfSite recursiveActionForMapOfSite =
                        new RecursiveActionForMapOfSite(mapOfSite, sitesMap, pages, sites, element, requestSettings, isIndexing,
                                                        lemmas, indexes);
                try {
                    log.info("Запущена индексация сайта " + element.getUrl());
                    new ForkJoinPool().invoke(recursiveActionForMapOfSite);
                } catch (SecurityException se) {
                    log.error("Ошибка при индексации сайта " + element.getUrl());
                    element.setStatus(StatusType.FAILED);
                    sites.save(element);
                }
                if (isIndexing.get()) {
                    log.info("Сайт " + element.getUrl() + " проиндексирован");
                    element.setStatus(StatusType.INDEXED);
                    sites.save(element);
                } else {
                    log.info("Индексация остановлена пользователем");
                    element.setStatus(StatusType.FAILED);
                    sites.save(element);
                }
            };
            Thread thread = new Thread(siteIndexing);
            indexingThreadList.add(thread);
            thread.start();
        }
        for (Thread thread : indexingThreadList) {
            thread.join();
        }
        isIndexing.set(false);
    }

    public void deleteSitesAndPages() {
        pages.deleteAll();
        sites.deleteAll();
    }

    public void deleteLemmasAndIndexes() {
        indexes.deleteAll();
        lemmas.deleteAll();
    }

}
