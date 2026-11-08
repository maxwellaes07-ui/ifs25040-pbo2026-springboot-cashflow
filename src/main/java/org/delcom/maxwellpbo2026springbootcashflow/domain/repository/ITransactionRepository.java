package org.delcom.maxwellpbo2026springbootcashflow.domain.repository;

import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.Transaction;

import java.math.BigDecimal;

/**
 * Port spesifik Transaction yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan transaksi baru dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    /**
     * Menyimpan transaksi bertipe pemasukan.
     *
     * @return transaksi yang tersimpan (lengkap dengan ID)
     */
    Transaction saveIncome(String description, BigDecimal amount);

    /**
     * Menyimpan transaksi bertipe pengeluaran.
     *
     * @return transaksi yang tersimpan (lengkap dengan ID)
     */
    Transaction saveExpense(String description, BigDecimal amount);
}
