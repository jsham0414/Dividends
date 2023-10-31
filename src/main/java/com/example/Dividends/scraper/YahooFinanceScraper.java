package com.example.Dividends.scraper;

import com.example.Dividends.model.Company;
import com.example.Dividends.model.Dividend;
import com.example.Dividends.model.ScrapedResult;
import com.example.Dividends.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMERY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; // 60 * 60 * 24

    @Override
    public ScrapedResult scrap(Company company) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000; // 1970 1 1로부터 흐른 시간

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDividends = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableElement = parsingDividends.get(0);
            Element tbody = tableElement.children().get(1);

            List<Dividend> dividendList = new ArrayList<>();
            for (Element e : tbody.children()) {
                String text = e.text();
                if (!text.endsWith("Dividend")) {
                    continue;
                }

                dividendList.add(reformData(text));
            }

            scrapedResult.setDividendEntities(dividendList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return scrapedResult;
    }

    private Dividend reformData(String data) {
        String[] splits = data.split(" ");
        int month = Month.strToNumber(splits[0]);
        if (month < 0) {
            throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
        }

        int day = Integer.parseInt(splits[1].replace(",", ""));
        int year = Integer.parseInt(splits[2]);
        String dividend = splits[3];

        return Dividend.builder()
                .date(LocalDateTime.of(year, month, day, 0, 0))
                .dividend(dividend)
                .build();
    }

    // 티커로 회사명을 가져온다.
    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = SUMMERY_URL.formatted(ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleElement = document.getElementsByTag("h1").get(0);
            // abc - def - ghi -> def
            String title = titleElement.text().split(" \\(")[0].trim();

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
