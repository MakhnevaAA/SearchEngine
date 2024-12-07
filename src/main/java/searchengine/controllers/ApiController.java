package searchengine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.config.SitesList;
import searchengine.dto.BadResponse;
import searchengine.dto.GoodResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.PageIndexingService;
import searchengine.services.SearchService;
import searchengine.services.SiteIndexingService;
import searchengine.services.StatisticsService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final StatisticsService statisticsService;
    private final SiteIndexingService siteIndexingService;
    private final PageIndexingService pageIndexingService;
    private final SitesList sitesList;
    private AtomicBoolean isIndexing = new AtomicBoolean(false);
    private final SearchService searchService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ApiController(StatisticsService statisticsService, SiteIndexingService siteIndexingService,
                         SitesList sitesList, PageIndexingService pageIndexingService, SearchService searchService) {
        this.statisticsService = statisticsService;
        this.siteIndexingService = siteIndexingService;
        this.sitesList = sitesList;
        this.pageIndexingService = pageIndexingService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity startIndexing() throws InterruptedException {
        if (!isIndexing.get()) {
            executor.submit(() -> {
                isIndexing.set(true);
                try {
                    siteIndexingService.startIndexing(isIndexing);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            return ResponseEntity.status(HttpStatus.OK).body(new GoodResponse(true));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new BadResponse(false, "Индексация уже запущена"));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity stopIndexing() {
        if (isIndexing.get()) {
            isIndexing.set(false);
            return ResponseEntity.status(HttpStatus.OK).body(new GoodResponse(true));
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new BadResponse(false, "Индексация не запущена"));
    }

    @PostMapping("/indexPage")
    public ResponseEntity indexPage(@RequestParam String url) {
        AtomicReference<String> siteName = new AtomicReference<>();
        AtomicReference<String> siteUrl = new AtomicReference<>();
        sitesList.getSites().forEach(site -> {
            if (url.contains(site.getUrl())) {
                siteName.set(site.getName());
                siteUrl.set(site.getUrl());
            }
        });
        if (siteName.get() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(false, "Данная страница находится за пределами сайтов, указанных в конфигурационном файле"));
        }
        try {
            pageIndexingService.indexPage(url, siteName, siteUrl);
        } catch (IOException io) {
            log.error(io.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadResponse(false, "Страницы не существует"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new GoodResponse(true));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(required = false) String query,
                                          @RequestParam(required = false, defaultValue = "all") String site,
                                          @RequestParam(required = false, defaultValue = "0") int offset,
                                          @RequestParam(required = false, defaultValue = "20") int limit) {
        return searchService.search(query, site, offset, limit);
    }
}
