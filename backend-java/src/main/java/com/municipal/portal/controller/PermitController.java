package com.municipal.portal.controller;

import com.municipal.portal.model.Permit;
import com.municipal.portal.repository.PermitRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/permits")
public class PermitController {

    private static final String MESSAGE = "message";
    private static final String USER_EMAIL = "userEmail";

    private final PermitRepository permitRepository;

    public PermitController(PermitRepository permitRepository) {
        this.permitRepository = permitRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> applyPermit(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String type = body.get("type");
        String details = body.get("details");
        String docName = body.get("docName");

        String userEmail = (String) request.getAttribute(USER_EMAIL);

        if (type == null || details == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "Permit type and details are required."));
        }

        String permitId = "pmt_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        Permit newPermit = new Permit(
                permitId,
                userEmail,
                type,
                details,
                docName != null ? docName : "blueprint_and_deed.pdf",
                "Pending",
                Instant.now().toString()
        );

        permitRepository.save(newPermit);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                MESSAGE, "Permit application submitted successfully.",
                "permit", newPermit
        ));
    }

    @GetMapping
    public ResponseEntity<List<Permit>> getPermits(HttpServletRequest request) {

        String userEmail = (String) request.getAttribute(USER_EMAIL);

        List<Permit> userPermits =
                permitRepository.findByUserEmailIgnoreCaseOrderByDateDesc(userEmail);

        return ResponseEntity.ok(userPermits);
    }

    @PostMapping("/{id}/simulate-review")
    public ResponseEntity<Map<String, Object>> simulateReview(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String status = body.get("status");
        String userEmail = (String) request.getAttribute(USER_EMAIL);

        if (status == null ||
                (!status.equals("Approved") && !status.equals("Rejected"))) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "Invalid status. Choose Approved or Rejected."));
        }

        Optional<Permit> permitOpt = permitRepository.findById(id);

        if (permitOpt.isEmpty() ||
                !permitOpt.get().getUserEmail().equalsIgnoreCase(userEmail)) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(MESSAGE, "Permit not found."));
        }

        Permit permit = permitOpt.get();
        permit.setStatus(status);
        permitRepository.save(permit);

        return ResponseEntity.ok(Map.of(
                MESSAGE, "Permit status updated to " + status + ".",
                "permit", permit
        ));
    }
}