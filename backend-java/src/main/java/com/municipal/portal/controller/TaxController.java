package com.municipal.portal.controller;

import com.municipal.portal.model.TaxPayment;
import com.municipal.portal.repository.TaxPaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tax")
public class TaxController {

    private static final String RECEIPT_PREFIX = "RCP-";

    private final TaxPaymentRepository taxPaymentRepository;

    public TaxController(TaxPaymentRepository taxPaymentRepository) {
        this.taxPaymentRepository = taxPaymentRepository;
    }

    @PostMapping("/pay")
    public ResponseEntity<Map<String, Object>> payTax(@RequestBody Map<String, Object> body,
                                                      HttpServletRequest request) {

        String zone = (String) body.get("zone");
        Object areaObj = body.get("area");
        String propertyType = (String) body.get("propertyType");
        Object amountObj = body.get("amount");
        String cardName = (String) body.get("cardName");

        String userEmail = (String) request.getAttribute("userEmail");

        if (zone == null || areaObj == null || propertyType == null || amountObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message", "Calculation and amount details are required.")
            );
        }

        Integer area = areaObj instanceof Number areaNumber
                ? areaNumber.intValue()
                : Integer.parseInt(areaObj.toString());

        Integer amount = amountObj instanceof Number amountNumber
                ? amountNumber.intValue()
                : Integer.parseInt(amountObj.toString());

        String taxId = "tax_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String receiptNo = RECEIPT_PREFIX + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        TaxPayment payment = new TaxPayment(
                taxId,
                userEmail,
                zone,
                area,
                propertyType,
                amount,
                cardName != null ? cardName : "Citizen User",
                receiptNo,
                Instant.now().toString()
        );

        taxPaymentRepository.save(payment);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Property tax payment processed successfully.",
                        "payment", payment
                )
        );
    }

    @GetMapping("/payments")
    public ResponseEntity<List<TaxPayment>> getPayments(HttpServletRequest request) {

        String userEmail = (String) request.getAttribute("userEmail");

        List<TaxPayment> payments =
                taxPaymentRepository.findByUserEmailIgnoreCaseOrderByDateDesc(userEmail);

        return ResponseEntity.ok(payments);
    }
}