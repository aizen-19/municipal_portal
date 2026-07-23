package com.municipal.portal.repository;

import com.municipal.portal.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, String> {
    List<Complaint> findByUserEmailIgnoreCaseOrderByDateDesc(String email);
}
