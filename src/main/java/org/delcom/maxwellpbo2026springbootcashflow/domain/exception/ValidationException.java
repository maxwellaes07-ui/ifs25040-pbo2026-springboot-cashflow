package org.delcom.maxwellpbo2026springbootcashflow.domain.exception;

/**
 * Exception yang dilempar saat validasi aturan bisnis atau input domain gagal.
 */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
