package org.delcom.maxwellpbo2026springbootcashflow.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class DomainExceptionTest {

    @Test
    void domainException_keepsMessageAndCause() {
        RuntimeException cause = new RuntimeException("akar");
        DomainException ex = new DomainException("pesan", cause);

        assertEquals("pesan", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void entityNotFound_isDomainExceptionWithId() {
        EntityNotFoundException ex = new EntityNotFoundException(7);

        assertInstanceOf(DomainException.class, ex);
        assertEquals(7, ex.getId());
        assertEquals("Transaksi dengan ID 7 tidak ditemukan.", ex.getMessage());
    }

    @Test
    void validation_isDomainException() {
        ValidationException ex = new ValidationException("salah");

        assertInstanceOf(DomainException.class, ex);
        assertEquals("salah", ex.getMessage());
    }
}
