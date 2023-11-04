package com.example.Dividends.service;

import com.example.Dividends.exception.impl.NoCompanyException;
import com.example.Dividends.model.Company;
import com.example.Dividends.model.Dividend;
import com.example.Dividends.model.ScrapedResult;
import com.example.Dividends.model.constants.CacheKey;
import com.example.Dividends.persist.CompanyRepository;
import com.example.Dividends.persist.DividendRepository;
import com.example.Dividends.persist.entity.CompanyEntity;
import com.example.Dividends.persist.entity.DividendEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);

        // 회사명을 기준으로 회사 정보를 조회
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        // 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntityList = dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 결과 조합 후 반환
        List<Dividend> dividendList = dividendEntityList.stream()
                .map(e -> Dividend.builder()
                        .dividend(e.getDividend())
                        .date(e.getDate())
                        .build())
                .toList();

        return new ScrapedResult(Company.builder()
                .ticker(companyEntity.getTicker())
                .name(companyEntity.getName())
                .build(), dividendList);
    }
}
