package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.dto.FinderPageDto;
import searchengine.dto.RelevanceDto;
import searchengine.dto.BadResponse;
import searchengine.dto.SearchGoodResponse;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.StatusType;
import searchengine.model.WebSite;
import searchengine.repositories.IndexForSearchRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService{
    private final LemmaFinder lemmaFinder;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexForSearchRepository indexForSearchRepository;
    @Override
    public ResponseEntity<Object> search(String query, String site, int offset, int limit) {
        if (query.isEmpty()) {
            log.error("Передан пустой поисковый запрос");
            return ResponseEntity.badRequest().body(new BadResponse(false, "Ошибка поиска. Передан пустой поисковый запрос"));
        }
        WebSite siteForSearch = siteRepository.findByUrl(site);

        if (siteForSearch != null && !siteForSearch.getStatus().equals(StatusType.INDEXED)) {
            log.error("Сайт не проиндексирован");
            return ResponseEntity.badRequest().body(new BadResponse(false, "Ошибка поиска. Сайт не проиндексирован"));
        }

        List<FinderPageDto> finderPages;
        List<RelevanceDto> pagesRelevance;
        List<Page> findingPages = new ArrayList<>();
        List<FinderPageDto> result = new ArrayList<>();

        try {
            List<String> lemmasInQuery = lemmaFinder.findAllLemmas(query).keySet().stream().toList();
            List<Lemma> lemmasForSearch = filterLemmasInQuery(lemmasInQuery, siteForSearch);
            if (lemmasForSearch.isEmpty()) {
                return ResponseEntity.ok(new SearchGoodResponse(true, 0, new ArrayList<>()));
            }
            if(lemmasForSearch.size() > 1) {
                Collections.sort(lemmasForSearch, (o1, o2) -> {
                    if (o1.getFrequency() < o2.getFrequency()) {
                        return -1;
                    } else if (o1.getFrequency() > o2.getFrequency()) {
                        return 1;
                    } else return 0;
                });
            }
            if (siteForSearch != null) {
                findingPages = pageRepository.findBySiteId(siteForSearch.getId());
                findPagesWithLemmas(findingPages, lemmasForSearch, lemmasInQuery.size());
            } else {
                for (WebSite webSite : siteRepository.findIndexed()) {
                    List<Page> pagesWithLemmas = pageRepository.findBySiteId(webSite.getId());
                    findPagesWithLemmas(pagesWithLemmas, lemmasForSearch, lemmasInQuery.size());
                    findingPages.addAll(pagesWithLemmas);
                }
            }
            pagesRelevance = setRelevance(findingPages, lemmasForSearch);
            Collections.sort(pagesRelevance, (o1, o2) ->  {
                        if (o1.getRelativeRelevance() > o2.getRelativeRelevance()) {
                            return -1;
                        } else if (o1.getRelativeRelevance() < o2.getRelativeRelevance()) {
                            return 1;
                        } else return 0;
                    } );
            finderPages = createFinderPagesList(pagesRelevance);
            for (int i = offset; i < offset + limit; i++) {
                if (i >= finderPages.size()) {
                    break;
                }
                finderPages.get(i).setSnippet(lemmaFinder.getSnippet(pagesRelevance.get(i).getPage().getContent(), lemmasInQuery));
                result.add(finderPages.get(i));
            }
        } catch (IOException io) {
            log.error(io.getMessage());
        }
        return ResponseEntity.ok(new SearchGoodResponse(true, findingPages.size(), result));
    }

    public List<Lemma> filterLemmasInQuery(List<String> lemmasInQuery, WebSite siteForSearch) {
        List<Lemma> lemmasForSearch = new ArrayList<>();
        long countPages = 0;
        if (siteForSearch == null) {
            List<WebSite> indexedSites = siteRepository.findIndexed();
            for (WebSite site : indexedSites) {
                countPages += pageRepository.getCountPagesOnSite(site.getId());
            }
            for (String lemma : lemmasInQuery) {
                boolean isLemmaExist = lemmaRepository.lemmaExist(lemma) != null;
                Optional<Integer> countLemmaOnIndexedSite = lemmaRepository.countLemmaOnIndexedSite(lemma);
                if (countLemmaOnIndexedSite.isEmpty()) {
                    return new ArrayList<>();
                }
                if (isLemmaExist) {
                    siteRepository.findIndexed().forEach(webSite -> {
                        Lemma lemmaOnSite = lemmaRepository.getLemmaInSite(lemma, webSite.getId());
                        if (lemmaOnSite != null) {
                            lemmasForSearch.add(lemmaOnSite);
                        }
                    });
                }
            }
        } else {
            for (String lemma : lemmasInQuery) {
                boolean isLemmaExist = lemmaRepository.getLemmaInSite(lemma, siteForSearch.getId()) != null;
                if (isLemmaExist) {
                    lemmasForSearch.add(lemmaRepository.getLemmaInSite(lemma,siteForSearch.getId()));
                }
            }
        }
        return lemmasForSearch;
    }

    public void findPagesWithLemmas(List<Page> findingPages, List<Lemma> lemmasForSearch, int requiredCountLemmasOnPage) {
        AtomicInteger countLemmasOnPage = new AtomicInteger();
        lemmasForSearch.forEach(lemma -> {
            if (!findingPages.isEmpty() && lemma.getSite().getId() == findingPages.get(0).getSite().getId()) {
                List<Page> intermediateListPages = new ArrayList<>();
                findingPages.forEach(page -> {
                    if (indexForSearchRepository.indexExist(page.getId(), lemma.getId()) != null) {
                        intermediateListPages.add(page);
                    }
                });
                if (!intermediateListPages.isEmpty()) {
                    countLemmasOnPage.getAndIncrement();
                }
                findingPages.clear();
                findingPages.addAll(intermediateListPages);
            }
        });
        if (countLemmasOnPage.get() != requiredCountLemmasOnPage) {
            findingPages.clear();
        }
    }

    public List<RelevanceDto> setRelevance(List<Page> findingPages, List<Lemma> lemmasForSearch) {
        List<RelevanceDto> pagesRelevance = new ArrayList<>();
        AtomicReference<Float> maxAbsolutRelevance = new AtomicReference<>(0f);
        findingPages.forEach(page -> {
            RelevanceDto relevanceDto = new RelevanceDto();
            relevanceDto.setPage(page);
            lemmasForSearch.forEach(lemma -> {
                if (lemma.getSite().getId() == page.getSite().getId()) {
                    relevanceDto.setAbsolutRelevance(relevanceDto.getAbsolutRelevance() +
                            indexForSearchRepository.indexExist(page.getId(), lemma.getId()).getRank());
                }
            });
            if (maxAbsolutRelevance.get() < relevanceDto.getAbsolutRelevance()) {
                maxAbsolutRelevance.set(relevanceDto.getAbsolutRelevance());
            }
            pagesRelevance.add(relevanceDto);
        });
        pagesRelevance.forEach(relevancePage -> relevancePage.setRelativeRelevance(relevancePage.getAbsolutRelevance()
                / maxAbsolutRelevance.get()));
        return pagesRelevance;
    }

    public List<FinderPageDto> createFinderPagesList(List<RelevanceDto> pagesRelevance) throws IOException {
        List<FinderPageDto> finderPages = new ArrayList<>();
        for (RelevanceDto page : pagesRelevance) {
            Document doc = Jsoup.parse(page.getPage().getContent());
            FinderPageDto finderPage = new FinderPageDto();
            finderPage.setUri(page.getPage().getPath());
            finderPage.setRelevance(page.getRelativeRelevance());
            finderPage.setTitle(doc.title());
            finderPage.setSnippet("");
            finderPage.setSite(siteRepository.findById(page.getPage().getSite().getId()).get().getUrl());
            finderPage.setSiteName(siteRepository.findById(page.getPage().getSite().getId()).get().getName());
            finderPages.add(finderPage);
        }
        return  finderPages;
    }
}
