package com.example.Dividends.scraper;

import com.example.Dividends.model.Company;
import com.example.Dividends.model.Dividend;
import com.example.Dividends.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
