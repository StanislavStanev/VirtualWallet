package com.team14.virtualwallet.utils.mappers;

import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.Wallet;
import com.team14.virtualwallet.models.dtos.userdto.UserWalletDto;

import java.util.ArrayList;
import java.util.List;

public class WalletMapper {

    public static List<UserWalletDto> mapUserWalletsToDtoList(UserProfile userProfile) {
        List<UserWalletDto> userWalletDtoList = new ArrayList<>();
        for (Wallet wallet :
                userProfile.getWallets()) {
            UserWalletDto userWalletDto = new UserWalletDto();
            userWalletDto.setWalletName(wallet.getName());
            userWalletDto.setDefault(wallet.isDefault());
            userWalletDto.setBalance(wallet.getBalance());
            userWalletDto.setShared(wallet.isShared());
            userWalletDto.setWalletId(wallet.getId());
            if (wallet.isShared() && wallet.getAdmin().equals(userProfile)) {
                userWalletDto.setAdmin(true);
            }
            userWalletDtoList.add(userWalletDto);
        }
        return userWalletDtoList;
    }
}
