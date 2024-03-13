package com.example.domyjob.repository;

import com.example.domyjob.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>{
    @Query("SELECT i FROM Invoice i WHERE i.status = 'Pending' AND i.id IN " +
            "(SELECT p.invoiceId FROM Payment p GROUP BY p.invoiceId HAVING SUM(p.amount) >= " +
            "(SELECT i2.amount FROM Invoice i2 WHERE i2.id = p.invoiceId))")
    List<Invoice> findPendingInvoicesWithFullPayment();
}
