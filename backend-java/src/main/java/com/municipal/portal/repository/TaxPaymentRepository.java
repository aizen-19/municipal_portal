package com.municipal.portal.repository;

import com.municipal.portal.model.TaxPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaxPaymentRepository extends JpaRepository<TaxPayment, String> {
    List<TaxPayment> findByUserEmailIgnoreCaseOrderByDateDesc(String email);
}
