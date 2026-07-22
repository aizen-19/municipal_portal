package com.municipal.portal.controller;

import com.municipal.portal.model.Complaint;
import com.municipal.portal.repository.ComplaintRepository;
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
@RequestMapping("/api/complaints")
public class ComplaintController {

    private static final String MESSAGE = "message";
    private static final String USER_EMAIL = "userEmail";

    private final ComplaintRepository complaintRepository;

    public ComplaintController(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerComplaint(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String category = body.get("category");
        String subject = body.get("subject");
        String description = body.get("description");

        String userEmail = (String) request.getAttribute(USER_EMAIL);

        if (category == null || subject == null || description == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "All fields are required."));
        }

        String complaintId = "cmp_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        Complaint newComplaint = new Complaint(
                complaintId,
                userEmail,
                category,
                subject,
                description,
                "Pending",
                Instant.now().toString()
        );

        complaintRepository.save(newComplaint);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                MESSAGE, "Complaint registered successfully.",
                "complaint", newComplaint
        ));
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getComplaints(HttpServletRequest request) {

        String userEmail = (String) request.getAttribute(USER_EMAIL);

        List<Complaint> userComplaints =
                complaintRepository.findByUserEmailIgnoreCaseOrderByDateDesc(userEmail);

        return ResponseEntity.ok(userComplaints);
    }

    @PostMapping("/{id}/simulate-action")
    public ResponseEntity<Map<String, Object>> simulateAction(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String status = body.get("status");
        String userEmail = (String) request.getAttribute(USER_EMAIL);

        if (status == null ||
                (!status.equals("In Progress") && !status.equals("Resolved"))) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(MESSAGE, "Invalid status."));
        }

        Optional<Complaint> complaintOpt = complaintRepository.findById(id);

        if (complaintOpt.isEmpty() ||
                !complaintOpt.get().getUserEmail().equalsIgnoreCase(userEmail)) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(MESSAGE, "Complaint not found."));
        }

        Complaint complaint = complaintOpt.get();
        complaint.setStatus(status);
        complaintRepository.save(complaint);

        return ResponseEntity.ok(Map.of(
                MESSAGE, "Complaint status updated to " + status + ".",
                "complaint", complaint
        ));
    }
}