package com.example.Dividends.scheduler;

import com.example.Dividends.model.Company;
import com.example.Dividends.model.ScrapedResult;
import com.example.Dividends.model.constants.CacheKey;
import com.example.Dividends.persist.CompanyRepository;
import com.example.Dividends.persist.DividendRepository;
import com.example.Dividends.persist.entity.CompanyEntity;
import com.example.Dividends.persist.entity.DividendEntity;
import com.example.Dividends.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // 스케쥴링이 동작할때마다
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    // 스프링 배치
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companyEntityList = companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companyEntityList) {
            log.info("scraping is started -> " + company.getName());

            ScrapedResult result = yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());

            AtomicInteger count = new AtomicInteger();
            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            result.getDividendEntities().stream()
                    // 디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        if (!dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate())) {
                            count.getAndIncrement();
                            dividendRepository.save(e);
                        }
                    });

            log.info("scraping is ended -> " + company.getName() + " updated count -> " + count);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("scraping scheduler is ended");
    }
}
