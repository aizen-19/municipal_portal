package com.municipal.portal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {

        String message = body.get("message");

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("reply", "I cannot hear you. Please send a message."));
        }

        String query = message.toLowerCase().trim();
        String reply;

        if (query.contains("permit") || query.contains("building") || query.contains("blueprints")) {

            reply = """
                    To apply for a **Building Permit**, go to the **Apply Permit** tab in your dashboard and select "Building Permit". You will need:
                    1. **Architectural Blueprints** (PDF blueprint)
                    2. **Property Ownership Deed**
                    3. **Site Layout Plan**

                    Once submitted, our compliance AI scanner validates the structure against municipal zoning rules (takes ~5 seconds in simulation) and then you can click **"Simulate Officer Approval"** to issue your official digital certificate.
                    """;

        } else if (query.contains("tax") || query.contains("property") || query.contains("payment")) {

            reply = """
                    You can calculate and pay your **Property Tax** under the **Property Tax** tab.

                    - **Zone A** (Core city/Premium area) is calculated at **₹15 per sq ft**
                    - **Zone B** (Suburban area) is calculated at **₹10 per sq ft**
                    - **Zone C** (Outskirts/Rural area) is calculated at **₹5 per sq ft**

                    Commercial property types are charged an additional **1.5x multiplier**.
                    Once calculated, use our secure mock card form to simulate the payment and download your digital receipt.
                    """;

        } else if (query.contains("complaint") || query.contains("pot") || query.contains("light") || query.contains("garbage")) {

            reply = """
                    To report civic issues like potholes, broken street lights, or garbage clearance delays, select the **Complaints** tab.

                    Fill out the grievance form with:
                    - **Category** (Roads, Water, Waste Management, Electricity)
                    - **Subject & Description**

                    Your complaints will be displayed on the list right below, where you can also trigger a **"Simulate Resolution"** action to mark it as In Progress or Resolved!
                    """;

        } else if (query.contains("license") || query.contains("trade") || query.contains("business")) {

            reply = """
                    **Trade Licenses** are required to operate shops, commercial firms, or offices.

                    - Go to the **Apply Permit** tab, choose **Trade License**
                    - Supply details like Business Name, Trade Type, and Area (sq ft)
                    - The annual fee is ₹5,000

                    Submit the form to place it in your tracker.
                    """;

        } else if (query.contains("hello") || query.contains("hi") || query.contains("hey") || query.contains("help")) {

            reply = """
                    Hello! I am the **AI Municipal Legal & Civic Assistant** 🏛️.

                    I can help you navigate local administrative guidelines:

                    - **Building Permits & Trade Licenses** (Forms, documents, approvals)
                    - **Property Tax Calculations** (Zones, commercial multipliers)
                    - **Filing Civic Grievance Complaints**

                    What would you like assistance with today?
                    """;

        } else {

            reply = """
                    Thank you for reaching out to the Municipal Support Center.

                    Your query:
                    "%s"

                    This query falls under standard administrative review.

                    - For permit regulations, check the **Apply Permit** procedures.
                    - For payment issues, verify details in the **Property Tax** tab.
                    - For field inspections, file a formal complaint in the **Complaints** tab.

                    Let me know if you need specific step-by-step instructions for any of these!
                    """.formatted(message);
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return ResponseEntity.ok(Map.of("reply", reply));
    }
}