package com.example.Dividends.service;

import com.example.Dividends.exception.impl.NoCompanyException;
import com.example.Dividends.model.Company;
import com.example.Dividends.model.ScrapedResult;
import com.example.Dividends.persist.CompanyRepository;
import com.example.Dividends.persist.DividendRepository;
import com.example.Dividends.persist.entity.CompanyEntity;
import com.example.Dividends.persist.entity.DividendEntity;
import com.example.Dividends.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {
    private final Trie<String, String> trie;
    private final Scraper scraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public Company addCompany(Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }

        Company company = save(ticker);
        addAutocompleteKeyword(company.getName());

        return company;
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

    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<CompanyEntity> autocomplete(String keyword) {
        List<String> companyList = trie.prefixMap(keyword).keySet().stream().toList();
        return companyList.stream().map(c -> companyRepository.findByName(c)
                .orElseThrow(NoCompanyException::new)).toList();
    }

    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }


    public List<String> getCompanyNamesByKeyword(String keyword, Pageable pageable) {
        return companyRepository.findByNameStartingWithIgnoreCase(keyword, pageable)
                .stream()
                .map(CompanyEntity::getName)
                .toList();
    }

    public String deleteCompany(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        dividendRepository.deleteAllByCompanyId(companyEntity.getId());
        companyRepository.delete(companyEntity);

        deleteAutocompleteKeyword(companyEntity.getName());

        return companyEntity.getName();
    }

}
