package com.team14.virtualwallet.utils.mappers;

import com.team14.virtualwallet.models.BankCard;
import com.team14.virtualwallet.models.dtos.apitopupdto.CardDetailsDto;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpDto;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpExtendedDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardPresentDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardUpdateDto;

import java.time.LocalDateTime;

public class BankCardMapper {

    public static final String BGN = "BGN";

    public static TopUpDto bankCardToTopUpDto(BankCard bankCard, TopUpDto topUpDto) {
        CardDetailsDto cardDetailsDto = new CardDetailsDto();
        cardDetailsDto.setCsv(topUpDto.getCsv());
        cardDetailsDto.setExpirationDate(bankCard.getExpirationDate());
        cardDetailsDto.setCardNumber(bankCard.getCardNumber());
        cardDetailsDto.setCardholderName(bankCard.getCardHolderName());

        topUpDto.setCardDetails(cardDetailsDto);
        topUpDto.setIdempotencyKey(LocalDateTime.now().toString() + bankCard.getCardHolderName());
        topUpDto.setCurrency(BGN);
        return topUpDto;
    }

    public static BankCardPresentDto BankCardToGetDto(BankCard bankCard) {
        BankCardPresentDto bankCardPresentDto = new BankCardPresentDto();
        if (bankCard == null) {
            return bankCardPresentDto;
        }
        bankCardPresentDto.setCardNumber(bankCard.getCardNumber());
        bankCardPresentDto.setCardHolderName(bankCard.getCardHolderName());
        bankCardPresentDto.setExpirationDate(bankCard.getExpirationDate());
        bankCardPresentDto.setCardIssuer(bankCard.getCardIssuer());
        bankCardPresentDto.setCsv(bankCard.getCsv());
        return bankCardPresentDto;
    }

    public static void mapBankCardUpdateDtoToBankCard(BankCardUpdateDto bankCardUpdateDto, BankCard bankCard) {
        bankCard.setCardNumber(!bankCardUpdateDto.getCardNumber().equals("") ? bankCardUpdateDto.getCardNumber() : bankCard.getCardNumber());
        bankCard.setExpirationDate(!bankCardUpdateDto.getExpirationDate().equals("") ? bankCardUpdateDto.getExpirationDate() : bankCard.getExpirationDate());
        bankCard.setCsv(!bankCardUpdateDto.getCsv().equals("") ? bankCardUpdateDto.getCsv() : bankCard.getCsv());
        bankCard.setCardIssuer(!bankCardUpdateDto.getCardIssuer().equals("") ? bankCardUpdateDto.getCardIssuer() : bankCard.getCardIssuer());
        bankCard.setCardHolderName(!bankCardUpdateDto.getCardHolderName().equals("") ? bankCardUpdateDto.getCardHolderName() : bankCard.getCardHolderName());
    }

    public static TopUpDto topUpExtendedDtoToTopUpDto(TopUpExtendedDto topUpExtendedDto) {
        TopUpDto topUpDto = new TopUpDto();
        topUpDto.setDescription(topUpExtendedDto.getDescription());
        topUpDto.setCsv(topUpExtendedDto.getCsv());
        topUpDto.setAmount(topUpExtendedDto.getAmount());
        topUpDto.setDescription(topUpExtendedDto.getDescription());
        return topUpDto;
    }
}
