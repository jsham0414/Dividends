package com.example.Dividends.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 금융 서비스를 위한 컨트롤러
 */

@RestController
@RequestMapping("/finance")
public class FinanceController {
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        return null;
    }
}
