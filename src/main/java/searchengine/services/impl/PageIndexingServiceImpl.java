package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.config.RequestSettings;
import searchengine.model.*;
import searchengine.repositories.IndexForSearchRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.PageIndexingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageIndexingServiceImpl implements PageIndexingService {
    private final LemmaFinder lemmaFinder;
    private final LemmaRepository lemmas;
    private final IndexForSearchRepository indexes;
    private final PageRepository pageRepository;
    private final RequestSettings requestSettings;
    private final SiteRepository siteRepository;

    @Override
    public void indexPage(String url, AtomicReference<String> siteName, AtomicReference<String> siteUrl) throws IOException{
        WebSite parentSite = siteRepository.findByUrl(siteUrl.get());
        if (parentSite == null) {
            parentSite = saveParentSite(siteUrl.get(), siteName.get());
        }
            Page indexingPage = saveIndexingPage(siteUrl.get(), url, parentSite);
            ConcurrentHashMap<String, Integer> allLemmas = lemmaFinder.findAllLemmas(indexingPage.getContent());
            allLemmas.forEach((lemma, count) -> {
                Lemma lemmaInRepository = saveLemma(lemma, indexingPage);
                saveIndex(count, lemmaInRepository, indexingPage);
            });
    }

    public Page saveIndexingPage(String siteUrl, String url, WebSite parentSite) throws IOException {
        MapOfSite mapOfSite = new MapOfSite(url, requestSettings);
        int sizeParentSite = siteUrl.length();
        String pathIndexingPage = url.substring(sizeParentSite);
        Page indexingPage = pageRepository.findByPathAndSiteId(pathIndexingPage, parentSite.getId());
        Document html = mapOfSite.getHtmlCode(url);
        String content = html.head() + String.valueOf(html.body());
        if (indexingPage != null) {
            clearInfoAboutPage(indexingPage);
        }
        indexingPage = new Page(parentSite, pathIndexingPage, html.connection().response().statusCode(), content);
        pageRepository.save(indexingPage);
        return indexingPage;
    }

    public void clearInfoAboutPage(Page indexingPage) {
        List<IndexForSearch> allIndexes = indexes.findAllByPageId(indexingPage.getId());
        allIndexes.forEach(idx -> {
            Optional<Lemma> lemmaToRefresh = lemmas.findById(idx.getLemma().getId());
            lemmaToRefresh.ifPresent(lemma -> {
                lemma.setFrequency(lemma.getFrequency() - 1);
                lemmas.saveAndFlush(lemma);
            });
        });
        indexes.deleteAllByPageId(indexingPage.getId());
        pageRepository.deleteById(indexingPage.getId());
    }

    public WebSite saveParentSite(String siteUrl, String siteName) {
        WebSite parentSite = new WebSite(StatusType.INDEXING, LocalDateTime.now(), null, siteUrl, siteName);
        siteRepository.save(parentSite);
        return  parentSite;
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
