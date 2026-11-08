package org.delcom.maxwellpbo2026springbootcashflow.usecase;

import org.delcom.maxwellpbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.maxwellpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.maxwellpbo2026springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinanceUseCaseTest {
    private FinanceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    private static BigDecimal money(String value) {
        return new BigDecimal(value).setScale(2);
    }

    // ---- tambah ----

    @Test
    void addIncome_storesTrimmedDescriptionAndScaledAmount() throws Exception {
        Transaction t = useCase.addIncome("  Gaji  ", new BigDecimal("1000000"));

        assertEquals("Gaji", t.getDescription());
        assertEquals(money("1000000"), t.getAmount());
        assertTrue(t.isIncome());
    }

    @Test
    void addExpense_storesExpense() throws Exception {
        Transaction t = useCase.addExpense("Makan", new BigDecimal("25000"));

        assertTrue(t.isExpense());
        assertEquals(1, useCase.getAllTransactions().size());
    }

    @Test
    void add_rejectsBlankOrNullDescription() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("   ", BigDecimal.TEN));
        assertThrows(ValidationException.class, () -> useCase.addExpense(null, BigDecimal.TEN));
    }

    @Test
    void add_rejectsNullZeroAndNegativeAmount() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("A", null));
        assertThrows(ValidationException.class, () -> useCase.addIncome("A", BigDecimal.ZERO));
        assertThrows(ValidationException.class, () -> useCase.addExpense("A", new BigDecimal("-5")));
        assertTrue(useCase.getAllTransactions().isEmpty());
    }

    // ---- parse ----

    @Test
    void parseAmount_acceptsPlainAndThousandSeparated() throws Exception {
        assertEquals(new BigDecimal("1000000"), useCase.parseAmount("1000000"));
        assertEquals(new BigDecimal("1000000.50"), useCase.parseAmount(" 1,000,000.50 "));
    }

    @Test
    void parseAmount_rejectsBlankAndNonNumeric() {
        assertThrows(ValidationException.class, () -> useCase.parseAmount(null));
        assertThrows(ValidationException.class, () -> useCase.parseAmount("  "));
        ValidationException ex = assertThrows(ValidationException.class, () -> useCase.parseAmount("abc"));
        assertEquals("Jumlah harus berupa angka", ex.getMessage());
    }

    // ---- hapus ----

    @Test
    void removeTransaction_deletesExisting() throws Exception {
        Transaction t = useCase.addIncome("Gaji", BigDecimal.TEN);

        useCase.removeTransaction(t.getId());

        assertTrue(useCase.getAllTransactions().isEmpty());
    }

    @Test
    void removeTransaction_unknownIdThrowsNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> useCase.removeTransaction(99));
        assertEquals(99, ex.getId());
    }

    @Test
    void removeTransaction_nonPositiveIdThrowsValidation() {
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(0));
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(-3));
    }

    // ---- cari ----

    @Test
    void searchTransactions_isCaseInsensitiveAndDoesNotChangeData() throws Exception {
        useCase.addIncome("Gaji Bulanan", new BigDecimal("100"));
        useCase.addExpense("Makan Siang", new BigDecimal("10"));

        List<Transaction> results = useCase.searchTransactions("  gAJI ");

        assertEquals(1, results.size());
        assertEquals("Gaji Bulanan", results.get(0).getDescription());
        assertEquals(2, useCase.getAllTransactions().size());
    }

    @Test
    void searchTransactions_blankOrNullReturnsAll() throws Exception {
        useCase.addIncome("A", BigDecimal.ONE);
        useCase.addIncome("B", BigDecimal.ONE);

        assertEquals(2, useCase.searchTransactions("").size());
        assertEquals(2, useCase.searchTransactions(null).size());
        assertTrue(useCase.searchTransactions("zzz").isEmpty());
    }

    // ---- urut ----

    @Test
    void sortTransactions_usesSortOptionComparator() throws Exception {
        useCase.addExpense("Besar", new BigDecimal("500"));
        useCase.addIncome("Kecil", new BigDecimal("100"));
        useCase.addIncome("Sedang", new BigDecimal("300"));

        assertEquals(List.of("Kecil", "Sedang", "Besar"), descriptions(useCase.sortTransactions(SortOption.AMOUNT_ASC)));
        assertEquals(List.of("Besar", "Sedang", "Kecil"), descriptions(useCase.sortTransactions(SortOption.AMOUNT_DESC)));
        assertEquals(List.of("Kecil", "Sedang", "Besar"), descriptions(useCase.sortTransactions(SortOption.INCOME_FIRST)));
        assertEquals(List.of("Besar", "Kecil", "Sedang"), descriptions(useCase.sortTransactions(SortOption.EXPENSE_FIRST)));
    }

    @Test
    void sortTransactions_nullOptionThrowsValidation() {
        assertThrows(ValidationException.class, () -> useCase.sortTransactions(null));
    }

    private static List<String> descriptions(List<Transaction> list) {
        return list.stream().map(Transaction::getDescription).toList();
    }

    // ---- saldo ----

    @Test
    void balance_isIncomeMinusExpense_andStaysConsistentAfterRemove() throws Exception {
        assertEquals(BigDecimal.ZERO, useCase.getBalance());

        useCase.addIncome("Gaji", new BigDecimal("5000000"));
        Transaction expense = useCase.addExpense("Sewa", new BigDecimal("25000"));

        assertEquals(money("5000000"), useCase.getTotalIncome());
        assertEquals(money("25000"), useCase.getTotalExpense());
        assertEquals(money("4975000"), useCase.getBalance());

        useCase.removeTransaction(expense.getId());

        assertEquals(money("5000000"), useCase.getBalance());
        assertEquals(BigDecimal.ZERO, useCase.getTotalExpense());
    }
}
