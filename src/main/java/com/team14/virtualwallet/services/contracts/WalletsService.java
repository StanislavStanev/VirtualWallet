package com.team14.virtualwallet.services.contracts;

import com.team14.virtualwallet.models.Transaction;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.Wallet;
import com.team14.virtualwallet.models.dtos.walletDto.SharedWalletUsersListDto;

import java.util.List;

public interface WalletsService {

    Wallet create(UserProfile userProfile, String walletName, String defaultWallet, String sharedWallet);

    Wallet getDefault(UserProfile userProfile);

    Wallet setDefault(UserProfile userProfile, String walletName);

    Wallet findByName(UserProfile userProfile, String walletName);

    Wallet checkForDuplicateName(UserProfile userProfile, String walletName);


    void addToBalance(UserProfile receiverUserProfile, Transaction transaction, Wallet wallet, Boolean isTopUp);

    void subtractFromBalance(Transaction transaction, Wallet wallet);

    List<SharedWalletUsersListDto> getAllUsersInSharedWallet(Long walletId);

    void addMember(UserProfile admin, UserProfile member, String walletname);

    void removeMember(UserProfile member, Long walletId);

    Long getSharedWalletRole(Long memberId, Long walletId);

    void updateSharedWalletRole(Long memberId, Long walletId, Long role_id);
}
