package com.municipal.portal.controller;

import com.municipal.portal.model.TaxPayment;
import com.municipal.portal.repository.TaxPaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/tax")
public class TaxController {

    @Autowired
    private TaxPaymentRepository taxPaymentRepository;

    @PostMapping("/pay")
    public ResponseEntity<?> payTax(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String zone = (String) body.get("zone");
        Object areaObj = body.get("area");
        String propertyType = (String) body.get("propertyType");
        Object amountObj = body.get("amount");
        String cardName = (String) body.get("cardName");

        String userEmail = (String) request.getAttribute("userEmail");

        if (zone == null || areaObj == null || propertyType == null || amountObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Calculation and amount details are required."));
        }

        Integer area = areaObj instanceof Number ? ((Number) areaObj).intValue() : Integer.parseInt(areaObj.toString());
        Integer amount = amountObj instanceof Number ? ((Number) amountObj).intValue() : Integer.parseInt(amountObj.toString());

        String taxId = "tax_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String receiptNo = "RCP-" + (100000 + new Random().nextInt(900000));
        
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

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Property tax payment processed successfully.",
                "payment", payment
        ));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<TaxPayment>> getPayments(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        List<TaxPayment> payments = taxPaymentRepository.findByUserEmailIgnoreCaseOrderByDateDesc(userEmail);
        return ResponseEntity.ok(payments);
    }
}
