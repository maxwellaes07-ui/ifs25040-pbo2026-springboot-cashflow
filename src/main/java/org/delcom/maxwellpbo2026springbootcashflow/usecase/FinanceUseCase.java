package org.delcom.maxwellpbo2026springbootcashflow.usecase;

import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.maxwellpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.maxwellpbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.maxwellpbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Use case yang menangani logika bisnis pencatatan keuangan.
 * Tidak melakukan I/O (tanpa Vaadin / System.out): hanya memproses data dan
 * melempar checked exception domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class FinanceUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Menambahkan pemasukan baru setelah validasi. */
    public Transaction addIncome(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return transactionRepository.saveIncome(description.trim(), normalize(amount));
    }

    /** Menambahkan pengeluaran baru setelah validasi. */
    public Transaction addExpense(String description, BigDecimal amount) throws ValidationException {
        validate(description, amount);
        return transactionRepository.saveExpense(description.trim(), normalize(amount));
    }

    /**
     * Mengubah teks jumlah dari input pengguna menjadi angka.
     * Tanda koma dianggap pemisah ribuan (sesuai format tampilan: 1,000,000.00).
     */
    public BigDecimal parseAmount(String text) throws ValidationException {
        if (text == null || text.isBlank()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        String cleaned = text.trim().replace(",", "");
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Jumlah harus berupa angka");
        }
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika ID tidak valid / tidak ditemukan. */
    public void removeTransaction(int id) throws ValidationException, EntityNotFoundException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return transactionRepository.findAll();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        return transactionRepository.findBy(
                transaction -> transaction.getDescription().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan transaksi sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Transaction> sortTransactions(SortOption option) throws ValidationException {
        if (option == null) {
            throw new ValidationException("Pilihan pengurutan tidak valid");
        }
        return transactionRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Total seluruh pemasukan. */
    public BigDecimal getTotalIncome() {
        return transactionRepository.findAll().stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Total seluruh pengeluaran. */
    public BigDecimal getTotalExpense() {
        return transactionRepository.findAll().stream()
                .filter(Transaction::isExpense)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Saldo saat ini = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return transactionRepository.findAll().stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validate(String description, BigDecimal amount) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        if (amount == null) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
    }

    private BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
