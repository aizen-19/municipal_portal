package com.municipal.portal.repository;

import com.municipal.portal.model.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermitRepository extends JpaRepository<Permit, String> {
    List<Permit> findByUserEmailIgnoreCaseOrderByDateDesc(String email);
}
