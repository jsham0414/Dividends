package com.example.Dividends.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 스크랩 해 온 결과를 주고 받기 위한 클래스
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ScrapedResult {
    private Company company;
    private List<Dividend> dividendEntities;

    public ScrapedResult() {
        dividendEntities = new ArrayList<>();
    }
}
