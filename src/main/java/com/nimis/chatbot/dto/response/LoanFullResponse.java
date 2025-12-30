package com.nimis.chatbot.dto.response;

import lombok.Data;

@Data
public class LoanFullResponse {

    private AllocationResponse allocation;
    private CustomerResponse customer;
    private PhoneResponse phone;
    private AddressResponse address;
    private BankAccountResponse bankAccount;
    private AgencyResponse agency;
    private LegalCaseResponse legalCase;
}
