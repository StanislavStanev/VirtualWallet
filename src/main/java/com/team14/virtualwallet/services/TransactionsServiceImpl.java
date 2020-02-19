package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.AccessDeniedException;
import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.exceptions.NotSufficientFundsException;
import com.team14.virtualwallet.models.*;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpDto;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpExtendedDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionCreateDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPageDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPresentDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionTimeframeDataDto;
import com.team14.virtualwallet.repositories.TransactionsRepository;
import com.team14.virtualwallet.services.contracts.*;
import com.team14.virtualwallet.utils.ModelFactory;
import com.team14.virtualwallet.utils.mappers.BankCardMapper;
import com.team14.virtualwallet.utils.mappers.TransactionMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    public static final String NOT_ENOUGH_MONEY = "You don't have enough money.";
    public static final String CSV_CODE_NOT_MATCHING = "CSV code not matching";
    public static final String REGISTER_BANK_CARD_FIRST = "You have no active bank card. Please register bank card first in order to proceed to topup";
    public static final String CANT_ADD_MONEY_TO_SHARED_WALLET = "You cannot add money to this shared wallet";
    public static final String CANT_SEND_MONEY_FROM_SHARED_WALLET = "You cannot send money from this shared wallet";
    public static final int SEND_MONEY_SHARED_WALLET_PERMISSION = 2;
    public static final int ADD_MONEY_SHARED_WALLET_PERMISSION = 1;
    public static final String WALLET_NOT_FOUND = "Wallet not found";
    public static final String REPEATING_TRANSACTION_IN_LAST_MINUTE = "You have such transaction last 1 minute, please try again later.";
    public static final String TRANSACTION_DENIED_PLEASE_CONTACT_SUPPORT = "TRANSACTION DENIED. PLEASE CONTACT SUPPORT!!!";
    public static final long ONE_MINUTE = 1L;
    public static final int TRANSACTION_VERIFICATION_AMOUNT = 10000;
    private static final int MAX_TRANSACTIONS_PER_PAGE = 5;
    private static final String WRONG_SEARCH_CRITERIA = "You're trying to enter incorrect search criteria.";
    private static final String ASCENDING = "ascending";
    private final UsersService usersService;
    private final UserProfilesService usersProfileService;
    private final WalletsService walletsService;
    private final ConfirmationTokensService confirmationTokensService;
    private final TransactionsRepository transactionsRepository;
    private final BankCardsService bankCardsService;

    @Autowired
    public TransactionsServiceImpl(UsersService usersService,
                                   UserProfilesService usersProfileService,
                                   WalletsService walletsService,
                                   ConfirmationTokensService confirmationTokensService,
                                   TransactionsRepository transactionsRepository,
                                   BankCardsService bankCardsService) {
        this.usersService = usersService;
        this.usersProfileService = usersProfileService;
        this.walletsService = walletsService;
        this.confirmationTokensService = confirmationTokensService;
        this.transactionsRepository = transactionsRepository;
        this.bankCardsService = bankCardsService;
    }

    @Override
    public List<Transaction> getAll() {
        return this.transactionsRepository.findAll();
    }

    @Override
    @Transactional
    public Transaction preCreate(TransactionCreateDto transactionCreateDto) {

        validateTransactionWithEmailSend(transactionCreateDto);

        Wallet receiverWallet = null;
        String receiver = transactionCreateDto.getReceiver();

        UserProfile senderUserProfile = usersProfileService.findByUser(
                usersService.findByUsername(transactionCreateDto.getSenderUserName()));

        Wallet senderWallet = walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName());
        if (senderWallet.isShared()) {
            Long sharedWalledRole = walletsService.getSharedWalletRole(senderUserProfile.getId(), senderWallet.getId());
            if (sharedWalledRole == ADD_MONEY_SHARED_WALLET_PERMISSION || sharedWalledRole == 0) {
                throw new AccessDeniedException(CANT_SEND_MONEY_FROM_SHARED_WALLET);
            }
        }
        //Business case when user transfer money between 2 own wallets
        if (transactionCreateDto.getSenderUserName().equals(transactionCreateDto.getReceiver())) {
            receiverWallet = walletsService.findByName(senderUserProfile, transactionCreateDto.getWalletName2());
            if (receiverWallet.isShared()) {
                Long shareWalletRole = walletsService.getSharedWalletRole(senderUserProfile.getId(), senderWallet.getId());
                if (shareWalletRole == SEND_MONEY_SHARED_WALLET_PERMISSION || shareWalletRole == 0)
                    throw new AccessDeniedException(CANT_ADD_MONEY_TO_SHARED_WALLET);
            }
        }

        checkSumControl(transactionCreateDto);

        return create(transactionCreateDto, transactionCreateDto.getSenderUserName(), receiver, false, senderWallet, receiverWallet);
    }

    private void validateTransactionWithEmailSend(TransactionCreateDto transactionCreateDto) {
        if (transactionCreateDto.getAmount().compareTo(BigDecimal.valueOf(TRANSACTION_VERIFICATION_AMOUNT)) >= 0) {
            confirmationTokensService.validateTransactionToken(transactionCreateDto.getSenderUserName(), transactionCreateDto.getToken());
        }
    }

    private void checkSumControl(TransactionCreateDto transactionCreateDto) {
        String code = transactionCreateDto.getSenderUserName() + transactionCreateDto.getReceiver() + transactionCreateDto.getAmount();
        String md5Hex = DigestUtils.md5Hex(code);
        if (!md5Hex.equals(transactionCreateDto.getChecksum())) {
            throw new AccessDeniedException(TRANSACTION_DENIED_PLEASE_CONTACT_SUPPORT);
        }
    }

    @Override
    public Transaction create(TransactionCreateDto transactionCreateDto, String senderUsername, String receiverUsername, Boolean isTopUp, Wallet senderWallet, Wallet receiverWallet) {
        User sender = this.usersService.findByUsername(senderUsername);
        User receiver = this.usersService.findByUsername(receiverUsername);

        UserProfile senderUserProfile = this.usersProfileService.findByUser(sender);
        UserProfile receiverUserProfile = this.usersProfileService.findByUser(receiver);

        if (!isTopUp) {
            availabilityCheckForSenderBalance(transactionCreateDto, isTopUp, senderWallet);
        }

        Transaction transaction = ModelFactory.createTransaction();

        transaction.setAmount(transactionCreateDto.getAmount());
        transaction.setNote(transactionCreateDto.getNote());

        LocalDateTime transactionTimeStamp = LocalDateTime.now();
        transaction.setExecutedOn(transactionTimeStamp);

        transaction.setSender(senderUserProfile);
        transaction.setReceiver(receiverUserProfile);

        transaction.setTopUp(isTopUp);

        if (!isTopUp) {
            transaction.setSenderWallet(senderWallet);
            if (receiverWallet == null) {
                transaction.setReceiverWallet(walletsService.getDefault(receiverUserProfile));
            } else {
                transaction.setReceiverWallet(receiverWallet);
            }
        } else {
            transaction.setReceiverWallet(senderWallet);
        }

        LocalDateTime lastTransactionTime = getLastTransactionTime(transaction.getSender(),
                transaction.getSenderWallet(),
                transaction.getReceiver(),
                transaction.getReceiverWallet(),
                transaction.getAmount());
        isRepeatingTransaction(lastTransactionTime);

        transaction = this.transactionsRepository.save(transaction);

        //General logic is - on top up only receiver balance is updated
        //On send money take from sender-->add to receiver
        if (!isTopUp) {
            walletsService.subtractFromBalance(transaction, senderWallet);
        }

        if (!isTopUp && !senderUserProfile.equals(receiverUserProfile)) {
            //User send to other user
            walletsService.addToBalance(receiverUserProfile, transaction, null, isTopUp);
        } else if (!isTopUp && senderUserProfile.equals(receiverUserProfile)) {
            //User transfer between wallets
            walletsService.addToBalance(receiverUserProfile, transaction, receiverWallet, isTopUp);
        } else {
            //TopUp
            walletsService.addToBalance(senderUserProfile, transaction, senderWallet, isTopUp);
        }

        return transaction;
    }

    @Override
    public TransactionPageDto getTransactionPage(int page, String fromDate, String toDate, String senderUsername, String recipientUsername, String typeValue, String username, String sort) {
        boolean isAdmin = usersService.userIsAdmin(username);
        fromDate = formatDate(fromDate);
        toDate = formatDate(toDate);

        Pageable pageable = setPageable(page, sort);
        Page<Transaction> pageTransactions = null;
        List<String> senders = null;
        List<String> recipients = null;

        if (isAdmin) {
            if (recipientUsername.equals(senderUsername)) {
                pageTransactions = transactionsRepository.findAllUserTransactions(senderUsername, recipientUsername, fromDate, toDate, pageable);
            } else if (!senderUsername.equals("")) {
                if (typeValue.equals("ALL")) {
                    pageTransactions = transactionsRepository.findAllUserTransactions(senderUsername, senderUsername, fromDate, toDate, pageable);
                } else {
                    pageTransactions = transactionsRepository.findTransactionsAdminPage(senderUsername, "", fromDate, toDate, pageable);
                }
            } else if (!recipientUsername.equals("")) {
                if (typeValue.equals("ALL")) {
                    pageTransactions = transactionsRepository.findAllUserTransactions(recipientUsername, recipientUsername, fromDate, toDate, pageable);
                } else {
                    pageTransactions = transactionsRepository.findTransactionsAdminPage("", recipientUsername, fromDate, toDate, pageable);
                }
            } else {
                throw new IllegalArgumentException(WRONG_SEARCH_CRITERIA);
            }

            senders = usersService.getAllUsernames();
            recipients = senders;
        } else {
            //TODO faulty logic for error handling. Need to find a better solution.
            if (typeValue.equals("INCOMING") && !recipientUsername.equals("")) {
                throw new IllegalArgumentException(WRONG_SEARCH_CRITERIA);
            }

            if (typeValue.equals("ALL") || typeValue.equals("")) {
                pageTransactions = transactionsRepository.findAllUserTransactions(username, username, fromDate, toDate, pageable);
            } else if (typeValue.equals("INCOMING")) {
                pageTransactions = transactionsRepository.findTransactionsAdminPage("", username, fromDate, toDate, pageable);
            } else if (typeValue.equals("OUTGOING")) {
                if (!recipientUsername.equals("")) {
                    pageTransactions = transactionsRepository.findTransactionsAdminPage(username, recipientUsername, fromDate, toDate, pageable);
                } else {
                    pageTransactions = transactionsRepository.findTransactionsAdminPage(username, "", fromDate, toDate, pageable);
                }
            }

            recipients = usersService.getUserRecipients(username);
        }

        TransactionPageDto transactionPageDto = TransactionMapper.getTransactionPageDto(page, typeValue, username, isAdmin, pageTransactions, fromDate, toDate, senderUsername, recipientUsername, sort, senders, recipients);

        return transactionPageDto;
    }

    @Override
    public List<TransactionPresentDto> getOldestTransactionsByCount(String username, String type, boolean isAdmin, int count) {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("executed_on").descending());
        Page<Transaction> pageTransactions = transactionsRepository.findAllUserTransactions(username, username, "", "", pageable);

        List<TransactionPresentDto> transactions = TransactionMapper.mapTransactionsToPresentDto(pageTransactions.getContent(), type, isAdmin, username);

        return transactions;
    }

    @Transactional
    @Override
    public Transaction topUpUserWallet(TopUpExtendedDto topUpExtendedDto, String username) {

        String key = username + LocalDateTime.now();
        BankCard bankCard = bankCardsService.getActiveCard(username);

        if (bankCard == null) {
            throw new EntityNotFoundException(REGISTER_BANK_CARD_FIRST);
        }

        if (!bankCard.getCsv().equals(topUpExtendedDto.getCsv())) {
            throw new EntityNotFoundException(CSV_CODE_NOT_MATCHING);
        }

        User user = usersService.findByUsername(username);
        UserProfile userProfile = usersProfileService.findByUser(user);
        Wallet wallet = walletsService.findByName(userProfile, topUpExtendedDto.getWalletName());

        if (wallet == null) {
            throw new EntityNotFoundException(WALLET_NOT_FOUND);
        }

        if (wallet.isShared()) {
            if (walletsService.getSharedWalletRole(userProfile.getId(), wallet.getId()) == SEND_MONEY_SHARED_WALLET_PERMISSION) {
                throw new AccessDeniedException(CANT_ADD_MONEY_TO_SHARED_WALLET);
            }
        }


        TopUpDto topUpDto = BankCardMapper.topUpExtendedDtoToTopUpDto(topUpExtendedDto);
        topUpDto = BankCardMapper.bankCardToTopUpDto(bankCard, topUpDto);
        ApiTopUpRequest apiTopUpRequest = ModelFactory.createApiTopUpRequest();
        apiTopUpRequest.sendTopUpRequestToAPI(topUpDto, key);

        TransactionCreateDto transactionCreateDto = ModelFactory.transactionCreateDto();
        transactionCreateDto.setAmount(topUpDto.getAmount());
        transactionCreateDto.setNote(topUpDto.getDescription());

        userProfile = usersProfileService.findByUser(
                usersService.findByUsername(username));

        Wallet userWallet = walletsService.findByName(userProfile, topUpExtendedDto.getWalletName());

        return create(transactionCreateDto, username, username, true, userWallet, null);
    }

    @Override
    public TransactionTimeframeDataDto getTimeframeData(String timeframe, String username, boolean isAdmin) {
        TransactionTimeframeDataDto transactionTimeframeDataDto = new TransactionTimeframeDataDto();

        switch (timeframe) {
            case "7days":
                transactionTimeframeDataDto.setDateNames(getDateNames(LocalDate.now().minusDays(6), 7, 1));
                transactionTimeframeDataDto.setTransactionCounts(getTransactionsCountForPeriod(username, LocalDate.now().minusDays(6), 7, 0, 1, isAdmin));
                break;
            case "28days":
                transactionTimeframeDataDto.setDateNames(getDateNames(LocalDate.now().minusDays(27), 4, 7));
                transactionTimeframeDataDto.setTransactionCounts(getTransactionsCountForPeriod(username, LocalDate.now().minusDays(27), 4, 6, 7, isAdmin));
                break;
            default:
                break;

        }

        return transactionTimeframeDataDto;
    }

    private List<String> getDateNames(LocalDate fromDate, int datesCount, int step) {
        List<String> result = new ArrayList<>();

        for (int i = 1; i <= datesCount; i++) {
            result.add(String.format("%d %s", fromDate.getDayOfMonth(), fromDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)));
            fromDate = fromDate.plusDays(step);
        }

        return result;
    }

    private List<Long> getTransactionsCountForPeriod(String username, LocalDate fromDate, int datesCount, int daysForward, int step, boolean isAdmin) {
        List<Long> result = new ArrayList<>();

        if (isAdmin) {
            for (int i = 1; i <= datesCount; i++) {
                result.add(transactionsRepository.findTransactionsAdminPage("", "", fromDate.toString(), fromDate.plusDays(daysForward).toString(), PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").descending())).getTotalElements());
                fromDate = fromDate.plusDays(step);
            }
        } else {
            for (int i = 1; i <= datesCount; i++) {
                result.add((long) transactionsRepository.findUserTransactionsPerDate(username, username, fromDate.toString(), fromDate.plusDays(daysForward).toString()));
                fromDate = fromDate.plusDays(step);
            }
        }

        return result;
    }

    private String formatDate(String date) {
        if (date.equals("") || date.contains("-")) {
            return date;
        }

        String[] dateParts = date.split("/");
        String result = dateParts[2] + "-" + dateParts[0] + "-" + dateParts[1];

        return result;
    }

    private Pageable setPageable(int page, String sort) {
        if (sort.equals("id")) {
            return PageRequest.of(page, MAX_TRANSACTIONS_PER_PAGE, Sort.by(sort).ascending());
        }

        String[] sortParams = sort.split("-");
        String sortType = sortParams[0];
        String sortDirection = sortParams[1];

        if (sortDirection.equals(ASCENDING)) {
            return PageRequest.of(page, MAX_TRANSACTIONS_PER_PAGE, Sort.by(sortType).ascending());
        }

        return PageRequest.of(page, MAX_TRANSACTIONS_PER_PAGE, Sort.by(sortType).descending());
    }


    private void availabilityCheckForSenderBalance(TransactionCreateDto transactionCreateDto, Boolean isTopUp, Wallet wallet) {
        if (wallet.getBalance().compareTo(transactionCreateDto.getAmount()) < 0 && !isTopUp) {
            throw new NotSufficientFundsException(String.format(NOT_ENOUGH_MONEY));
        }
    }

    private LocalDateTime getLastTransactionTime(UserProfile sender,
                                                 Wallet senderWallet,
                                                 UserProfile receiver,
                                                 Wallet receiverWallet,
                                                 BigDecimal amount) {
        return transactionsRepository.findLastTransaction(sender,
                senderWallet,
                receiver,
                receiverWallet,
                amount);
    }

    private void isRepeatingTransaction(LocalDateTime lastTransactionTime) {
        LocalDateTime now = LocalDateTime.now();
        if (lastTransactionTime != null && lastTransactionTime.compareTo(now.minusMinutes(ONE_MINUTE)) > 0) {
            throw new DuplicateEntityException(REPEATING_TRANSACTION_IN_LAST_MINUTE);
        }
    }
}