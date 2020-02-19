package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.models.BankCard;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardDeleteDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardRegisterDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardUpdateDto;
import com.team14.virtualwallet.repositories.BankCardsRepository;
import com.team14.virtualwallet.services.constants.BankCardsServiceConstants;
import com.team14.virtualwallet.services.contracts.BankCardsService;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.utils.mappers.BankCardMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BankCardsServiceImpl implements BankCardsService {

    private final UsersService usersService;
    private final UserProfilesService userProfilesService;

    private final BankCardsRepository bankCardsRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public BankCardsServiceImpl(UsersService usersService, UserProfilesService userProfilesService, BankCardsRepository bankCardsRepository, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.userProfilesService = userProfilesService;
        this.bankCardsRepository = bankCardsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BankCard register(BankCardRegisterDto bankCardRegisterDto, String username) {
        if (!expirationDateIsValid(bankCardRegisterDto.getExpirationDate())) {
            throw new IllegalArgumentException(BankCardsServiceConstants.CARD_IS_EXPIRED);
        }

        BankCard bankCard = this.bankCardsRepository.findByCardNumber(bankCardRegisterDto.getCardNumber());
        UserProfile userProfile = this.userProfilesService.findByUser(this.usersService.findByUsername(username));

        if (bankCard != null && cardOwnerIsDifferent(bankCard, userProfile)) {
            throw new DuplicateEntityException(BankCardsServiceConstants.CARD_ALREADY_EXISTS);
        }

        bankCard = modelMapper.map(bankCardRegisterDto, BankCard.class);
        bankCardsRepository.save(bankCard);

        userProfile.getBankCards().add(bankCard);
        userProfilesService.saveToDb(userProfile);

        return bankCard;
    }

    @Override
    public BankCard update(BankCardUpdateDto bankCardUpdateDto, String username) {
        if (!expirationDateIsValid(bankCardUpdateDto.getExpirationDate())) {
            throw new EntityNotFoundException(BankCardsServiceConstants.CARD_IS_EXPIRED);
        }

        BankCard bankCard = this.bankCardsRepository.findByCardNumber(bankCardUpdateDto.getCardNumber());
        UserProfile userProfile = this.userProfilesService.findByUser(this.usersService.findByUsername(username));

        if (bankCard != null && cardOwnerIsDifferent(bankCard, userProfile)) {
            throw new DuplicateEntityException(BankCardsServiceConstants.CARD_ALREADY_EXISTS);
        }

        bankCard = getActiveCard(username);

        if (bankCard == null) {
            throw new EntityNotFoundException(String.format(BankCardsServiceConstants.CARD_DOESNT_EXIST, bankCardUpdateDto.getCardNumber()));
        }

        BankCardMapper.mapBankCardUpdateDtoToBankCard(bankCardUpdateDto, bankCard);
        bankCardsRepository.save(bankCard);

        return bankCard;
    }

    @Override
    public void delete(BankCardDeleteDto bankCardDeleteDto, String username) {
        BankCard bankCard = bankCardsRepository.findByCardNumber(bankCardDeleteDto.getCardNumber());

        if (bankCard == null) {
            throw new EntityNotFoundException(String.format(BankCardsServiceConstants.CARD_DOESNT_EXIST, bankCardDeleteDto.getCardNumber()));
        }

        UserProfile userProfile = this.userProfilesService.findByUser(this.usersService.findByUsername(username));

        if (cardOwnerIsDifferent(bankCard, userProfile)) {
            throw new DuplicateEntityException(String.format(BankCardsServiceConstants.CARD_BELONGS_TO_DIFFERENT_USER, bankCardDeleteDto.getCardNumber()));
        }

        bankCard.setCardNumber(UUID.randomUUID().toString());
        bankCard.setCsv("---");
        bankCard.setDeactivated(true);
        bankCardsRepository.save(bankCard);
    }

    public BankCard getActiveCard(String username) {
        UserProfile userProfile = this.userProfilesService.findByUser(this.usersService.findByUsername(username));
        return userProfile.getBankCards()
                    .stream()
                    .filter(card -> card.isDeactivated() == false)
                    .findFirst()
                    .orElse(null);
    }

    public void hasRegisteredCards(String username) {
        UserProfile userProfile = this.userProfilesService.findByUser(this.usersService.findByUsername(username));
        if (userProfile.getBankCards().isEmpty()) {
            throw new EntityNotFoundException(BankCardsServiceConstants.PLEASE_REGISTER_BANK_CARD);
        }

        Boolean hasActiveCards = false;
        for (BankCard card :
                userProfile.getBankCards()) {
            if (!card.isDeactivated()) {
                hasActiveCards = true;
                break;
            }
        }

        if (!hasActiveCards) {
            throw new EntityNotFoundException(BankCardsServiceConstants.PLEASE_REGISTER_BANK_CARD1);
        }
    }

    private boolean expirationDateIsValid(String expiationPeriod) {
        String[] expirationDateData = expiationPeriod.split("/");

        String expirationMonthString = expirationDateData[0];
        int expMonth = Integer.parseInt(expirationMonthString);

        String expirationYearString = expirationDateData[1];
        String expirationMillenniumAndCentury = getExpirationMillenniumAndCentury(expirationYearString);

        int expYear = Integer.parseInt(expirationMillenniumAndCentury + expirationYearString);

        if (expMonth == 0 || expMonth > 12) {
            return false;
        }

        LocalDate initialDate = LocalDate.of(expYear, expMonth, 1);
        LocalDate newBankCardExpirationDate = LocalDate.of(expYear, expMonth, initialDate.withDayOfMonth(initialDate.lengthOfMonth()).getDayOfMonth());
        LocalDate today = LocalDate.now();

        return newBankCardExpirationDate.isAfter(today);
    }

    private String getExpirationMillenniumAndCentury(String expirationYearString) {
        if ((LocalDate.now().getYear() == 98 || LocalDate.now().getYear() == 99) && (expirationYearString.equals("00") || expirationYearString.equals("01"))) {
            return Integer.toString(LocalDate.now().getYear() + 1).substring(0, 2);
        } else {
            return Integer.toString(LocalDate.now().getYear()).substring(0, 2);
        }
    }

    private BankCard convertToEntity(BankCardRegisterDto bankCardRegisterDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(bankCardRegisterDto, BankCard.class);
    }

    private boolean cardOwnerIsDifferent(BankCard newBankCard, UserProfile userProfile) {
         return !userProfile.getBankCards().contains(newBankCard);
    }
}