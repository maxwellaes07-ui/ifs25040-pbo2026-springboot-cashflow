package org.delcom.maxwellpbo2026springbootcashflow;

import org.delcom.maxwellpbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.maxwellpbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.maxwellpbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke test: memastikan konteks Spring naik dan wiring Clean Architecture benar.
 */
@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private FinanceUseCase financeUseCase;

    @Autowired
    private FinancePresenter financePresenter;

    @Test
    void contextLoads_wiresAllBeans() {
        assertNotNull(transactionRepository);
        assertNotNull(financeUseCase);
        assertNotNull(financePresenter);
    }

    @Test
    void useCase_startsWithZeroBalance() {
        assertEquals("Saldo: Rp 0.00", financePresenter.formatBalance(financeUseCase.getBalance()));
    }

    @Test
    void main_delegatesToSpringApplicationRun() throws Exception {
        String[] args = {};
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            App.main(args);
            mocked.verify(() -> SpringApplication.run(App.class, args));
        }
    }
}
