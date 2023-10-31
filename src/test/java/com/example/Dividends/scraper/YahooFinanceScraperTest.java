package com.example.Dividends.scraper;

import com.example.Dividends.model.Company;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional
@SpringBootTest
class YahooFinanceScraperTest {
    private final Scraper scraper = new YahooFinanceScraper();

    @Test
    @DisplayName("티커로 배당금 스크랩하기")
    void scrapDividend() {
        var result = scraper.scrap(Company.builder().ticker("O").build());
        log.info(result.toString());
    }

    @Test
    @DisplayName("티커로 회사 이름 가져오기")
    void scrapCompanyTitle() {
        var result = scraper.scrapCompanyByTicker("MMM");
        log.info(result.toString());
    }
}