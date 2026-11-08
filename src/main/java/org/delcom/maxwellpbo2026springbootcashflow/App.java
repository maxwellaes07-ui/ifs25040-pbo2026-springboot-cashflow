package org.delcom.maxwellpbo2026springbootcashflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point aplikasi Cashflow.
 * File .env dimuat terlebih dahulu agar APP_PORT terbaca oleh application.yaml.
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) throws Exception {
        DotEnv.load();
        SpringApplication.run(App.class, args);
    }
}
