package com.team14.virtualwallet.utils.mappers;

import com.team14.virtualwallet.models.Transaction;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPageDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPresentDto;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class TransactionMapper {

    public static final String DEFAULT = "DEFAULT";
    private static String ALL = "ALL";
    private static String TOPUP = "TOPUP";
    private static String INCOMING = "INCOMING";
    private static String OUTGOING = "OUTGOING";
    private static String NONE = "NONE";
    private static int MAX_TRANSACTIONS_PER_PAGE = 5;

    public static List<TransactionPresentDto> mapTransactionsToPresentDto(List<Transaction> transactions, String type, boolean isAdmin, String username) {
        List<TransactionPresentDto> transactionPresentDtos = new ArrayList<>();

        for (Transaction t : transactions) {
            if (!isAdmin && type.equals(OUTGOING) && t.isTopUp()) {
                continue;
            }
            TransactionPresentDto transactionPresentDto = new TransactionPresentDto();

            transactionPresentDto.setId(t.getId());
            transactionPresentDto.setAmount(t.getAmount());
            transactionPresentDto.setFrom(t.getSender().getUser().getUsername());
            transactionPresentDto.setTo(t.getReceiver().getUser().getUsername());
            transactionPresentDto.setType(determineType(t, type, isAdmin, username));
            transactionPresentDto.setExecutedOn(t.getExecutedOn().toString());
            if (t.getSenderWallet() != null) {
                transactionPresentDto.setSenderWalletName(t.getSenderWallet().getName());
            }
            transactionPresentDto.setReceiverWalletName(t.getReceiverWallet().getName());
            transactionPresentDtos.add(transactionPresentDto);
        }

        return transactionPresentDtos;
    }

    public static TransactionPageDto getTransactionPageDto(int page, String typeValue, String username, boolean isAdmin, Page<Transaction> pageTransactions, String fromDate, String toDate, String sender, String recipient, String sort, List<String> senders, List<String> recipients) {
        TransactionPageDto transactionPageDto = new TransactionPageDto();
        transactionPageDto.setTransactions(TransactionMapper.mapTransactionsToPresentDto(pageTransactions.getContent(), typeValue, isAdmin, username));
        transactionPageDto.setAdmin(isAdmin);
        transactionPageDto.setCurrentPage(page);
        transactionPageDto.setHasNext(page + 1 < pageTransactions.getTotalPages());
        transactionPageDto.setHasPrevious(page > 0);
        transactionPageDto.setFromRecordNum(pageTransactions.getTotalElements() == 0 ? 0 : page * MAX_TRANSACTIONS_PER_PAGE  == 0
                ? 1
                : (page * MAX_TRANSACTIONS_PER_PAGE) + 1);
        transactionPageDto.setToRecordNum(pageTransactions.getTotalElements() < MAX_TRANSACTIONS_PER_PAGE
                ? (int)pageTransactions.getTotalElements()
                : ((page * MAX_TRANSACTIONS_PER_PAGE) + MAX_TRANSACTIONS_PER_PAGE) > pageTransactions.getTotalElements()
                ? (int)pageTransactions.getTotalElements()
                : ((page * MAX_TRANSACTIONS_PER_PAGE) + MAX_TRANSACTIONS_PER_PAGE));
        transactionPageDto.setAllRecords(pageTransactions.getTotalElements());
        transactionPageDto.setFromDate(fromDate);
        transactionPageDto.setToDate(toDate);
        transactionPageDto.setSender(sender);
        transactionPageDto.setRecipient(recipient);
        transactionPageDto.setType(typeValue);
        transactionPageDto.setSort(sort);
        transactionPageDto.setSenders(senders);
        transactionPageDto.setRecipients(recipients);
        return transactionPageDto;
    }

    private static String determineType(Transaction t, String type, boolean isAdmin, String username) {
        if (!isAdmin) {
            if (type.equals(ALL) || type.equals("")) {
                if (t.isTopUp()) {
                    return TOPUP;
                } else if (t.getSender().getUser().getUsername().equals(username)) {
                    return OUTGOING;
                } else {
                    return INCOMING;
                }
            } else if (type.equals(INCOMING)) {
                if (t.isTopUp()) {
                    return TOPUP;
                }
                return INCOMING;
            } else if (type.equals(OUTGOING)) {
                return OUTGOING;
            }
        }

        if (t.isTopUp()) {
            return TOPUP;
        }

        return NONE;
    }
}
