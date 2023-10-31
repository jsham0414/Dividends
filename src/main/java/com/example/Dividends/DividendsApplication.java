package com.example.Dividends;

import com.example.Dividends.model.Company;
import com.example.Dividends.model.Dividend;
import com.example.Dividends.model.ScrapedResult;
import com.example.Dividends.model.constants.Month;
import com.example.Dividends.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DividendsApplication {
	// cron을 사용해서 1시간마다 검색량을 db에 저장한다.

	public static void main(String[] args) {
		SpringApplication.run(DividendsApplication.class, args);
	}

}
