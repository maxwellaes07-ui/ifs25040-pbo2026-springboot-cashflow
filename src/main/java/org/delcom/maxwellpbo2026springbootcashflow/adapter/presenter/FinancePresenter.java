package org.delcom.maxwellpbo2026springbootcashflow.adapter.presenter;

import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Presenter yang memformat data dari use case menjadi teks siap tampil.
 * Format uang dan label berada di sini, bukan di entity, sehingga tampilan
 * bisa diubah tanpa menyentuh domain.
 */
public class FinancePresenter {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Memformat uang: 1000000 menjadi "Rp 1,000,000.00" (negatif: "-Rp 1,000.00"). */
    public String formatMoney(BigDecimal amount) {
        BigDecimal rounded = amount.setScale(2, RoundingMode.HALF_UP);
        String number = String.format(Locale.US, "%,.2f", rounded.abs());
        return (rounded.signum() < 0 ? "-Rp " : "Rp ") + number;
    }

    /** Label jenis transaksi. */
    public String typeLabel(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    /** Label opsi pengurutan untuk ditampilkan di UI. */
    public String sortLabel(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    /** Memformat satu transaksi menjadi satu baris teks. */
    public String format(Transaction transaction) {
        return String.format(
                "[%d] %s | %s | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                formatMoney(transaction.getAmount()),
                typeLabel(transaction.getType())
        );
    }

    /** Memformat waktu pencatatan transaksi. */
    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** Memformat saldo: "Saldo: Rp 4,975,000.00". */
    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    /** Memformat daftar transaksi menjadi teks multi-baris. */
    public String formatList(List<Transaction> transactions, String header, String emptyMessage) {
        StringBuilder builder = new StringBuilder(header);
        if (transactions.isEmpty()) {
            builder.append('\n').append(emptyMessage);
            return builder.toString();
        }
        String body = transactions.stream()
                .map(this::format)
                .collect(Collectors.joining("\n"));
        builder.append('\n').append(body);
        return builder.toString();
    }

    /** Memformat daftar semua transaksi. */
    public String showTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi:", "- Data transaksi belum tersedia!");
    }

    /** Memformat hasil pencarian berdasarkan kata kunci. */
    public String showSearchResults(List<Transaction> transactions, String keyword) {
        return formatList(transactions, "Hasil Pencarian: \"" + keyword + "\"", "- Transaksi tidak ditemukan!");
    }

    /** Memformat daftar transaksi yang sudah diurutkan. */
    public String showSortedTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi (Terurut):", "- Data transaksi belum tersedia!");
    }

    /** Menampilkan saldo saat ini. */
    public String showBalance(BigDecimal balance) {
        return formatBalance(balance);
    }

    /** Pesan sukses setelah menambah pemasukan. */
    public String showAddIncomeSuccess(Transaction transaction) {
        return "Berhasil menambah pemasukan: " + format(transaction);
    }

    /** Pesan sukses setelah menambah pengeluaran. */
    public String showAddExpenseSuccess(Transaction transaction) {
        return "Berhasil menambah pengeluaran: " + format(transaction);
    }

    /** Pesan sukses setelah menghapus transaksi. */
    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    /** Pesan gagal saat menghapus transaksi. */
    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    /** Pesan saat ID yang dimasukkan tidak valid. */
    public String showInvalidId() {
        return "[!] ID tidak valid!";
    }

    /** Pesan saat opsi pengurutan tidak valid. */
    public String showInvalidSortOption() {
        return "[!] Pilihan pengurutan tidak valid!";
    }

    /** Pesan validasi umum dari domain. */
    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
