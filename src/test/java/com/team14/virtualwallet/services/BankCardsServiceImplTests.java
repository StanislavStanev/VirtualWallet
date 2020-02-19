package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.models.BankCard;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardDeleteDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardPresentDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardRegisterDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardUpdateDto;
import com.team14.virtualwallet.repositories.BankCardsRepository;
import com.team14.virtualwallet.repositories.UserProfilesRepository;
import com.team14.virtualwallet.services.contracts.BankCardsService;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import com.team14.virtualwallet.services.contracts.UsersService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.doReturn;

//@RunWith(MockitoJUnitRunner.class)
public class BankCardsServiceImplTests {
    private BankCard bankCard;
    private BankCard bankCard2;
    private BankCardsRepository bankCardsRepository;
    private UserProfilesRepository userProfilesRepository;
    private ModelMapper modelMapper;
    private BankCardPresentDto getBankCardDataDto;
    private BankCardRegisterDto bankCardRegisterDto;
    private BankCardPresentDto getBankCardDataDto2;
    private BankCardRegisterDto bankCardRegisterDto2;
    private BankCardUpdateDto bankCardUpdateDto;
    private BankCardDeleteDto bankCardDeleteDto;
    private BankCardsService bankCardsService;
    private UsersService usersService;
    private UserProfilesService userProfilesService;
    private User user;
    private UserProfile userProfile;
    private User user2;
    private UserProfile userProfile2;

    @Before
    public void init() {
        bankCard = new BankCard() {{
            setId(1L);
            setCardNumber("1111-1111-1111-1111");
            setCardHolderName("Radi Arnaudov");
            setCardIssuer("DSK Bank");
            setDeactivated(false);
            setExpirationDate("05/23");
            setCsv("829");
            setExpired(false);
        }};
        bankCard2 = new BankCard() {{
            //setId(2L);
            setCardNumber("2111-1111-1111-1111");
            setCardHolderName("Test2");
            setCardIssuer("DSK Bank");
            setDeactivated(false);
            setExpirationDate("05/23");
            setCsv("829");
            setExpired(false);
        }};
        Set<BankCard> bankCardSet = new HashSet<>();
        //bankCardSet.add(bankCard);
        Set<BankCard> bankCardSetEmpty = new HashSet<>();
        user = new User() {{
            setId(1L);
            setUsername("rarnaudov");
            setEmail("radi.arnaudov@gmail.com");
        }};
        userProfile = new UserProfile() {{
            setId(1L);
            setUser(user);
            setFullName("Radi Arnaudov");
            setBankCards(bankCardSet);
        }};
        user2 = new User() {{
            setId(2L);
            setUsername("user2");
            setEmail("user2@gmail.com");
        }};
        userProfile2 = new UserProfile() {{
            setId(2L);
            setUser(user);
            setFullName("User2");
            setBankCards(bankCardSetEmpty);
        }};

        modelMapper = Mockito.mock(ModelMapper.class);
        usersService = Mockito.mock(UsersService.class);
        userProfilesService = Mockito.mock(UserProfilesService.class);
        bankCardsRepository = Mockito.mock(BankCardsRepository.class);
        userProfilesRepository = Mockito.mock(UserProfilesRepository.class);
        getBankCardDataDto = new BankCardPresentDto() {{
            setCardIssuer("DSKBank");
            setExpirationDate("05/23");
            setCardHolderName("Radi Arnaudov");
            setCardNumber("1111-1111-1111-1111");
        }};
        bankCardRegisterDto = new BankCardRegisterDto() {{
            setCardNumber("1111-1111-1111-1111");
            setExpirationDate("05/23");
            setCsv("829");
            setCardIssuer("DSK Bank");
            setCardHolderName("Radi Arnaudov");
        }};
        getBankCardDataDto2 = new BankCardPresentDto() {{
            setCardIssuer("DSKBank");
            setExpirationDate("05/23");
            setCardHolderName("Test2");
            setCardNumber("2111-1111-1111-1111");
        }};
        bankCardRegisterDto2 = new BankCardRegisterDto() {{
            setCardNumber("2111-1111-1111-1111");
            setExpirationDate("05/23");
            setCsv("829");
            setCardIssuer("DSK Bank");
            setCardHolderName("Test2");
        }};
        bankCardUpdateDto = new BankCardUpdateDto();
        bankCardDeleteDto = new BankCardDeleteDto();

        bankCardsService = new BankCardsServiceImpl(usersService, userProfilesService, bankCardsRepository, modelMapper);
    }

    @Test(expected = DuplicateEntityException.class)
    public void whenRegisterBankCard_And_CardAlreadyInDbWithUserProfile_Then_ThrowsError() {
        //Arrange
        Mockito.when(bankCardsRepository.findByCardNumber("1111-1111-1111-1111"))
                .thenReturn(bankCard);
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user2);
        Mockito.when(userProfilesService.findByUser(user2))
                .thenReturn(userProfile2);
        //Act

        bankCardsService.register(bankCardRegisterDto, Mockito.anyString());
    }

    @Test
    public void when_NewBankCardAddedToUserBankCards_Then_CheckUserBankCardsSize() {

        //Arrange
        Mockito.when(bankCardsRepository.findByCardNumber("2111-1111-1111-1111"))
                .thenReturn(null);
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user2);
        Mockito.when(userProfilesService.findByUser(user2))
                .thenReturn(userProfile2);
        bankCardRegisterDto2.setExpirationDate("02/20");
        //Act
        bankCardsService.register(bankCardRegisterDto2, Mockito.anyString());
        //Assert
        Assert.assertEquals(1, userProfile2.getBankCards().size());
    }

    @Test
    public void when_1NewBankCardAddedToUserBankCards_Then_CheckUserBankCardsSize() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        Mockito.when(bankCardsRepository.findByCardNumber("1111-1111-1111-1111"))
                .thenReturn(null);
        bankCardsService.register(bankCardRegisterDto, Mockito.anyString());

        //Act
        bankCardsService.register(bankCardRegisterDto2, Mockito.anyString());
        //Assert
        Assert.assertEquals(1, userProfile.getBankCards().size());
    }

    @Test
    public void when_NewBankCardRegister_ThenCheckSavedBankCardAgainstDto() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString())).thenReturn(user2);
        Mockito.when(userProfilesService.findByUser(Mockito.any(User.class))).thenReturn(userProfile2);
        userProfile2.getBankCards().add(bankCard2);
        Mockito.when(bankCardsService.getActiveCard("user2")).thenReturn(bankCard);
        Mockito.when(userProfilesRepository.findByUser(user2)).thenReturn(java.util.Optional.ofNullable(userProfile2));
        Mockito.when(bankCardsRepository.findByCardNumber("2111-1111-1111-1111")).thenReturn(null);

        //Act
        bankCardsService.register(bankCardRegisterDto2, "user2");
        //Assert
        Assert.assertEquals(bankCardRegisterDto2.getCardNumber(), bankCardsService.getActiveCard(user2.getUsername()).getCardNumber());
        Assert.assertEquals(bankCardRegisterDto2.getCardHolderName(), bankCardsService.getActiveCard(user2.getUsername()).getCardHolderName());
        Assert.assertEquals(bankCardRegisterDto2.getCardIssuer(), bankCardsService.getActiveCard(user2.getUsername()).getCardIssuer());
        Assert.assertEquals(bankCardRegisterDto2.getCsv(), bankCardsService.getActiveCard(user2.getUsername()).getCsv());
        Assert.assertEquals(bankCardRegisterDto2.getExpirationDate(), bankCardsService.getActiveCard(user2.getUsername()).getExpirationDate());
    }

    //Test whether user has default 0 cards size
    @Test
    public void when_UserRegistered_Then_CheckInitialUserBankCardsSetSize() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        //Act
        // Assert
        Assert.assertEquals(0, userProfile.getBankCards().size());
    }

    //Test whether user has NO active cards
    @Test
    public void when_UserRegistered_Then_CheckUserHasNoActiveBankCards() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        //Act
        //Assert
        Assert.assertEquals(null, bankCardsService.getActiveCard(user.getUsername()));
    }

    //Test whether user HAS active cards
    @Test
    public void when_newCardRegistered_Then_CheckUserHasActiveCards() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        bankCardsService.register(bankCardRegisterDto, user.getUsername());
        //Act
        //Assert
        Assert.assertEquals(bankCardsService.register(bankCardRegisterDto, user.getUsername()), bankCardsService.getActiveCard(user.getUsername()));
    }

    //Test whether new card is the active card, after add card over existing one
    @Test
    public void when_SecondCardRegistered_Then_SecondCardIsActive() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        bankCardsService.register(bankCardRegisterDto, user.getUsername());
        //Act
        //Assert
        Assert.assertEquals(false, bankCardsService.getActiveCard(user.getUsername()).isDeactivated());
    }

//    //Test whether old card is deactivated
//    @Test
//    public void when_SecondCardRegistered_Then_FirstCardDeactivated() {
//        //Arrange
//        Mockito.when(usersService.findByUsername(user.getUsername()))
//                .thenReturn(user);
//        Mockito.when(userProfilesService.findByUser(user))
//                .thenReturn(userProfile);
//        ModelMapper modelMapper = new ModelMapper();
//
//        BankCard beforeSaveBankCard = modelMapper.map(bankCardRegisterDto, BankCard.class);
//        BankCard beforeSaveBankCard2 = modelMapper.map(bankCardRegisterDto2, BankCard.class);
//        Mockito.when(bankCardsRepository.save(beforeSaveBankCard)).thenReturn(bankCard);
//        Mockito.when(bankCardsRepository.save(beforeSaveBankCard2)).thenReturn(bankCard2);
//
//        //Act
//        //Add first Bank Card
//        bankCardsService.register(bankCardRegisterDto, user.getUsername());
//        //Because of problem with manipulation users cards manually do this here
//        //Cannot handle line //userProfilesService.saveToDb(userProfile); -> Here Set<BankCard> is with one element, but this element is with null values
//        //What to do here
//        userProfile.getBankCards().clear();
//        userProfile.getBankCards().add(bankCard);
//        bankCardsService.register(bankCardRegisterDto2, user.getUsername());
//        userProfile.getBankCards().clear();
//        userProfile.getBankCards().add(bankCard);
//        userProfile.getBankCards().add(bankCard2);
//        Mockito.when(bankCardsRepository.findByCardNumber(bankCardRegisterDto.getCardNumber()))
//                .thenReturn(bankCard);
//        bankCardsService.register(bankCardRegisterDto, user.getUsername());
//        //Assert
//        Assert.assertEquals(true, bankCard2.isDeactivated());
//    }

    //Test whether size of bank cards is 2, after 2 cards added
//    @Test
//    public void when_UserHas2Cards_Then_CheckBankCardsSetSizeEquals2() {
//        //Arrange
//        Mockito.when(usersService.findByUsername(Mockito.anyString()))
//                .thenReturn(user);
//        Mockito.when(userProfilesService.findByUser(user))
//                .thenReturn(userProfile);
//        //Act
//        bankCardsService.register(bankCardRegisterDto, user.getUsername());
//        bankCardsService.register(bankCardRegisterDto2, user.getUsername());
//        //Assert
//        Assert.assertEquals(userProfile.getBankCards().size(), 2);
//    }

    //Test whether size of bank cards is 1, after 2 SAME cards added
//    @Test
//    public void when_OneCardAddedTwice_Then_UserHasOnly1RegisteredCard() {
//        //Arrange
//        Mockito.when(usersService.findByUsername(Mockito.anyString()))
//                .thenReturn(user);
//        Mockito.when(userProfilesService.findByUser(user))
//                .thenReturn(userProfile);
//
//        //Act
//        bankCardsService.register(bankCardRegisterDto, user.getUsername());
//        Mockito.when(bankCardsRepository.findByCardNumber(bankCardRegisterDto.getCardNumber())).thenReturn(bankCard);
//        userProfile.getBankCards().clear();
//        userProfile.getBankCards().add(bankCard);
//        bankCardsService.register(bankCardRegisterDto, user.getUsername());
//        //Assert
//        Assert.assertEquals(userProfile.getBankCards().size(), 1);
//    }

    //Test whether user has a register card
    @Test(expected = EntityNotFoundException.class)
    public void whenUserIsRegistered_Then_CheckUserHasRegisteredCard() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        //Act
        bankCardsService.hasRegisteredCards(user.getUsername());
        //Assert
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenUserDeactivateCard_Then_CheckUserHasNORegisteredCard() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        userProfile.getBankCards().add(bankCard);
        bankCard.setDeactivated(true);
        //Act
        bankCardsService.hasRegisteredCards(user.getUsername());
        //Assert
    }

    //If no exception is thrown in check_whether_UserHasRegisteredCard(), then test is successful
    @Test
    public void whenCardAddedToUser_Then_CheckUserHasRegisteredCard() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        userProfile.getBankCards().add(bankCard);
        //Act
        bankCardsService.hasRegisteredCards(user.getUsername());
        //Assert
    }

    //Verify repository is called once
    @Test
    public void whenRepositoryIsCalledByCardNumber_Then_CheckRepositoryIsCalledOnce() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);

        //Act
        bankCardsService.register(bankCardRegisterDto, user.getUsername());
        //Assert
        Mockito.verify(bankCardsRepository).findByCardNumber(bankCardRegisterDto.getCardNumber());
    }

    @Test(expected = EntityNotFoundException.class)
    public void when_PassingOlderThanTodayDate_ThrowsException() {
        //Arrange
        bankCardUpdateDto.setExpirationDate("01/05");
        //Assert
        bankCardsService.update(bankCardUpdateDto, Mockito.anyString());
        //Act
    }

    @Test(expected = EntityNotFoundException.class)
    public void when_PassingInvalidDate_ThrowsException() {
        //Arrange
        bankCardUpdateDto.setExpirationDate("00/05");
        //Assert
        bankCardsService.update(bankCardUpdateDto, Mockito.anyString());
        //Act
    }

    @Test(expected = DuplicateEntityException.class)
    public void when_UpdatingBankCard_numberAlreadyInDB_ThrowsException() {
        //Arrange
        Mockito.when(bankCardsRepository.findByCardNumber(Mockito.anyString()))
                .thenReturn(bankCard);
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);

        bankCardUpdateDto.setExpirationDate("01/25");
        bankCardUpdateDto.setCardNumber("1111-1111-1111-1111");

        //Act
        bankCardsService.update(bankCardUpdateDto, Mockito.anyString());
    }

    @Test(expected = EntityNotFoundException.class)
    public void when_UpdateBankCardUserHasNoActiveCard_ThrowsException() {
        //Arrange
        Mockito.when(bankCardsRepository.findByCardNumber(Mockito.anyString()))
                .thenReturn(null);
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);

        bankCardUpdateDto.setExpirationDate("01/25");
        bankCardUpdateDto.setCardNumber("1111-1111-1111-1234");

        //Act
        bankCardsService.update(bankCardUpdateDto, Mockito.anyString());
    }

    @Test
    public void when_UpdateBankCard_BankCardInfo_changedSuccessfully() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        userProfile.getBankCards().add(bankCard);

        bankCardUpdateDto.setCardNumber("4321-4321-4321-4321");
        bankCardUpdateDto.setExpirationDate("12/25");
        bankCardUpdateDto.setCsv("829");
        bankCardUpdateDto.setCardIssuer("DSK Bank");
        bankCardUpdateDto.setCardHolderName("Radi Arnaudov");

        //Act
        bankCardsService.update(bankCardUpdateDto, Mockito.anyString());

        //Assert
        Assert.assertEquals(bankCardUpdateDto.getCardNumber(), bankCard.getCardNumber());
    }

    @Test(expected = EntityNotFoundException.class)
    public void when_deleteNullBankCard_throwsException() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);

        //Act
        bankCardsService.delete(bankCardDeleteDto, Mockito.anyString());
    }

    @Test(expected = DuplicateEntityException.class)
    public void when_deleteCardOfAnotherUser_throwsException() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        Mockito.when(bankCardsRepository.findByCardNumber(Mockito.any()))
                .thenReturn(bankCard);
        bankCardDeleteDto.setCardNumber("1111-1111-1111-1111");

        userProfile.getBankCards().add(bankCard2);

        //Act
        bankCardsService.delete(bankCardDeleteDto, Mockito.anyString());
    }

    @Test
    public void when_deleteCardWithCorrectData_successfulDelete() {
        //Arrange
        Mockito.when(usersService.findByUsername(Mockito.anyString()))
                .thenReturn(user);
        Mockito.when(userProfilesService.findByUser(user))
                .thenReturn(userProfile);
        Mockito.when(bankCardsRepository.findByCardNumber(Mockito.any()))
                .thenReturn(bankCard);
        bankCardDeleteDto.setCardNumber("1111-1111-1111-1111");

        userProfile.getBankCards().add(bankCard);
        //Act

        bankCardsService.delete(bankCardDeleteDto, Mockito.anyString());
        //Assert

        Assert.assertEquals(bankCard.getCsv(), "---");
        Assert.assertEquals(bankCard.isDeactivated(), true);
    }
}
