package com.municipal.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxPayment {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String userEmail;
    
    @Column(nullable = false, length = 10)
    private String zone;
    
    @Column(nullable = false)
    private Integer area;
    
    @Column(nullable = false, length = 100)
    private String propertyType;
    
    @Column(nullable = false)
    private Integer amount;
    
    @Column(nullable = false, length = 100)
    private String cardName;
    
    @Column(nullable = false, length = 50)
    private String receiptNo;
    
    @Column(nullable = false, length = 100)
    private String date;
}
