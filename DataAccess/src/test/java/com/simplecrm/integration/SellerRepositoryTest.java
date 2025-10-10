package com.simplecrm.integration;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.Models.Enums.PaymentType;
import com.simplecrm.Repositories.SellerRepository;
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
class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        sellerRepository.deleteAll();
    }

    @Test
    void findNotDeletedById_returnsSeller_whenExistsAndNotDeleted() {
        Seller seller = new Seller();
        seller.setName("Alice");
        seller.setContactInfo("alice@example.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setDeleted(false);
        Seller saved = sellerRepository.save(seller);

        Optional<Seller> result = sellerRepository.findNotDeletedById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice");
        assertThat(result.get().getDeleted()).isFalse();
    }

    @Test
    void findNotDeletedById_returnsEmpty_whenSellerDeleted() {
        Seller seller = new Seller();
        seller.setName("Bob (deleted)");
        seller.setDeleted(true);
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setContactInfo("stest@example.com");
        Seller saved = sellerRepository.save(seller);

        Optional<Seller> result = sellerRepository.findNotDeletedById(saved.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findNotDeletedById_returnsEmpty_whenSellerDoesNotExist() {
        Optional<Seller> result = sellerRepository.findNotDeletedById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllNotDeleted_returnsOnlyActiveSellers() {
        Seller active1 = new Seller();
        active1.setName("Active 1");
        active1.setDeleted(false);
        active1.setRegistrationDate(LocalDateTime.now());
        active1.setContactInfo("a1test@example.com");

        Seller active2 = new Seller();
        active2.setName("Active 2");
        active2.setDeleted(false);
        active2.setRegistrationDate(LocalDateTime.now());
        active2.setContactInfo("a2test@example.com");

        Seller deleted = new Seller();
        deleted.setName("Deleted");
        deleted.setDeleted(true);
        deleted.setRegistrationDate(LocalDateTime.now());
        deleted.setContactInfo("deltest@example.com");

        sellerRepository.saveAll(List.of(active1, active2, deleted));
        List<Seller> result = sellerRepository.findAllNotDeleted();


        assertThat(result).hasSize(2);
        assertThat(result).extracting(Seller::getName)
                .containsExactlyInAnyOrder("Active 1", "Active 2");
    }

    @Test
    void findByNameAndNotDeleted_returnsSeller_whenExistsAndNotDeleted() {
        Seller seller = new Seller();
        seller.setName("Charlie");
        seller.setDeleted(false);
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setContactInfo("test@example.com");

        sellerRepository.save(seller);
        Optional<Seller> result = sellerRepository.findByNameAndNotDeleted("Charlie");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Charlie");
    }

    @Test
    void findByNameAndNotDeleted_returnsEmpty_whenDeleted() {
        Seller seller = new Seller();
        seller.setName("Charlie");
        seller.setDeleted(true);
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setContactInfo("stest@example.com");
        sellerRepository.save(seller);

        Optional<Seller> result = sellerRepository.findByNameAndNotDeleted("Charlie");

        assertThat(result).isEmpty();
    }

    @Test
    void findTopSellerByPeriod_returnsSortedByTotalAmount() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        Seller seller1 = new Seller();
        seller1.setName("Top Seller");
        seller1.setDeleted(false);
        seller1.setRegistrationDate(LocalDateTime.now());
        seller1.setContactInfo("s1test@example.com");
        seller1 = sellerRepository.save(seller1);

        Seller seller2 = new Seller();
        seller2.setName("Low Seller");
        seller2.setDeleted(false);
        seller2.setRegistrationDate(LocalDateTime.now());
        seller2.setContactInfo("s2test@example.com");
        seller2 = sellerRepository.save(seller2);

        saveTransaction(seller1, new BigDecimal("500.00"), start.plusDays(1));
        saveTransaction(seller1, new BigDecimal("300.00"), start.plusDays(2));
        saveTransaction(seller2, new BigDecimal("100.00"), start.plusDays(3));
        List<Seller> result = sellerRepository.findTopSellerByPeriod(start, end);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Top Seller");
        assertThat(result.get(1).getName()).isEqualTo("Low Seller");
    }

    @Test
    void findTopSellerByPeriod_excludesDeletedTransactions() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        Seller seller = new Seller();
        seller.setName("Seller with deleted tx");
        seller.setDeleted(false);
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setContactInfo("stest@example.com");
        seller = sellerRepository.save(seller);

        saveTransaction(seller, new BigDecimal("200.00"), start.plusDays(1), false);
        saveTransaction(seller, new BigDecimal("800.00"), start.plusDays(2), true);

        List<Seller> result = sellerRepository.findTopSellerByPeriod(start, end);

        assertThat(result).hasSize(1);

    }

    @Test
    void findSellersWithAmountLessThan_filtersBySumAndPeriod() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        BigDecimal threshold = new BigDecimal("500.00");

        Seller sellerLow = new Seller();
        sellerLow.setName("Low");
        sellerLow.setDeleted(false);
        sellerLow.setRegistrationDate(LocalDateTime.now());
        sellerLow.setContactInfo("slowtest@example.com");
        sellerLow = sellerRepository.save(sellerLow);

        Seller sellerHigh = new Seller();
        sellerHigh.setName("High");
        sellerHigh.setDeleted(false);
        sellerHigh.setRegistrationDate(LocalDateTime.now());
        sellerHigh.setContactInfo("shightest@example.com");
        sellerHigh = sellerRepository.save(sellerHigh);

        saveTransaction(sellerLow, new BigDecimal("300.00"), start.plusDays(1));
        saveTransaction(sellerHigh, new BigDecimal("600.00"), start.plusDays(2));

        List<Seller> result = sellerRepository.findSellersWithAmountLessThan(threshold, start, end);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Low");
    }

    @Test
    void findSellersWithAmountLessThan_includesSellersWithNoTransactions() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        BigDecimal threshold = new BigDecimal("100.00");

        Seller sellerNoTx = new Seller();
        sellerNoTx.setName("No Transactions");
        sellerNoTx.setDeleted(false);
        sellerNoTx.setRegistrationDate(LocalDateTime.now());
        sellerNoTx.setContactInfo("sNoTxtest@example.com");
        sellerRepository.save(sellerNoTx);

        List<Seller> result = sellerRepository.findSellersWithAmountLessThan(threshold, start, end);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("No Transactions");
    }


    private void saveTransaction(Seller seller, BigDecimal amount, LocalDateTime date) {
        saveTransaction(seller, amount, date, false);
    }

    private void saveTransaction(Seller seller, BigDecimal amount, LocalDateTime date, boolean deleted) {
        Transaction tx = new Transaction();
        tx.setSeller(seller);
        tx.setAmount(amount);
        tx.setPaymentType(PaymentType.CASH);
        tx.setTransactionDate(date);
        tx.setDeleted(deleted);
        tx.setVersion(0L);

        entityManager.persist(tx);
        entityManager.flush();
    }
}