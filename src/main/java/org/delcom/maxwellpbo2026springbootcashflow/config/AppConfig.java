package org.delcom.maxwellpbo2026springbootcashflow.config;

import org.delcom.maxwellpbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.maxwellpbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.maxwellpbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.maxwellpbo2026springbootcashflow.usecase.FinanceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root Spring: satu-satunya tempat yang merakit dependency antar-layer.
 * View cukup meminta bean lewat constructor, tanpa {@code new} tersebar.
 */
@Configuration
public class AppConfig {

    @Bean
    public ITransactionRepository transactionRepository() {
        return new TransactionRepository();
    }

    @Bean
    public FinanceUseCase financeUseCase(ITransactionRepository transactionRepository) {
        return new FinanceUseCase(transactionRepository);
    }

    @Bean
    public FinancePresenter financePresenter() {
        return new FinancePresenter();
    }
}
