package com.municipal.portal.repository;

import com.municipal.portal.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, String> {
    List<Complaint> findByUserEmailIgnoreCaseOrderByDateDesc(String email);
}
