package com.municipal.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permit {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String userEmail;
    
    @Column(nullable = false, length = 100)
    private String type;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;
    
    @Column(nullable = false, length = 255)
    private String docName;
    
    @Column(nullable = false, length = 50)
    private String status;
    
    @Column(nullable = false, length = 100)
    private String date;
}
