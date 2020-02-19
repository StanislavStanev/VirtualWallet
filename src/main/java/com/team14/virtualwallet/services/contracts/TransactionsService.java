package com.team14.virtualwallet.services.contracts;

import com.team14.virtualwallet.models.Transaction;
import com.team14.virtualwallet.models.Wallet;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpExtendedDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionCreateDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPageDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionPresentDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionTimeframeDataDto;

import java.io.IOException;
import java.util.List;

public interface TransactionsService {

    Transaction preCreate(TransactionCreateDto transactionCreateDto);

    Transaction create(TransactionCreateDto transactionCreateDto, String senderUsername, String receiverUsername, Boolean isTopUp, Wallet senderWallet, Wallet receiverWallet);

    TransactionPageDto getTransactionPage(int page, String fromDate, String toDate, String senderUsername, String recipientUsername, String typeValue, String username, String sort);

    List<TransactionPresentDto> getOldestTransactionsByCount(String username, String type, boolean isAdmin, int count);

    List<Transaction> getAll();

    Transaction topUpUserWallet(TopUpExtendedDto topUpExtendedDto, String username) throws IOException;

    TransactionTimeframeDataDto getTimeframeData(String timeframe, String username, boolean isAdmin);
}
