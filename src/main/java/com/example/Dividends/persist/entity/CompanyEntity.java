package com.example.Dividends.persist.entity;

import com.example.Dividends.model.Company;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "COMPANY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String ticker;
    private String name;

    public CompanyEntity(Company company) {
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
