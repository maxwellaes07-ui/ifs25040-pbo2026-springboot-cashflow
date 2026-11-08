package org.delcom.maxwellpbo2026springbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortOptionTest {
    private final Transaction a = new Transaction(1, "A", new BigDecimal("300"), TransactionType.EXPENSE);
    private final Transaction b = new Transaction(2, "B", new BigDecimal("100"), TransactionType.INCOME);
    private final Transaction c = new Transaction(3, "C", new BigDecimal("200"), TransactionType.INCOME);
    private final Transaction d = new Transaction(4, "D", new BigDecimal("100"), TransactionType.EXPENSE);

    private List<Integer> ids(SortOption option) {
        return List.of(a, b, c, d).stream().sorted(option.comparator()).map(Transaction::getId).toList();
    }

    @Test
    void amountAsc_smallestFirst_tieBrokenById() {
        assertEquals(List.of(2, 4, 3, 1), ids(SortOption.AMOUNT_ASC));
    }

    @Test
    void amountDesc_largestFirst() {
        assertEquals(List.of(1, 3, 2, 4), ids(SortOption.AMOUNT_DESC));
    }

    @Test
    void incomeFirst_putsIncomeBeforeExpense() {
        assertEquals(List.of(2, 3, 1, 4), ids(SortOption.INCOME_FIRST));
    }

    @Test
    void expenseFirst_putsExpenseBeforeIncome() {
        assertEquals(List.of(1, 4, 2, 3), ids(SortOption.EXPENSE_FIRST));
    }

    @Test
    void hasFourOptions() {
        assertEquals(4, SortOption.values().length);
    }
}
