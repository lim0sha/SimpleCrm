package com.simplecrm.Repositories;

import com.simplecrm.Models.Entities.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long>, JpaSpecificationExecutor<Seller> {

    @Query("SELECT s FROM Seller s WHERE s.id = :id AND s.deleted = false")
    Optional<Seller> findNotDeletedById(@Param("id") Long id);

    @Query("SELECT s FROM Seller s WHERE s.deleted = false")
    List<Seller> findAllNotDeleted();

    @Query("SELECT s FROM Seller s WHERE s.name = :name AND s.deleted = false")
    Optional<Seller> findByNameAndNotDeleted(@Param("name") String name);

    @Query("""
            SELECT s FROM Seller s
            JOIN Transaction t ON t.seller.id = s.id
            WHERE s.deleted = false AND t.deleted = false
            AND t.transactionDate >= :start AND t.transactionDate <= :end
            GROUP BY s.id
            ORDER BY SUM(t.amount) DESC
            """)
    List<Seller> findTopSellerByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT s FROM Seller s
            LEFT JOIN Transaction t ON t.seller.id = s.id AND t.transactionDate >= :start AND t.transactionDate <= :end AND t.deleted = false
            WHERE s.deleted = false
            GROUP BY s.id
            HAVING SUM(COALESCE(t.amount, 0)) < :amount
            """)
    List<Seller> findSellersWithAmountLessThan(@Param("amount") java.math.BigDecimal amount, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}