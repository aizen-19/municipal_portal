package com.municipal.portal.repository;

import com.municipal.portal.model.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PermitRepository extends JpaRepository<Permit, String> {
    List<Permit> findByUserEmailIgnoreCaseOrderByDateDesc(String email);
}
