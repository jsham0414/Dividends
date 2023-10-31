package com.example.Dividends.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
@Setter
public class Dividend {
    private LocalDateTime date;
    private String dividend;
}
