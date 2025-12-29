package com.nimis.chatbot.service;

import com.nimis.chatbot.model.*;
import com.nimis.chatbot.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final AllocationRepository allocationRepository;
    private final CustomerRepository customerRepository;
    private final PhoneRepository phoneRepository;
    private final AddressRepository addressRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AgencyRepository agencyRepository;
    private final LegalCaseRepository legalCaseRepository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void processExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Read header row and normalize headers
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String normalizedHeader = cell.getStringCellValue()
                        .trim()
                        .toLowerCase()
                        .replace(" ", "_"); // normalize spaces
                headerMap.put(normalizedHeader, cell.getColumnIndex());
            }

            // Validate required headers
            if (!headerMap.containsKey("loannumber")) {
                throw new Exception("Missing required column: LOANNUMBER");
            }

            // Loop through data rows
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                // ====== Allocation ======
                Allocation allocation = new Allocation();
                allocation.setLoanNumber(getCell(row, headerMap, "loannumber"));
                allocation.setSegment(getCell(row, headerMap, "segment"));
                allocation.setProduct(getCell(row, headerMap, "product"));
                allocation.setZone(getCell(row, headerMap, "zone"));
                allocation.setState(getCell(row, headerMap, "state"));
                allocation.setBranch(getCell(row, headerMap, "branch"));
                allocation.setLocation(getCell(row, headerMap, "location"));
                allocation.setCustomerName(getCell(row, headerMap, "customer_name"));
                allocation.setDisbursedAmountCr(getBigDecimal(row, headerMap, "disbursed_amount_cr"));
                allocation.setDisbursedDate(getDate(row, headerMap, "disbursed_date"));
                allocation.setPosCr(getBigDecimal(row, headerMap, "pos_cr"));
                allocation.setPosAmt(getBigDecimal(row, headerMap, "pos_amt"));
                allocation.setEmi(getBigDecimal(row, headerMap, "emi"));
                allocation.setEmiStartDate(getDate(row, headerMap, "emi_start_date"));
                allocation.setEmiEndDate(getDate(row, headerMap, "emi_end_date"));
                allocation.setBktTag(getCell(row, headerMap, "bkt_tag"));
                allocation.setOpeningBkt(getCell(row, headerMap, "opening_bkt"));
                allocation.setAshvDaPtc(getCell(row, headerMap, "ashv_da_ptc"));
                allocation.setSecuritization(getCell(row, headerMap, "securitization"));
                allocation.setSeInse(getCell(row, headerMap, "se_inse"));
                allocation = allocationRepository.save(allocation);

                // ====== Customer ======
                Customer customer = new Customer();
                customer.setAllocation(allocation);
                customer.setMainApplicantName(getCell(row, headerMap, "main_applicant_name"));
                customer.setMainApplicantMobile(getCell(row, headerMap, "main_applicant_mobile"));
                customer.setPanMainApp(getCell(row, headerMap, "pan_main_app"));
                customer.setDobMainApp(getDate(row, headerMap, "dob_main_app"));
                customer.setCoApplicantName(getCell(row, headerMap, "co_applicant_name"));
                customer.setCoApplicantMobile(getCell(row, headerMap, "co_applicant_mobile"));
                customer.setPanCoApp(getCell(row, headerMap, "pan_co_app"));
                customer.setDobCoApp(getDate(row, headerMap, "dob_co_app"));
                customer.setRelationWithMainApplicant(getCell(row, headerMap, "relation_with_main_applicant"));
                customerRepository.save(customer);

                // ====== Phone ======
                Phone phone = new Phone();
                phone.setAllocation(allocation);
                phone.setPhone_1(getCell(row, headerMap, "phone_1"));
                phone.setPhone_2(getCell(row, headerMap, "phone_2"));
                phone.setPhone_3(getCell(row, headerMap, "phone_3"));
                phone.setPhone_4(getCell(row, headerMap, "phone_4"));
                phone.setPhone_5(getCell(row, headerMap, "phone_5"));
                phone.setPhone_6(getCell(row, headerMap, "phone_6"));
                phone.setPhone_7(getCell(row, headerMap, "phone_7"));
                phone.setPhone_8(getCell(row, headerMap, "phone_8"));
                phone.setPhone_9(getCell(row, headerMap, "phone_9"));
                phone.setPhone_10(getCell(row, headerMap, "phone_10"));
                phoneRepository.save(phone);

                // ====== Address ======
                Address address = new Address();
                address.setAllocation(allocation);
                address.setAddressPriority1(getCell(row, headerMap, "address_priority_1"));
                address.setAddressPriority2(getCell(row, headerMap, "address_priority_2"));
                address.setAddressPriority3(getCell(row, headerMap, "address_priority_3"));
                address.setAddressPriority4(getCell(row, headerMap, "address_priority_4"));
                address.setAddressPriority5(getCell(row, headerMap, "address_priority_5"));
                address.setAddressPriority6(getCell(row, headerMap, "address_priority_6"));
                address.setAddressPriority7(getCell(row, headerMap, "address_priority_7"));
                address.setAddressPriority8(getCell(row, headerMap, "address_priority_8"));
                address.setAddress_1(getCell(row, headerMap, "address_1"));
                address.setAddress_2(getCell(row, headerMap, "address_2"));
                address.setAddress_3(getCell(row, headerMap, "address_3"));
                address.setAddress_4(getCell(row, headerMap, "address_4"));
                address.setAddress_5(getCell(row, headerMap, "address_5"));
                address.setAddress_6(getCell(row, headerMap, "address_6"));
                address.setAddress_7(getCell(row, headerMap, "address_7"));
                address.setAddress_8(getCell(row, headerMap, "address_8"));
                address.setAddress_9(getCell(row, headerMap, "address_9"));
                address.setAddress_10(getCell(row, headerMap, "address_10"));
                address.setBusinessPinCode(getCell(row, headerMap, "business_pin_code"));
                address.setResidencePinCode(getCell(row, headerMap, "residence_pin_code"));
                address.setMainPinCode(getCell(row, headerMap, "main_pin_code"));
                addressRepository.save(address);

                // ====== BankAccount ======
                BankAccount bankAccount = new BankAccount();
                bankAccount.setAllocation(allocation);
                bankAccount.setAccountHolder1(getCell(row, headerMap, "account_holder1"));
                bankAccount.setBankName1(getCell(row, headerMap, "bank_name1"));
                bankAccount.setAccountNumber1(getCell(row, headerMap, "account_number1"));
                bankAccount.setIfscCode1(getCell(row, headerMap, "ifsc_code1"));
                bankAccount.setAccountHolder2(getCell(row, headerMap, "account_holder2"));
                bankAccount.setBankName2(getCell(row, headerMap, "bank_name2"));
                bankAccount.setAccountNumber2(getCell(row, headerMap, "account_number2"));
                bankAccount.setIfscCode2(getCell(row, headerMap, "ifsc_code2"));
                bankAccount.setAccountHolder3(getCell(row, headerMap, "account_holder3"));
                bankAccount.setBankName3(getCell(row, headerMap, "bank_name3"));
                bankAccount.setAccountNumber3(getCell(row, headerMap, "account_number3"));
                bankAccount.setIfscCode3(getCell(row, headerMap, "ifsc_code3"));
                bankAccountRepository.save(bankAccount);

                // ====== Agency ======
                Agency agency = new Agency();
                agency.setAllocation(allocation);
                agency.setAgencyCode(getCell(row, headerMap, "agency_code"));
                agency.setAgencyName(getCell(row, headerMap, "agency_name"));
                agency.setManagerEmpId(getCell(row, headerMap, "manager_emp_id"));
                agency.setManagerName(getCell(row, headerMap, "manager_name"));
                agency.setZmEmpId(getCell(row, headerMap, "zm_emp_id"));
                agency.setZonalManager(getCell(row, headerMap, "zonal_manager"));
                agencyRepository.save(agency);

                // ====== LegalCase ======
                LegalCase legalCase = new LegalCase();
                legalCase.setAllocation(allocation);
                legalCase.setCaseType(getCell(row, headerMap, "case_type"));
                legalCase.setProcessStage(getCell(row, headerMap, "process_stage"));
                legalCase.setRevisedStage(getCell(row, headerMap, "revised_stage"));
                legalCase.setMonthLastNotice(getCell(row, headerMap, "month_last_notice"));
                legalCase.setMonthOfNotice(getCell(row, headerMap, "month_of_notice"));
                legalCase.setNoticeDate(getDate(row, headerMap, "notice_date"));
                legalCase.setFilingConfirmationDate(getDate(row, headerMap, "filing_confirmation_date"));
                legalCase.setLrn1(getCell(row, headerMap, "lrn1"));
                legalCase.setLrnMonth2(getCell(row, headerMap, "lrn_month2"));
                legalCase.setLrn2(getCell(row, headerMap, "lrn2"));
                legalCase.setLrnMonth3(getCell(row, headerMap, "lrn_month3"));
                legalCase.setLrn3(getCell(row, headerMap, "lrn3"));
                legalCase.setArbitrationInvocationDate1(getDate(row, headerMap, "arbitration_invocation_date1"));
                legalCase.setArbitrationInvocationDate2(getDate(row, headerMap, "arbitration_invocation_date2"));
                legalCase.setTentativeReferenceDate(getDate(row, headerMap, "tentative_reference_date"));
                legalCase.setLetterToArbitratorDate(getDate(row, headerMap, "letter_to_arbitrator_date"));
                legalCase.setTentativeFreezeDate(getDate(row, headerMap, "tentative_freeze_date"));
                legalCase.setSec17OrderDate(getDate(row, headerMap, "sec17_order_date"));
                legalCase.setRelief(getCell(row, headerMap, "relief"));
                legalCase.setSec25Filed(getCell(row, headerMap, "sec25_filed"));
                legalCase.setSec25ProcessStage(getCell(row, headerMap, "sec25_process_stage"));
                legalCase.setSec25LDOH(getDate(row, headerMap, "sec25_ldoh"));
                legalCase.setSec25NDOH(getDate(row, headerMap, "sec25_ndoh"));
                legalCase.setSec138Filed(getCell(row, headerMap, "sec138_filed"));
                legalCase.setSec138ProcessStage(getCell(row, headerMap, "sec138_process_stage"));
                legalCase.setSec138LDOH(getDate(row, headerMap, "sec138_ldoh"));
                legalCase.setSec138NDOH(getDate(row, headerMap, "sec138_ndoh"));
                legalCase.setSec420ProcessStage(getCell(row, headerMap, "sec420_process_stage"));
                legalCase.setSec420LDOH(getDate(row, headerMap, "sec420_ldoh"));
                legalCase.setSec420NDOH(getDate(row, headerMap, "sec420_ndoh"));
                legalCase.setSec420ListingDate(getDate(row, headerMap, "sec420_listing_date"));
                legalCase.setCaseWithdrawalDate(getDate(row, headerMap, "case_withdrawal_date"));
                legalCase.setListingDate(getDate(row, headerMap, "listing_date"));
                legalCase.setFilingDate(getDate(row, headerMap, "filing_date"));
                legalCase.setCourtForum(getCell(row, headerMap, "court_forum"));
                legalCase.setCourtLocation(getCell(row, headerMap, "court_location"));
                legalCase.setCaseNumber(getCell(row, headerMap, "case_number"));
                legalCase.setClaimAmount(getBigDecimal(row, headerMap, "claim_amount"));
                legalCase.setAdvocateName(getCell(row, headerMap, "advocate_name"));
                legalCase.setAdvocateContact(getCell(row, headerMap, "advocate_contact"));
                legalCase.setAuthorizedOfficer(getCell(row, headerMap, "authorized_officer"));
                legalCase.setStage1Verification(getCell(row, headerMap, "stage1_verification"));
                legalCase.setStage1HearingOn(getDate(row, headerMap, "stage1_hearing_on"));
                legalCase.setStage2SummonsStage(getCell(row, headerMap, "stage2_summons_stage"));
                legalCase.setStage2SummonsStatus(getCell(row, headerMap, "stage2_summons_status"));
                legalCase.setStage3Appearance(getCell(row, headerMap, "stage3_appearance"));
                legalCase.setStage3HearingOn(getDate(row, headerMap, "stage3_hearing_on"));
                legalCase.setStage4BailableWarrantDate(getDate(row, headerMap, "stage4_bailable_warrant_date"));
                legalCase.setStage4BailableWarrantStatus(getCell(row, headerMap, "stage4_bailable_warrant_status"));
                legalCase.setStage5NonBailableWarrantDate(getDate(row, headerMap, "stage5_non_bailable_warrant_date"));
                legalCase.setBwReissuedDate(getDate(row, headerMap, "bw_reissued_date"));
                legalCase.setBwWarrantPostDate(getDate(row, headerMap, "bw_warrant_post_date"));
                legalCase.setBwReissuedCollectionDate(getDate(row, headerMap, "bw_reissued_collection_date"));
                legalCase.setStage5NonBailableWarrantStatus(getCell(row, headerMap, "stage5_non_bailable_warrant_status"));
                legalCase.setStage5NonBailableWarrantCollectedStatus(getCell(row, headerMap, "stage5_non_bailable_warrant_collected_status"));
                legalCase.setStage5ProclamationIssuedDate(getDate(row, headerMap, "stage5_proclamation_issued_date"));
                legalCase.setStage5ProclamationStatus(getCell(row, headerMap, "stage5_proclamation_status"));
                legalCase.setStage5ProclamationCollectedStatus(getCell(row, headerMap, "stage5_proclamation_collected_status"));
                legalCase.setStage5AttachmentIssuedDate(getDate(row, headerMap, "stage5_attachment_issued_date"));
                legalCase.setStage5AttachmentStatus(getCell(row, headerMap, "stage5_attachment_status"));
                legalCase.setStage5AttachmentCollectedStatus(getCell(row, headerMap, "stage5_attachment_collected_status"));
                legalCase.setStage6NonBailableReissueDate(getDate(row, headerMap, "stage6_non_bailable_reissue_date"));
                legalCase.setStage6NonBailableReissueStatus(getCell(row, headerMap, "stage6_non_bailable_reissue_status"));
                legalCase.setNonBailableWarrantReissuedDate(getDate(row, headerMap, "non_bailable_warrant_reissued_date"));
                legalCase.setNonBailableWarrantReissueCollectionDate(getDate(row, headerMap, "non_bailable_warrant_reissue_collection_date"));
                legalCase.setStage7NonBailableReissueDate(getDate(row, headerMap, "stage7_non_bailable_reissue_date"));
                legalCase.setStage7NonBailableReissueStatus(getCell(row, headerMap, "stage7_non_bailable_reissue_status"));
                legalCase.setStage8NonBailableReissueDate(getDate(row, headerMap, "stage8_non_bailable_reissue_date"));
                legalCase.setStage8NonBailableReissueStatus(getCell(row, headerMap, "stage8_non_bailable_reissue_status"));
                legalCase.setRemarks(getCell(row, headerMap, "remarks"));
                legalCaseRepository.save(legalCase);
            }
        }
    }

    // ===== Helpers =====
    private String getCell(Row row, Map<String, Integer> headerMap, String column) {
        Integer idx = headerMap.get(column.toLowerCase());
        if (idx == null) return null;
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return dateFormatter.format(cell.getLocalDateTimeCellValue().toLocalDate());
                }
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return null;
        }
    }

    private BigDecimal getBigDecimal(Row row, Map<String, Integer> headerMap, String column) {
        String val = getCell(row, headerMap, column);
        if (val == null || val.isEmpty()) return null;
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate getDate(Row row, Map<String, Integer> headerMap, String column) {
        String val = getCell(row, headerMap, column);
        if (val == null || val.isEmpty()) return null;
        try {
            return LocalDate.parse(val, dateFormatter);
        } catch (Exception e) {
            return null;
        }
    }
}
