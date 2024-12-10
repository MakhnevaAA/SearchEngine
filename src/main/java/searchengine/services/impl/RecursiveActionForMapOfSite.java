package searchengine.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchengine.config.RequestSettings;
import searchengine.model.IndexForSearch;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.WebSite;
import searchengine.repositories.IndexForSearchRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecursiveActionForMapOfSite extends RecursiveAction {
    private static final Logger log = LoggerFactory.getLogger(RecursiveActionForMapOfSite.class);
    private MapOfSite mapOfSite;
    private static CopyOnWriteArrayList<String> pagesList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<String> childPages = new CopyOnWriteArrayList<>();
    private static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> sitesMap;
    private final PageRepository pages;
    private final SiteRepository sites;
    private final WebSite parentSite;
    private final RequestSettings requestSettings;
    private AtomicBoolean isIndexing;
    private final LemmaRepository lemmas;
    private final IndexForSearchRepository indexes;
    private final LemmaFinder lemmaFinder = new LemmaFinder();

    public RecursiveActionForMapOfSite(MapOfSite mapOfSite, ConcurrentHashMap<String,CopyOnWriteArrayList<String>> sitesMap,
                                       PageRepository pages, SiteRepository sites, WebSite parentSite,
                                       RequestSettings requestSettings, AtomicBoolean isIndexing, LemmaRepository lemmas, IndexForSearchRepository indexes) {
        this.mapOfSite = mapOfSite;
        RecursiveActionForMapOfSite.sitesMap = sitesMap;
        this.pages = pages;
        this.sites = sites;
        this.parentSite = parentSite;
        this.requestSettings = requestSettings;
        this.isIndexing = isIndexing;
        this.lemmas = lemmas;
        this.indexes = indexes;
    }

    @Override
    protected void compute() {
        try {
            if (!isIndexing.get()) {
                return;
            }
            if (!pagesList.contains(mapOfSite.getParentSite())) {
                pagesList.add(mapOfSite.getParentSite());
            }
            ConcurrentHashMap<String, Page> listSites = mapOfSite.getChildSites(mapOfSite.getParentSite(), parentSite);
            for (Map.Entry<String, Page> page: listSites.entrySet()) {
                if (!isIndexing.get()) {
                    return;
                }
                if (!pagesList.contains(page.getKey())) {
                    pagesList.add(page.getKey());
                    childPages.add(page.getKey());
                    pages.save(page.getValue());
                    if (page.getValue().getCode() < 300) {
                        ConcurrentHashMap<String, Integer> mapLemmas = lemmaFinder.findAllLemmas(page.getValue().getContent());
                        mapLemmas.forEach((lemma, count) -> {
                            Lemma lemmaInRepository = saveLemma(lemma, page.getValue());
                            saveIndex(count, lemmaInRepository, page.getValue());
                        });
                    }
                    parentSite.setStatusTime(LocalDateTime.now());
                    sites.save(parentSite);
                }
                sitesMap.put(mapOfSite.getParentSite(), childPages);
            }
            List<RecursiveActionForMapOfSite> actions = new ArrayList<>();
            for (String site : childPages){
                MapOfSite newMap = new MapOfSite(site, requestSettings);
                RecursiveActionForMapOfSite action = new RecursiveActionForMapOfSite(newMap, sitesMap, pages, sites, parentSite, requestSettings, isIndexing, lemmas, indexes);
                action.fork();
                actions.add(action);
            }
            for (RecursiveActionForMapOfSite a : actions) {
                if (!isIndexing.get()) {
                    return;
                }
                a.join();
            }
        } catch (InterruptedException ie){
            log.error(ie.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public Lemma saveLemma(String lemma, Page indexingPage) {
        Lemma lemmaInRepository = lemmas.getLemmaInSite(lemma, indexingPage.getSite().getId());
        if (lemmaInRepository != null) {
            lemmaInRepository.setFrequency(lemmaInRepository.getFrequency() + 1);
        } else {
            lemmaInRepository = new Lemma(indexingPage.getSite(), lemma, 1);
        }
        lemmas.save(lemmaInRepository);
        return lemmaInRepository;
    }

    public void saveIndex(float count, Lemma lemmaInRepository, Page indexingPage) {
        IndexForSearch indexInRepository = indexes.indexExist(indexingPage.getId(), lemmaInRepository.getId());
        if (indexInRepository != null) {
            indexInRepository.setRank(indexInRepository.getRank() + count);
        } else {
            indexInRepository = new IndexForSearch(indexingPage, lemmaInRepository, count);
        }
        indexes.save(indexInRepository);
    }
}
