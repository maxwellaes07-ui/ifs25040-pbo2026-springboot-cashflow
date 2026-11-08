package org.delcom.maxwellpbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan transaksi.
 * Setiap opsi membawa comparator-nya sendiri sehingga aturan pengurutan
 * terpusat di domain, bukan angka ajaib yang tersebar di view/use case.
 */
public enum SortOption {
    /** Jumlah terkecil lebih dulu. */
    AMOUNT_ASC(Comparator.comparing(Transaction::getAmount)
            .thenComparing(Transaction::getId)),

    /** Jumlah terbesar lebih dulu. */
    AMOUNT_DESC(Comparator.comparing(Transaction::getAmount).reversed()
            .thenComparing(Transaction::getId)),

    /** Pemasukan ditampilkan lebih dulu. */
    INCOME_FIRST(Comparator.comparing((Transaction t) -> t.isIncome() ? 0 : 1)
            .thenComparing(Transaction::getId)),

    /** Pengeluaran ditampilkan lebih dulu. */
    EXPENSE_FIRST(Comparator.comparing((Transaction t) -> t.isExpense() ? 0 : 1)
            .thenComparing(Transaction::getId));

    /** Comparator yang dipakai untuk mengurutkan daftar transaksi. */
    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    /** Mengembalikan comparator yang sesuai dengan opsi ini. */
    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
