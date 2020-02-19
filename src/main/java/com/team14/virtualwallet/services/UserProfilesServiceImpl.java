package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.models.Picture;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.dtos.userdto.UserPictureDto;
import com.team14.virtualwallet.models.dtos.userdto.UserWalletDto;
import com.team14.virtualwallet.repositories.UserProfilesRepository;
import com.team14.virtualwallet.services.contracts.PicturesService;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import com.team14.virtualwallet.services.contracts.WalletsService;
import com.team14.virtualwallet.utils.ModelFactory;
import com.team14.virtualwallet.utils.mappers.UserMapper;
import com.team14.virtualwallet.utils.mappers.WalletMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfilesServiceImpl implements UserProfilesService {

    public static final String DEFAULT_WALLET = "Default wallet";
    public static final String DEFAULT = "default";
    public static final String IS_DEFAULT_AS_STRING = "default";
    private static final String USER_PROFILE_FOR_USER_WITH_USERNAME_NOT_EXISTS = "User Profile for user with username: %s doesn't exist in the database.";
    private final WalletsService walletsService;
    private final PicturesService picturesService;

    private UserProfilesRepository userProfilesRepository;

    public UserProfilesServiceImpl(WalletsService walletsService, PicturesService picturesService, UserProfilesRepository userProfilesRepository) {
        this.walletsService = walletsService;
        this.picturesService = picturesService;
        this.userProfilesRepository = userProfilesRepository;
    }

    @Override
    public UserProfile create(User user) {
        UserProfile userProfile = ModelFactory.createUserProfile();
        userProfile.setUser(user);

        Picture picture = picturesService.create();

        userProfile.setPicture(picture);

        UserProfile userProfileFromDb = userProfilesRepository.save(userProfile);
        walletsService.create(userProfileFromDb, DEFAULT_WALLET, DEFAULT, null);

        return userProfileFromDb;
    }

    @Override
    public UserProfile findByUser(User user) {
        UserProfile userProfile = userProfilesRepository.findByUser(user).orElse(null);

        if (userProfile == null) {
            throw new EntityNotFoundException(String.format(USER_PROFILE_FOR_USER_WITH_USERNAME_NOT_EXISTS, user.getUsername()));
        }

        return userProfile;
    }

    @Override
    public void saveToDb(UserProfile userProfile) {
        this.userProfilesRepository.save(userProfile);
    }

    @Override
    public List<String> searchByUsername(String keyword) {
        return userProfilesRepository.searchByUsername(keyword);
    }

    @Override
    public List<String> searchByPhone(String keyword) {
        return userProfilesRepository.searchByPhone(keyword);
    }

    @Override
    public List<String> searchByEmail(String keyword) {
        return userProfilesRepository.searchByEmail(keyword);
    }

    @Override
    public String searchByExactUsername(String keyword) {
        return userProfilesRepository.searchByExactUsername(keyword);
    }

    @Override
    public BigDecimal getWalletBalance(String username) {
        UserProfile userProfile = findByUser(userProfilesRepository.findByUserName(username));
        return walletsService.getDefault(userProfile).getBalance();
    }

    @Override
    public BigDecimal getWalletBalanceByWalletName(String username, String walletname) {
        UserProfile userProfile = findByUser(userProfilesRepository.findByUserName(username));
        return walletsService.findByName(userProfile, walletname).getBalance();
    }

    @Override
    public List<UserPictureDto> searchByUsernameGetPictureUrl(String keyword) {
        List<UserPictureDto> userPictureDtoList =
                UserMapper.mapUserProfilesToPictureList(userProfilesRepository.searchByUsernameGetPictureUrl(keyword));
        return userPictureDtoList;
    }

    @Override
    public List<UserPictureDto> searchByEmailGetPictureUrl(String keyword) {
        List<UserPictureDto> userPictureDtoList =
                UserMapper.mapUserProfilesToPictureList(userProfilesRepository.searchByEmailGetPictureUrl(keyword));
        return userPictureDtoList;
    }

    @Override
    public List<UserPictureDto> searchByPhoneGetPictureUrl(String keyword) {
        List<UserPictureDto> userPictureDtoList =
                UserMapper.mapUserProfilesToPictureList(userProfilesRepository.searchByPhoneGetPictureUrl(keyword));
        return userPictureDtoList;
    }

    public List<UserWalletDto> getUserProfileWalletsList(String username) {
        User user = userProfilesRepository.findByUserName(username);
        UserProfile userProfile = findByUser(user);
        List<UserWalletDto> userWalletDtoList = WalletMapper.mapUserWalletsToDtoList(userProfile);
        userWalletDtoList = sortWalletsList(userWalletDtoList);
        return userWalletDtoList;
    }

    private List<UserWalletDto> sortWalletsList(List<UserWalletDto> userWalletDtoList) {
        return
                userWalletDtoList.stream()
                        .sorted(Comparator.comparing(UserWalletDto::getDefault)
                                .thenComparing(UserWalletDto::getBalance).reversed()
                                .thenComparing(UserWalletDto::getWalletName))
                        .parallel()
                        .collect(Collectors.toList());
    }
}
