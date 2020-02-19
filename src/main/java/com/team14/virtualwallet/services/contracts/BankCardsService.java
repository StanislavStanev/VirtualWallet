package com.team14.virtualwallet.services.contracts;

import com.team14.virtualwallet.models.BankCard;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardDeleteDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardUpdateDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardRegisterDto;

public interface BankCardsService {

    BankCard register(BankCardRegisterDto bankCardRegisterDto, String username);

    BankCard update(BankCardUpdateDto bankCardUpdateDto, String username);

    void delete(BankCardDeleteDto bankCardDeleteDto, String username);

    BankCard getActiveCard(String username);

    void hasRegisteredCards(String username);
}
