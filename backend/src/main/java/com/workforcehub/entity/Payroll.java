package com.workforcehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payroll entity for salary management.
 */
@Entity
@Table(name = "payroll", indexes = {
        @Index(name = "idx_pay_employee", columnList = "employee_id"),
        @Index(name = "idx_pay_period", columnList = "pay_period_start, pay_period_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;

    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "basic_salary", precision = 12, scale = 2, nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "allowances", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "deductions", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "tax", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "overtime_pay", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(name = "bonus", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private boolean paid = false;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    /**
     * Calculate net salary.
     */
    @PrePersist
    @PreUpdate
    public void calculateNetSalary() {
        this.netSalary = basicSalary
                .add(allowances != null ? allowances : BigDecimal.ZERO)
                .add(overtimePay != null ? overtimePay : BigDecimal.ZERO)
                .add(bonus != null ? bonus : BigDecimal.ZERO)
                .subtract(deductions != null ? deductions : BigDecimal.ZERO)
                .subtract(tax != null ? tax : BigDecimal.ZERO);
    }
}
