package com.team14.virtualwallet.services.contracts;

import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.dtos.userdto.UserPictureDto;
import com.team14.virtualwallet.models.dtos.userdto.UserWalletDto;

import java.math.BigDecimal;
import java.util.List;

public interface UserProfilesService {

    UserProfile create(User user);

    UserProfile findByUser(User user);

    void saveToDb(UserProfile userProfile);

    List<String> searchByUsername(String keyword);

    List<String> searchByPhone(String keyword);

    List<String> searchByEmail(String keyword);

    BigDecimal getWalletBalance(String username);

    String searchByExactUsername(String keyword);

    BigDecimal getWalletBalanceByWalletName(String username, String walletname);

    List<UserPictureDto> searchByUsernameGetPictureUrl(String keyword);

    List<UserPictureDto> searchByEmailGetPictureUrl(String keyword);

    List<UserPictureDto> searchByPhoneGetPictureUrl(String keyword);

    List<UserWalletDto> getUserProfileWalletsList(String username);
}
