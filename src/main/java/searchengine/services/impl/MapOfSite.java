package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchengine.config.RequestSettings;
import searchengine.model.Page;
import searchengine.model.WebSite;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class MapOfSite {
    private static final Logger log = LoggerFactory.getLogger(MapOfSite.class);
    private String parentSite;
    private final RequestSettings requestSettings;

    public MapOfSite(String parentSite, RequestSettings requestSettings) {
        this.parentSite = parentSite;
        this.requestSettings = requestSettings;
    }

    public String getParentSite() {
        return parentSite;
    }

    public Document getHtmlCode(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(requestSettings.getUserAgent())
                .referrer(requestSettings.getReferrer())
                .timeout(30 * 1000)
                .ignoreContentType(true)
                .get();
    }

    public ConcurrentHashMap<String, Page> getChildSites(String url, WebSite parentSite) throws InterruptedException {
        ConcurrentHashMap<String, Page> sites = new ConcurrentHashMap<>();
        Thread.sleep(150);
        try {
            Document htmlCode = this.getHtmlCode(url);
            Elements elements = htmlCode.select("a");
            String regexp = "/.+";
            String regexpPicture = "(?i)/.+[.]((png)|(jpg)|(jpeg))";

            for (Element element : elements) {
                if (element.hasAttr("href") && element.attribute("href").getValue().matches(regexp)
                    && !element.attribute("href").getValue().matches(regexpPicture)) {
                    Document htmlChild = getHtmlCode(element.absUrl("href"));
                    Page page = new Page(parentSite, element.attr("href"), htmlChild.connection().response().statusCode(),
                            htmlChild.head() + String.valueOf(htmlChild.body()));
                    sites.put(element.absUrl("href"), page);
                }
            }
        } catch (IOException io) {
            log.error(io.getMessage());
        }
        return sites;
    }
}
