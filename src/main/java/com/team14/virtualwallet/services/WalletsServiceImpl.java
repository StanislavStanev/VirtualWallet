package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.models.Transaction;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.Wallet;
import com.team14.virtualwallet.models.dtos.walletDto.SharedWalletUsersListDto;
import com.team14.virtualwallet.repositories.UserProfilesRepository;
import com.team14.virtualwallet.repositories.WalletsRepository;
import com.team14.virtualwallet.services.contracts.WalletsService;
import com.team14.virtualwallet.utils.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletsServiceImpl implements WalletsService {

    public static final String ADD_MONEY_ROLE = "Add money";
    public static final String SEND_MONEY_ROLE = "Send money";
    public static final String ADD_AND_SEND_MONEY_ROLE = "Add and send";
    public static final String NOT_IN_WALLET_ROLE = "";
    public static final long ADD_MONEY_ROLE_ID = 1L;
    public static final int SEND_MONEY_ROLE_ID = 2;
    public static final int ADD_AND_SEND_MONEY_ROLE_ID = 3;
    private WalletsRepository walletsRepository;
    private UserProfilesRepository userProfilesRepository;

    @Autowired
    public WalletsServiceImpl(WalletsRepository walletsRepository, UserProfilesRepository userProfilesRepository) {
        this.walletsRepository = walletsRepository;
        this.userProfilesRepository = userProfilesRepository;
    }

    @Override
    public Wallet create(UserProfile userProfile, String walletName, String defaultWallet, String sharedWallet) {

        if (defaultWallet == null) {
            defaultWallet = "";
        }

        if (sharedWallet == null) {
            sharedWallet = "";
        }

        if (checkForDuplicateName(userProfile, walletName) != null) {
            throw new DuplicateEntityException(String.format("You have wallet with name: %s", walletName));
        }
        Wallet wallet = ModelFactory.createWallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setName(walletName);
        if (defaultWallet.length() > 0) {

            if (getDefault(userProfile) != null) {
                Wallet userProfileDefaultWallet = getDefault(userProfile);
                userProfileDefaultWallet.setIsDefault(false);
                walletsRepository.save(userProfileDefaultWallet);
            }
            wallet.setIsDefault(true);
        } else {
            wallet.setIsDefault(false);
        }

        if (sharedWallet.length() > 0) {
            wallet.setShared(true);
            wallet.setAdmin(userProfile);
            wallet.getMembers().add(userProfile);
        } else {
            wallet.setShared(false);
        }

        walletsRepository.save(wallet);
        userProfile.getWallets().add(wallet);
        userProfilesRepository.save(userProfile);
        return wallet;
    }

    public Wallet getDefault(UserProfile userProfile) {
        Wallet wallet = null;

        Iterator<Wallet> iterator = userProfile.getWallets().iterator();
        while (iterator.hasNext()) {
            wallet = iterator.next();
            if (wallet.isDefault()) {
                break;
            }
        }
        return wallet;
    }

    @Override
    public Wallet setDefault(UserProfile userProfile, String walletName) {
        Wallet wallet = null;

        Iterator<Wallet> iterator = userProfile.getWallets().iterator();
        while (iterator.hasNext()) {
            wallet = iterator.next();
            if (wallet.getName().equals(walletName)) {
                Wallet defaultWallet = getDefault(userProfile);
                defaultWallet.setIsDefault(false);
                walletsRepository.save(defaultWallet);
                wallet.setIsDefault(true);
                walletsRepository.save(wallet);
                break;
            }
        }
        return wallet;
    }

    @Override
    public Wallet findByName(UserProfile userProfile, String walletName) {
        Wallet wallet = null;

        Iterator<Wallet> iterator = userProfile.getWallets().iterator();
        while (iterator.hasNext()) {
            wallet = iterator.next();
            if (wallet.getName().equals(walletName)) {
                break;
            }
        }
        return wallet;
    }

    @Override
    public Wallet checkForDuplicateName(UserProfile userProfile, String walletName) {
        Wallet wallet = null;

        Iterator<Wallet> iterator = userProfile.getWallets().iterator();
        while (iterator.hasNext()) {
            wallet = iterator.next();
            if (wallet.getName().equals(walletName)) {
                break;
            } else {
                wallet = null;
            }
        }
        return wallet;
    }

    @Override
    public void addToBalance(UserProfile receiverUserProfile, Transaction transaction, Wallet wallet, Boolean isTopUp) {

        if (!isTopUp && wallet == null) {
            wallet = getDefault(receiverUserProfile);
        }

        wallet.addTransaction(transaction);
        wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        walletsRepository.save(wallet);
    }

    @Override
    public void subtractFromBalance(Transaction transaction, Wallet wallet) {
        wallet.addTransaction(transaction);
        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        walletsRepository.save(wallet);
    }

    @Override
    public List<SharedWalletUsersListDto> getAllUsersInSharedWallet(Long walletId) {
        List<SharedWalletUsersListDto> sharedWalletUsersListDtoList = new ArrayList<>();

        Wallet wallet = walletsRepository.findById(walletId).orElse(null);
        for (UserProfile walletMember :
                wallet.getMembers()) {
            SharedWalletUsersListDto sharedWalletUsersListDto = ModelFactory.createSharedWalletUsersListDto();
            sharedWalletUsersListDto.setUsername(walletMember.getUser().getUsername());
            sharedWalletUsersListDto.setPictureUrl(walletMember.getPicture().getPictureUrl());
            String walletRole = "";
            if (getSharedWalletRole(walletMember.getId(), walletId) == ADD_MONEY_ROLE_ID) {
                walletRole = ADD_MONEY_ROLE;
            } else if (getSharedWalletRole(walletMember.getId(), walletId) == SEND_MONEY_ROLE_ID) {
                walletRole = SEND_MONEY_ROLE;
            } else if (getSharedWalletRole(walletMember.getId(), walletId) == ADD_AND_SEND_MONEY_ROLE_ID) {
                walletRole = ADD_AND_SEND_MONEY_ROLE;
            } else {
                walletRole = NOT_IN_WALLET_ROLE;
            }
            sharedWalletUsersListDto.setRole(walletRole);
            sharedWalletUsersListDtoList.add(sharedWalletUsersListDto);
        }

        sharedWalletUsersListDtoList = sharedWalletUsersListDtoList.stream().sorted(Comparator.comparing(SharedWalletUsersListDto::getUsername)).collect(Collectors.toList());
        return sharedWalletUsersListDtoList;
    }

    @Override
    public void addMember(UserProfile admin, UserProfile member, String walletName) {
        Wallet wallet = findByName(admin, walletName);
        wallet.getMembers().add(member);
        updateSharedWalletRole(member.getId(), wallet.getId(), 1L);
        walletsRepository.save(wallet);
    }

    @Override
    public void removeMember(UserProfile member, Long walletId) {
        Wallet wallet = walletsRepository.findById(walletId).orElse(null);
        wallet.getMembers().remove(member);
        walletsRepository.save(wallet);
    }

    @Override
    public Long getSharedWalletRole(Long memberId, Long walletId) {
        Long role_id = walletsRepository.findUserProfileWalletRole(memberId, walletId);
        return role_id;
    }

    @Override
    public void updateSharedWalletRole(Long memberId, Long walletId, Long role_id) {
        walletsRepository.updateUserProfileWalletRole(memberId, walletId, role_id);
    }

}
