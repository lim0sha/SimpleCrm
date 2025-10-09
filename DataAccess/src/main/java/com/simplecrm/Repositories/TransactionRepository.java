package com.simplecrm.Repositories;

import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Projections.TransactionFlatView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT t FROM Transaction t WHERE t.id = :id AND t.deleted = false")
    Optional<Transaction> findNotDeletedById(@Param("id") Long id);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.seller WHERE t.deleted = false ORDER BY t.transactionDate ASC")
    List<Transaction> findAllNotDeleted();

    @Query("SELECT t FROM Transaction t JOIN FETCH t.seller WHERE t.seller.id = :sellerId AND t.deleted = false ORDER BY t.transactionDate ASC")
    List<Transaction> findBySellerIdAndNotDeleted(@Param("sellerId") Long sellerId);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.seller WHERE t.seller.id = :sellerId AND t.deleted = false AND t.transactionDate >= :start AND t.transactionDate <= :end ORDER BY t.transactionDate ASC")
    List<Transaction> findBySellerIdAndDateRange(@Param("sellerId") Long sellerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.seller WHERE t.deleted = false AND t.transactionDate >= :start AND t.transactionDate <= :end ORDER BY t.transactionDate ASC")
    List<Transaction> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT 
                t.id AS id,
                t.amount AS amount,
                t.paymentType AS paymentType,
                t.transactionDate AS transactionDate,
                t.version AS version,
                s.id AS seller_id,
                s.name AS seller_name,
                s.contactInfo AS seller_contactInfo,
                s.registrationDate AS seller_registrationDate,
                s.version AS seller_version
            FROM Transaction t
            JOIN t.seller s
            WHERE s.id = :sellerId AND t.deleted = false
            ORDER BY t.transactionDate ASC
            """)
    List<TransactionFlatView> findFlatBySellerId(@Param("sellerId") Long sellerId);
}