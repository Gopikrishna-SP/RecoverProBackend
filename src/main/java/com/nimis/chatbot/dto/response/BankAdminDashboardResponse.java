package com.nimis.chatbot.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * Bank Admin Dashboard Statistics DTO
 *
 * Contains calculated metrics for bank admin dashboard:
 * - Total Case Value: Sum of (EMI * OPENING_BKT) for all unique loans
 * - Collection metrics: Today, Yesterday, Monthly, Total
 * - Unapproved Cash breakdown: Pending Approval, Pending Deposit
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAdminDashboardResponse {

    /**
     * Total Case Value = Sum of (EMI * OPENING_BKT) for all unique loan numbers
     * Example: If loan has BKT=5 and EMI=10000, case value = 50000
     */
    private BigDecimal totalCaseValue;

    /**
     * Total collection amount (all time) from visit logs
     */
    private BigDecimal totalCollection;

    /**
     * Collection amount from today's visit logs where amount is not null
     */
    private BigDecimal todayCollections;

    /**
     * Collection amount from yesterday's visit logs where amount is not null
     */
    private BigDecimal yesterdayCollection;

    /**
     * Collection amount from current month's visit logs
     */
    private BigDecimal monthlyCollection;

    /**
     * Total unapproved cash = cashPendingForDeposit + pendingForApproval
     */
    private BigDecimal totalUnapprovedCash;

    /**
     * Cash pending for deposit (from visit logs with PENDING_DEPOSIT status)
     */
    private BigDecimal cashPendingForDeposit;

    /**
     * Cash pending for approval (from visit logs with PENDING_APPROVAL status)
     */
    private BigDecimal pendingForApproval;
}