package org.delcom.maxwellpbo2026springbootcashflow.adapter.presenter;

import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancePresenterTest {
    private final FinancePresenter presenter = new FinancePresenter();
    private final Transaction income = new Transaction(1, "Gaji", new BigDecimal("1000000"),
            TransactionType.INCOME, LocalDateTime.of(2026, 5, 6, 7, 8));
    private final Transaction expense = new Transaction(2, "Makan", new BigDecimal("25000.5"),
            TransactionType.EXPENSE, LocalDateTime.of(2026, 5, 6, 9, 10));

    @Test
    void formatMoney_usesThousandSeparatorAndTwoDecimals() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(new BigDecimal("1000000")));
        assertEquals("Rp 0.00", presenter.formatMoney(BigDecimal.ZERO));
        assertEquals("Rp 12.35", presenter.formatMoney(new BigDecimal("12.345")));
        assertEquals("-Rp 1,500.00", presenter.formatMoney(new BigDecimal("-1500")));
    }

    @Test
    void format_matchesReferenceLine() {
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(income));
        assertEquals("[2] Makan | Rp 25,000.50 | [Pengeluaran]", presenter.format(expense));
    }

    @Test
    void formatBalance_andShowBalance() {
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(new BigDecimal("4975000")));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.showBalance(new BigDecimal("4975000")));
    }

    @Test
    void formatCreatedAt_usesDayMonthYear() {
        assertEquals("06/05/2026 07:08", presenter.formatCreatedAt(income));
    }

    @Test
    void labels_forTypeAndSortOptions() {
        assertEquals("Pemasukan", presenter.typeLabel(TransactionType.INCOME));
        assertEquals("Pengeluaran", presenter.typeLabel(TransactionType.EXPENSE));
        assertEquals("Jumlah terkecil", presenter.sortLabel(SortOption.AMOUNT_ASC));
        assertEquals("Jumlah terbesar", presenter.sortLabel(SortOption.AMOUNT_DESC));
        assertEquals("Pemasukan dulu", presenter.sortLabel(SortOption.INCOME_FIRST));
        assertEquals("Pengeluaran dulu", presenter.sortLabel(SortOption.EXPENSE_FIRST));
    }

    @Test
    void lists_showHeaderAndRowsOrEmptyMessage() {
        assertEquals("Daftar Transaksi:\n[1] Gaji | Rp 1,000,000.00 | [Pemasukan]",
                presenter.showTransactions(List.of(income)));
        assertEquals("Daftar Transaksi:\n- Data transaksi belum tersedia!",
                presenter.showTransactions(List.of()));
        assertEquals("Hasil Pencarian: \"x\"\n- Transaksi tidak ditemukan!",
                presenter.showSearchResults(List.of(), "x"));
        assertEquals("Daftar Transaksi (Terurut):\n[2] Makan | Rp 25,000.50 | [Pengeluaran]",
                presenter.showSortedTransactions(List.of(expense)));
        assertEquals("Daftar Transaksi (Terurut):\n- Data transaksi belum tersedia!",
                presenter.showSortedTransactions(List.of()));
    }

    @Test
    void messages_successAndFailure() {
        assertEquals("Berhasil menambah pemasukan: [1] Gaji | Rp 1,000,000.00 | [Pemasukan]",
                presenter.showAddIncomeSuccess(income));
        assertEquals("Berhasil menambah pengeluaran: [2] Makan | Rp 25,000.50 | [Pengeluaran]",
                presenter.showAddExpenseSuccess(expense));
        assertEquals("Berhasil menghapus transaksi.", presenter.showRemoveSuccess());
        assertEquals("[!] Gagal menghapus transaksi dengan ID: 9.", presenter.showRemoveFailed(9));
        assertEquals("[!] ID tidak valid!", presenter.showInvalidId());
        assertEquals("[!] Pilihan pengurutan tidak valid!", presenter.showInvalidSortOption());
        assertEquals("[!] Jumlah tidak boleh kosong", presenter.showValidationError("Jumlah tidak boleh kosong"));
    }
}
