package com.example.Dividends.service;

import com.example.Dividends.model.Company;
import com.example.Dividends.model.ScrapedResult;
import com.example.Dividends.persist.CompanyRepository;
import com.example.Dividends.persist.DividendRepository;
import com.example.Dividends.persist.entity.CompanyEntity;
import com.example.Dividends.persist.entity.DividendEntity;
import com.example.Dividends.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final Scraper scraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        return storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 배당금 스크래핑
        Company company = scraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap to ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = scraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = companyRepository.save(CompanyEntity.builder()
                .name(company.getName())
                .ticker(ticker)
                .build());

        List<DividendEntity> entityList = scrapedResult.getDividendEntities()
                .stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .toList();

        dividendRepository.saveAll(entityList);
        return company;
    }

}
