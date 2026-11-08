package org.delcom.maxwellpbo2026springbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void income_hasPositiveSignedAmount() {
        Transaction t = new Transaction(1, "Gaji", new BigDecimal("1000"), TransactionType.INCOME);

        assertTrue(t.isIncome());
        assertFalse(t.isExpense());
        assertEquals(new BigDecimal("1000"), t.signedAmount());
        assertNotNull(t.getCreatedAt());
    }

    @Test
    void expense_hasNegativeSignedAmount() {
        Transaction t = new Transaction(2, "Makan", new BigDecimal("25"), TransactionType.EXPENSE);

        assertTrue(t.isExpense());
        assertFalse(t.isIncome());
        assertEquals(new BigDecimal("-25"), t.signedAmount());
    }

    @Test
    void changeMethods_updateFieldsButNotIdOrType() {
        LocalDateTime time = LocalDateTime.of(2026, 1, 2, 3, 4);
        Transaction t = new Transaction(3, "Lama", new BigDecimal("10"), TransactionType.INCOME, time);

        t.changeDescription("Baru");
        t.changeAmount(new BigDecimal("99"));

        assertEquals(3, t.getId());
        assertEquals("Baru", t.getDescription());
        assertEquals(new BigDecimal("99"), t.getAmount());
        assertEquals(TransactionType.INCOME, t.getType());
        assertEquals(time, t.getCreatedAt());
    }
}
