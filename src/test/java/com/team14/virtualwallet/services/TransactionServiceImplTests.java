package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.AccessDeniedException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.exceptions.NotSufficientFundsException;
import com.team14.virtualwallet.models.*;
import com.team14.virtualwallet.models.dtos.apitopupdto.CardDetailsDto;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpDto;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpExtendedDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionCreateDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPageDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPresentDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionTimeframeDataDto;
import com.team14.virtualwallet.repositories.TransactionsRepository;
import com.team14.virtualwallet.repositories.UserProfilesRepository;
import com.team14.virtualwallet.repositories.UsersRepository;
import com.team14.virtualwallet.repositories.WalletsRepository;
import com.team14.virtualwallet.services.contracts.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class TransactionServiceImplTests {

    private UsersService usersService;
    private UserProfilesService usersProfileService;
    private TransactionsRepository transactionsRepository;
    private WalletsRepository walletsRepository;
    private BankCardsService bankCardsService;
    private UsersRepository usersRepository;
    private UserProfilesRepository userProfilesRepository;
    private TransactionsService transactionsService;
    private TransactionCreateDto transactionCreateDto;
    private User sender;
    private UserProfile senderUserProfile;
    private User receiver;
    private UserProfile receiverUserProfile;
    private Wallet wallet;
    private Wallet wallet2;
    private Transaction transaction;
    private Transaction transactionTopUp;
    private TopUpDto topUpDto;
    private TopUpExtendedDto topUpDtoExtend;
    private CardDetailsDto cardDetailsDto;
    private BankCard bankCard;
    private WalletsService walletsService;
    private ConfirmationTokensService confirmationTokensService;

    @Before
    public void init() {
        usersService = Mockito.mock(UsersService.class);
        usersProfileService = Mockito.mock(UserProfilesService.class);
        transactionsRepository = Mockito.mock(TransactionsRepository.class);
        walletsRepository = Mockito.mock(WalletsRepository.class);
        bankCardsService = Mockito.mock(BankCardsService.class);
        usersRepository = Mockito.mock(UsersRepository.class);
        userProfilesRepository = Mockito.mock(UserProfilesRepository.class);
        walletsService = Mockito.mock(WalletsService.class);
        confirmationTokensService = Mockito.mock(ConfirmationTokensService.class);
        transactionsService = new TransactionsServiceImpl(usersService,
                usersProfileService,
                walletsService,
                confirmationTokensService,
                transactionsRepository,
                bankCardsService);
        wallet = new Wallet() {{
            setId(1L);
            setIsDefault(true);
            setName("Default");
        }};

        wallet2 = new Wallet() {{
            setId(2L);
            setIsDefault(true);
            setName("Default");
        }};
        sender = new User() {{
            setId(1L);
            setUsername("rarnaudov");
            setEmail("radi.arnaudov@gmail.com");
        }};

        senderUserProfile = new UserProfile() {{
            setId(1L);
            setUser(sender);
            setFullName("Radi Arnaudov");
            getWallets().add(wallet);
            setPhoneNumber("0884408213");
        }};

        receiver = new User() {{
            setId(2L);
            setUsername("test");
            setEmail("test@gmail.com");
        }};

        receiverUserProfile = new UserProfile() {{
            setId(2L);
            setUser(receiver);
            setFullName("Test");
            getWallets().add(wallet2);
        }};

        transactionCreateDto = new TransactionCreateDto() {{
            setSenderUserName(sender.getUsername());
            setNote("Test");
            setAmount(BigDecimal.valueOf(100L));
            setReceiver(receiver.getUsername());
            setReceiverDataType("username");
            setWalletName(wallet.getName());
            setWalletName2(wallet2.getName());
            setChecksum("9abd8ec5c35b6006759ab3731ae428ee");
        }};

        transaction = new Transaction() {{
            setReceiver(receiverUserProfile);
            setSender(senderUserProfile);
            setNote("Test");
            setExecutedOn(LocalDateTime.now());
            setAmount(BigDecimal.valueOf(100L));
            setTopUp(false);
            setId(1L);
            setSenderWallet(wallet);
            setReceiverWallet(wallet2);
        }};

        transactionTopUp = new Transaction() {{
            setReceiver(senderUserProfile);
            setSender(senderUserProfile);
            setNote("Test");
            setExecutedOn(LocalDateTime.now());
            setAmount(BigDecimal.valueOf(90L));
            setTopUp(true);
            setId(2L);
        }};

        cardDetailsDto = new CardDetailsDto() {{
            setCardholderName("Radi Arnaudov");
            setCardNumber("1111-1111-1111-1111");
            setExpirationDate("05/23");
            setCsv("555");
        }};

        topUpDto = new TopUpDto() {{
            setAmount(BigDecimal.valueOf(90));
            setCurrency("BGN");
            setCsv("555");
            setCardDetails(cardDetailsDto);
            setDescription("Test");
        }};

        topUpDtoExtend = new TopUpExtendedDto() {{
            setAmount(BigDecimal.valueOf(90));
            setCurrency("BGN");
            setCsv("555");
            setCardDetails(cardDetailsDto);
            setDescription("Test");
            setWalletName(wallet.getName());
        }};

        bankCard = new BankCard() {{
            setId(1L);
            setDeactivated(false);
            setCardHolderName("Radi Arnaudov");
            setCardIssuer("Bank");
            setExpirationDate("05/23");
            setCsv("555");
            setExpired(false);
            setCardNumber("1111-1111-1111-1111");
        }};
    }

    @Test(expected = NotSufficientFundsException.class)
    public void whenCreateTransactionSendMoney_Then_NotEnoughMoney() {
        wallet.setBalance(BigDecimal.valueOf(0));
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersService.findByUsername(receiver.getUsername())).thenReturn(receiver);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(usersProfileService.findByUser(receiver)).thenReturn(receiverUserProfile);
        transactionsService.create(transactionCreateDto, sender.getUsername(), receiver.getUsername(), false, wallet, wallet2);
    }

    @Test
    public void whenCreateTransactionSendMoney_Then_Success() {
        wallet.setBalance(BigDecimal.valueOf(100));
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersService.findByUsername(receiver.getUsername())).thenReturn(receiver);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(usersProfileService.findByUser(receiver)).thenReturn(receiverUserProfile);
        Mockito.when(transactionsRepository.save(any(Transaction.class))).thenReturn(transaction);
        Mockito.when(walletsRepository.save(wallet)).thenReturn(wallet);
        Mockito.when(walletsRepository.save(wallet2)).thenReturn(wallet2);
        Transaction transaction2 = transactionsService.create(transactionCreateDto, sender.getUsername(), receiver.getUsername(), false, wallet, wallet2);
        Assert.assertEquals(transaction, transaction2);
    }

    @Test(expected = AccessDeniedException.class)
    public void whenSendMoneyFromSharedWallet_Then_ThrowExceptionBecauseNoPermissions() {
        transactionCreateDto.setReceiver("rarnaudov");
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setShared(true);
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersService.findByUsername(receiver.getUsername())).thenReturn(receiver);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(usersProfileService.findByUser(receiver)).thenReturn(receiverUserProfile);
        Mockito.when(walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName())).thenReturn(wallet);
        Mockito.when(walletsService.getSharedWalletRole(senderUserProfile.getId(), wallet.getId())).thenReturn(0L);
        Transaction transaction2 = transactionsService.preCreate(transactionCreateDto);
    }

    @Test(expected = AccessDeniedException.class)
    public void whenAddMoneyFromSharedWallet_Then_ThrowExceptionBecauseNoPermissions() {
        transactionCreateDto.setReceiver(sender.getUsername());
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setShared(true);
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName())).thenReturn(wallet);
        Mockito.when(walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName2())).thenReturn(wallet);
        Mockito.when(walletsService.getSharedWalletRole(senderUserProfile.getId(), wallet.getId())).thenReturn(2L);
        Transaction transaction2 = transactionsService.preCreate(transactionCreateDto);
    }

    @Test
    public void whenPreCreateTransaction_Then_Success() {
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setShared(false);
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName())).thenReturn(wallet);
        Mockito.when(walletsService.getSharedWalletRole(senderUserProfile.getId(), wallet.getId())).thenReturn(2L);
        Mockito.when(transactionsRepository.save(any(Transaction.class))).thenReturn(transaction);
        Mockito.when(walletsRepository.save(wallet)).thenReturn(wallet);
        Mockito.when(walletsRepository.save(wallet2)).thenReturn(wallet2);
        Transaction transaction2 = transactionsService.preCreate(transactionCreateDto);
        Assert.assertEquals(transaction, transaction2);
    }


    @Test(expected = AccessDeniedException.class)
    public void whenAddMoneyFromSharedWallet_Then_CheckSumError() {
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setShared(false);
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName())).thenReturn(wallet);
        Mockito.when(walletsService.getSharedWalletRole(senderUserProfile.getId(), wallet.getId())).thenReturn(2L);
        Mockito.when(transactionsRepository.save(any(Transaction.class))).thenReturn(transaction);
        Mockito.when(walletsRepository.save(wallet)).thenReturn(wallet);
        Mockito.when(walletsRepository.save(wallet2)).thenReturn(wallet2);
        transactionCreateDto.setChecksum("");
        Transaction transaction2 = transactionsService.preCreate(transactionCreateDto);
        Assert.assertEquals(transaction, transaction2);
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenTopUpUserWallet_Then_ThrowExceptionBankCardNotFound() throws IOException {
        transactionsService.topUpUserWallet(topUpDtoExtend, sender.getUsername());
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenTopUpUserWallet_Then_ThrowExceptionCSVNotMatching() throws IOException {
        Mockito.when(bankCardsService.getActiveCard(sender.getUsername())).thenReturn(bankCard);
        bankCard.setCsv("999");
        transactionsService.topUpUserWallet(topUpDtoExtend, sender.getUsername());
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenTopUpUserWallet_Then_ThrowExceptionWalletNotFound() throws IOException {
        Mockito.when(bankCardsService.getActiveCard(sender.getUsername())).thenReturn(bankCard);
        transactionsService.topUpUserWallet(topUpDtoExtend, sender.getUsername());
    }

    @Test(expected = AccessDeniedException.class)
    public void whenTopUpUserWallet_Then_CheckUserHasNoRightsForSharedWallet() throws IOException {
        topUpDtoExtend.setWalletName(wallet.getName());
        topUpDtoExtend.setCsv(bankCard.getCsv());
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(bankCardsService.getActiveCard(sender.getUsername())).thenReturn(bankCard);
        Mockito.when(walletsService.findByName(Mockito.any(UserProfile.class), Mockito.anyString())).thenReturn(wallet);
        Mockito.when(walletsService.getSharedWalletRole(Mockito.anyLong(), Mockito.anyLong())).thenReturn(2L);
        wallet.setShared(true);
        transactionsService.topUpUserWallet(topUpDtoExtend, sender.getUsername());
    }

    @Test
    public void whenTopUpUserWallet_Then_Success() throws IOException {
        topUpDtoExtend.setWalletName(wallet.getName());
        topUpDtoExtend.setCsv(bankCard.getCsv());
        wallet.setBalance(BigDecimal.ZERO);
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(bankCardsService.getActiveCard(sender.getUsername())).thenReturn(bankCard);
        Mockito.when(walletsService.findByName(Mockito.any(UserProfile.class), Mockito.anyString())).thenReturn(wallet);
        Mockito.when(walletsService.getSharedWalletRole(Mockito.anyLong(), Mockito.anyLong())).thenReturn(2L);
        wallet.setShared(false);
        Mockito.when(transactionsRepository.save(any(Transaction.class))).thenReturn(transactionTopUp);
        Transaction transactionTopUpTest = transactionsService.topUpUserWallet(topUpDtoExtend, sender.getUsername());
        Assert.assertEquals(transactionTopUp, transactionTopUpTest);
    }

    @Test
    public void whenGetTimeFrameData_Then_checkTranscationCountSize7Days() {
        TransactionTimeframeDataDto transactionTimeframeDataDto = new TransactionTimeframeDataDto();
        transactionTimeframeDataDto = transactionsService.getTimeframeData("7days", "rarnaudov", false);
        Assert.assertEquals(7, transactionTimeframeDataDto.getTransactionCounts().size());
    }

    @Test
    public void whenGetTimeFrameData_Then_checkTranscationCountSize28Days() {
        TransactionTimeframeDataDto transactionTimeframeDataDto = new TransactionTimeframeDataDto();
        transactionTimeframeDataDto = transactionsService.getTimeframeData("28days", "rarnaudov", false);
        Assert.assertEquals(4, transactionTimeframeDataDto.getTransactionCounts().size());
    }

    @Test
    public void whenGetTransactionPage_Then_checkTransactionPageDtoSize() {
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        Page<Transaction> pageTransactions = new PageImpl(transactionList);
        Pageable pageable = Mockito.mock(Pageable.class);
        Mockito.when(transactionsRepository.findTransactionsAdminPage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(pageTransactions);
        TransactionPageDto transactionPageDto = transactionsService.getTransactionPage(1, "01/01/2020", "03/03/2020", "rarnaudov", "test", "OUTGOING", "rarnaudov", "amount-ascending");
        Assert.assertEquals(transactionPageDto.getTransactions().size(), transactionList.size());
    }

    @Test
    public void whenGetTransactionPage_Then_checkTransactionPageDtoSize2() {
        List<Transaction> transactionList = new ArrayList<>();
        Role role = new Role();
        role.setAuthority("ADMIN");
        sender.getAuthorities().add(role);
        transactionList.add(transaction);
        Page<Transaction> pageTransactions = new PageImpl(transactionList);
        Pageable pageable = Mockito.mock(Pageable.class);
        Mockito.when(transactionsRepository.findTransactionsAdminPage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(pageTransactions);
        Mockito.when(usersService.userIsAdmin(sender.getUsername())).thenReturn(true);
        TransactionPageDto transactionPageDto = transactionsService.getTransactionPage(1, "01/01/2020", "03/03/2020", "rarnaudov", "test", "OUTGOING", "rarnaudov", "amount-ascending");
        Assert.assertEquals(transactionPageDto.getTransactions().size(), transactionList.size());
    }

    @Test
    public void whenGetTransactionPage_Then_checkTransactionPageDtoSize3() {
        List<Transaction> transactionList = new ArrayList<>();
        Role role = new Role();
        role.setAuthority("ADMIN");
        sender.getAuthorities().add(role);
        transactionList.add(transaction);
        Page<Transaction> pageTransactions = new PageImpl(transactionList);
        Pageable pageable = Mockito.mock(Pageable.class);
        Mockito.when(transactionsRepository.findTransactionsAdminPage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(pageTransactions);
        Mockito.when(usersService.userIsAdmin(sender.getUsername())).thenReturn(true);
        TransactionPageDto transactionPageDto = transactionsService.getTransactionPage(1, "01/01/2020", "03/03/2020", "rarnaudov", "", "OUTGOING", "rarnaudov", "amount-ascending");
        Assert.assertEquals(transactionPageDto.getTransactions().size(), transactionList.size());
    }

    @Test
    public void whenGetOldestTransactionsByCount_Then_checkDtoSize3() {
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        Page<Transaction> pageTransactions = new PageImpl(transactionList);
        Mockito.when(transactionsRepository.findAllUserTransactions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(pageTransactions);
        List<TransactionPresentDto> transactionPresentDtos = transactionsService.getOldestTransactionsByCount("rarnaudov", "OUTGOING", false, 1);
        Assert.assertEquals(transactionList.size(),transactionPresentDtos.size());
    }

    @Test(expected = AccessDeniedException.class)
    public void whenSendMoneyOverLargeTransaction_Then_ThrowException() {
        transactionCreateDto.setAmount(BigDecimal.valueOf(10000));
        wallet.setBalance(BigDecimal.valueOf(10000));
        wallet.setShared(false);
        wallet2.setShared(false);
        Mockito.when(usersService.findByUsername(sender.getUsername())).thenReturn(sender);
        Mockito.when(usersService.findByUsername(receiver.getUsername())).thenReturn(receiver);
        Mockito.when(usersProfileService.findByUser(sender)).thenReturn(senderUserProfile);
        Mockito.when(usersProfileService.findByUser(receiver)).thenReturn(receiverUserProfile);
        Mockito.when(walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName())).thenReturn(wallet);
        Mockito.when(walletsService.getSharedWalletRole(senderUserProfile.getId(), wallet.getId())).thenReturn(0L);
        Transaction transaction2 = transactionsService.preCreate(transactionCreateDto);
    }


}
