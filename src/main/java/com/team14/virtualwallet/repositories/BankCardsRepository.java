package com.team14.virtualwallet.repositories;

import com.team14.virtualwallet.models.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCardsRepository extends JpaRepository<BankCard, Long> {
    BankCard findByCardNumber(String cardNumber);
}
