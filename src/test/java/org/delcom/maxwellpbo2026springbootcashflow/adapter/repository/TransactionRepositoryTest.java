package org.delcom.maxwellpbo2026springbootcashflow.adapter.repository;

import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRepositoryTest {
    private TransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();
    }

    @Test
    void saveIncomeAndExpense_assignIncrementingIds() {
        Transaction first = repository.saveIncome("Gaji", new BigDecimal("100"));
        Transaction second = repository.saveExpense("Makan", new BigDecimal("10"));

        assertEquals(1, first.getId());
        assertEquals(TransactionType.INCOME, first.getType());
        assertEquals(2, second.getId());
        assertEquals(TransactionType.EXPENSE, second.getType());
    }

    @Test
    void findAll_returnsDefensiveCopy() {
        repository.saveIncome("Gaji", new BigDecimal("100"));

        List<Transaction> copy = repository.findAll();
        copy.clear();

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findById_returnsMatchOrEmpty() {
        Transaction saved = repository.saveIncome("Gaji", new BigDecimal("100"));

        assertEquals(saved, repository.findById(1).orElseThrow());
        assertTrue(repository.findById(99).isEmpty());
    }

    @Test
    void deleteById_removesOnlyExisting() {
        repository.saveIncome("Gaji", new BigDecimal("100"));

        assertTrue(repository.deleteById(1));
        assertFalse(repository.deleteById(1));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findBy_filtersWithPredicate() {
        repository.saveIncome("Gaji", new BigDecimal("100"));
        repository.saveExpense("Makan", new BigDecimal("10"));

        List<Transaction> expenses = repository.findBy(Transaction::isExpense);

        assertEquals(1, expenses.size());
        assertEquals("Makan", expenses.get(0).getDescription());
    }

    @Test
    void update_keepsReferenceChanges() {
        Transaction saved = repository.saveIncome("Gaji", new BigDecimal("100"));
        saved.changeDescription("Bonus");

        repository.update(saved);

        assertEquals("Bonus", repository.findById(1).orElseThrow().getDescription());
    }
}
