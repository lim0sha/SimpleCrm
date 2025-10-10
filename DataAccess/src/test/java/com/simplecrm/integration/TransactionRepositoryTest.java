package com.simplecrm.integration;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Models.Enums.PaymentType;
import com.simplecrm.Projections.TransactionFlatView;
import com.simplecrm.Repositories.TransactionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = com.simplecrm.Application.Application.class)
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }

    @Test
    void findNotDeletedById_returnsTransaction_whenExistsAndNotDeleted() {
        Seller seller = createAndPersistSeller("Seller A");
        Transaction tx = createAndPersistTransaction(seller, new BigDecimal("100.00"), PaymentType.CASH, LocalDateTime.now(), false);

        Optional<Transaction> result = transactionRepository.findNotDeletedById(tx.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void findNotDeletedById_returnsEmpty_whenDeleted() {
        Seller seller = createAndPersistSeller("Seller B");
        Transaction tx = createAndPersistTransaction(seller, new BigDecimal("200.00"), PaymentType.CARD, LocalDateTime.now(), true);

        Optional<Transaction> result = transactionRepository.findNotDeletedById(tx.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findAllNotDeleted_returnsOnlyActiveTransactions() {
        Seller seller = createAndPersistSeller("Seller C");
        createAndPersistTransaction(seller, new BigDecimal("100"), PaymentType.CASH, LocalDateTime.now(), false);
        createAndPersistTransaction(seller, new BigDecimal("200"), PaymentType.CARD, LocalDateTime.now().plusDays(1), true);

        List<Transaction> result = transactionRepository.findAllNotDeleted();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAmount()).isEqualByComparingTo("100");
    }

    @Test
    void findBySellerIdAndNotDeleted_returnsTransactionsForSeller() {
        Seller seller1 = createAndPersistSeller("Seller 1");
        Seller seller2 = createAndPersistSeller("Seller 2");

        createAndPersistTransaction(seller1, new BigDecimal("50"), PaymentType.CASH, LocalDateTime.now(), false);
        createAndPersistTransaction(seller2, new BigDecimal("60"), PaymentType.CARD, LocalDateTime.now(), false);

        List<Transaction> result = transactionRepository.findBySellerIdAndNotDeleted(seller1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getSeller().getId()).isEqualTo(seller1.getId());
    }

    @Test
    void findBySellerIdAndDateRange_filtersByPeriod() {
        Seller seller = createAndPersistSeller("Seller D");
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 30, 23, 59);

        createAndPersistTransaction(seller, new BigDecimal("10"), PaymentType.CASH, start.minusDays(1), false);
        createAndPersistTransaction(seller, new BigDecimal("20"), PaymentType.CARD, start.plusDays(5), false);
        createAndPersistTransaction(seller, new BigDecimal("30"), PaymentType.TRANSFER, end.plusDays(1), false);

        List<Transaction> result = transactionRepository.findBySellerIdAndDateRange(seller.getId(), start, end);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAmount()).isEqualByComparingTo("20");
    }

    @Test
    void findByDateRange_returnsAllTransactionsInPeriod() {
        Seller seller1 = createAndPersistSeller("S1");
        Seller seller2 = createAndPersistSeller("S2");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 7, 31, 23, 59);

        createAndPersistTransaction(seller1, new BigDecimal("100"), PaymentType.CASH, start.plusDays(1), false);
        createAndPersistTransaction(seller2, new BigDecimal("200"), PaymentType.CARD, start.plusDays(2), false);
        createAndPersistTransaction(seller1, new BigDecimal("300"), PaymentType.TRANSFER, end.plusDays(1), false);

        List<Transaction> result = transactionRepository.findByDateRange(start, end);

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(Transaction::getAmount))
                .containsExactlyInAnyOrder(new BigDecimal("100"), new BigDecimal("200"));
    }

    @Test
    void findFlatBySellerId_returnsProjectionFields() {
        Seller seller = createAndPersistSeller("Projected Seller");
        LocalDateTime txDate = LocalDateTime.of(2024, 8, 15, 10, 30);
        Transaction tx = createAndPersistTransaction(
                seller, new BigDecimal("999.99"), PaymentType.TRANSFER, txDate, false);

        List<TransactionFlatView> result = transactionRepository.findFlatBySellerId(seller.getId());

        assertThat(result).hasSize(1);
        TransactionFlatView view = result.getFirst();

        assertThat(view.getId()).isEqualTo(tx.getId());
        assertThat(view.getAmount()).isEqualByComparingTo("999.99");
        assertThat(view.getPaymentType()).isEqualTo(PaymentType.TRANSFER);
        assertThat(view.getTransactionDate()).isEqualTo(txDate);
        assertThat(view.getVersion()).isEqualTo(0L);
    }

    private Seller createAndPersistSeller(String name) {
        Seller seller = new Seller();
        seller.setName(name);
        seller.setContactInfo(name + "@example.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setDeleted(false);
        entityManager.persist(seller);
        entityManager.flush();
        return seller;
    }

    private Transaction createAndPersistTransaction(
            Seller seller,
            BigDecimal amount,
            PaymentType paymentType,
            LocalDateTime date,
            boolean deleted
    ) {
        Transaction tx = new Transaction();
        tx.setSeller(seller);
        tx.setAmount(amount);
        tx.setPaymentType(paymentType);
        tx.setTransactionDate(date);
        tx.setDeleted(deleted);
        tx.setVersion(0L);
        entityManager.persist(tx);
        entityManager.flush();
        return tx;
    }
}