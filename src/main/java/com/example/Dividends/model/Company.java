package com.example.Dividends.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    private String ticker;
    private String name;
}
