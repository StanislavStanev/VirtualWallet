package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.models.*;
import com.team14.virtualwallet.models.dtos.userdto.UserPictureDto;
import com.team14.virtualwallet.models.dtos.userdto.UserWalletDto;
import com.team14.virtualwallet.repositories.UserProfilesRepository;
import com.team14.virtualwallet.services.contracts.PicturesService;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.services.contracts.WalletsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserProfileServiceImplTests {

    public static final String USER = "USER";

    private UsersService usersService;
    private UserProfilesRepository userProfilesRepository;
    private WalletsService walletsService;
    private PicturesService picturesService;
    private UserProfilesService userProfilesService;
    private User user;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private Role role;
    private Picture picture;
    private UserProfile userProfile;
    private Wallet wallet;

    @Before
    public void init() {

        usersService = Mockito.mock(UsersService.class);
        walletsService = Mockito.mock(WalletsService.class);
        picturesService = Mockito.mock(PicturesService.class);
        userProfilesRepository = Mockito.mock(UserProfilesRepository.class);
        userProfilesService = new UserProfilesServiceImpl(walletsService, picturesService, userProfilesRepository);
        wallet = new Wallet();
        role = new Role() {{
            setAuthority(USER);
        }};

        user = new User() {{
            setId(1L);
            setUsername("rarnaudov");
            setEmail("radi.arnaudov@gmail.com");
            setPassword(bCryptPasswordEncoder.encode("TestPass123@@@"));
            getAuthorities().add(role);
        }};
        picture = new Picture();
        picture.setPictureUrl("www.test.url");
        userProfile = new UserProfile() {{
            setId(1L);
            setUser(user);
            setFullName("Radi Arnaudov");
            setPicture(picture);
            getWallets().add(wallet);
        }};
    }

    @Test
    public void check_createUser_Successful() {
        UserProfile userProfile1 = new UserProfile();
        Mockito.when(walletsService.create(userProfile1, "Defaul", "DefaultWallet", "")).thenReturn(wallet);
        userProfile1.setUser(user);
        Mockito.when(picturesService.create()).thenReturn(picture);
        userProfile1.setPicture(picture);
        Mockito.when(userProfilesRepository.save(Mockito.any(UserProfile.class))).thenReturn(userProfile1);
        userProfile1 = userProfilesService.create(user);
        Assert.assertEquals(userProfile.getUser().getUsername(), userProfile1.getUser().getUsername());
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenFindByUser_Then_ThrowEntityNotFoundException() {
        userProfilesService.findByUser(user);
    }

    @Test
    public void whenFindByUser_Then_FindUserProfile() {
        Mockito.when(userProfilesRepository.findByUser(user)).thenReturn(java.util.Optional.ofNullable(userProfile));
        Assert.assertEquals(userProfile.getUser().getUsername(), userProfilesService.findByUser(user).getUser().getUsername());
    }

    @Test
    public void whenSearchByUsername_Then_checkResult() {
        List<String> userNamesList = new ArrayList<>();
        userNamesList.add(user.getUsername());
        Mockito.when(userProfilesRepository.searchByUsername(Mockito.anyString())).thenReturn(userNamesList);
        Assert.assertEquals(userNamesList, userProfilesService.searchByUsername(user.getUsername()));
    }

    @Test
    public void whenSearchByPhone_Then_checkResult() {
        List<String> userNamesList = new ArrayList<>();
        userNamesList.add(user.getUsername());
        Mockito.when(userProfilesRepository.searchByPhone(Mockito.anyString())).thenReturn(userNamesList);
        Assert.assertEquals(userNamesList, userProfilesService.searchByPhone(user.getUsername()));
    }

    @Test
    public void whenSearchByEmail_Then_checkResult() {
        List<String> userNamesList = new ArrayList<>();
        userNamesList.add(user.getUsername());
        Mockito.when(userProfilesRepository.searchByEmail(Mockito.anyString())).thenReturn(userNamesList);
        Assert.assertEquals(userNamesList, userProfilesService.searchByEmail(user.getUsername()));
    }

    @Test
    public void whenGetWalletBalance_Then_checkResult() {
        wallet.setBalance(BigDecimal.TEN);
        Mockito.when(walletsService.getDefault(userProfile)).thenReturn(wallet);
        Mockito.when(userProfilesRepository.findByUserName(Mockito.anyString())).thenReturn(user);
        Mockito.when(userProfilesRepository.findByUser(Mockito.any(User.class))).thenReturn(java.util.Optional.ofNullable(userProfile));
        Assert.assertEquals(wallet.getBalance(), userProfilesService.getWalletBalance(user.getUsername()));
    }

    @Test
    public void whenNewUserCreated_Then_CheckRepositoryIsCalledOnce() {
        userProfilesService.saveToDb(userProfile);
        verify(userProfilesRepository, times(1)).save(userProfile);
    }

    @Test
    public void whenSearchByExactUserName_Then_Success() {
        Mockito.when(userProfilesRepository.searchByExactUsername(Mockito.anyString())).thenReturn("rarnaudov");
        String result = userProfilesService.searchByExactUsername("rarnaudov");
        Assert.assertEquals("rarnaudov", result);
    }

    @Test
    public void whenGetWalletWalanceByWalletName_Then_CheckResult() {
        wallet.setBalance(BigDecimal.valueOf(100));
        Mockito.when(userProfilesRepository.findByUserName(Mockito.anyString())).thenReturn(user);
        Mockito.when(userProfilesRepository.findByUser(Mockito.any(User.class))).thenReturn(java.util.Optional.ofNullable(userProfile));
        Mockito.when(walletsService.findByName(userProfile, wallet.getName())).thenReturn(wallet);
        BigDecimal result = userProfilesService.getWalletBalanceByWalletName(userProfile.getUser().getUsername(), wallet.getName());
        Assert.assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    public void whenSearchByUsernameGetPictureUrl_Then_CheckListSize() {
        List<UserProfile> userProfileList = new ArrayList<>();
        userProfileList.add(userProfile);
        Mockito.when(userProfilesRepository.searchByUsernameGetPictureUrl(Mockito.anyString())).thenReturn(userProfileList);
        List<UserPictureDto> userPictureDtoList = userProfilesService.searchByUsernameGetPictureUrl("rar");
        Assert.assertEquals(1, userPictureDtoList.size());
    }

    @Test
    public void whenSearchByPhoneGetPictureUrl_Then_CheckListSize() {
        List<UserProfile> userProfileList = new ArrayList<>();
        userProfileList.add(userProfile);
        Mockito.when(userProfilesRepository.searchByPhoneGetPictureUrl(Mockito.anyString())).thenReturn(userProfileList);
        List<UserPictureDto> userPictureDtoList = userProfilesService.searchByPhoneGetPictureUrl("rar");
        Assert.assertEquals(1, userPictureDtoList.size());
    }

    @Test
    public void whenSearchByEmailGetPictureUrl_Then_CheckListSize() {
        List<UserProfile> userProfileList = new ArrayList<>();
        userProfileList.add(userProfile);
        Mockito.when(userProfilesRepository.searchByEmailGetPictureUrl(Mockito.anyString())).thenReturn(userProfileList);
        List<UserPictureDto> userPictureDtoList = userProfilesService.searchByEmailGetPictureUrl("rar");
        Assert.assertEquals(1, userPictureDtoList.size());
    }

    @Test
    public void whenGetUserProfileWalletsList_Then_CheckListSize() {
        Mockito.when(userProfilesRepository.findByUserName(Mockito.anyString())).thenReturn(user);
        Mockito.when(userProfilesRepository.findByUser(user)).thenReturn(java.util.Optional.ofNullable(userProfile));
        wallet.setShared(false);
        wallet.setAdmin(null);
        wallet.setIsDefault(true);
        wallet.setName("First");
        Wallet wallet3 = new Wallet();
        wallet3.setShared(false);
        wallet3.setBalance(BigDecimal.ZERO);
        wallet3.setIsDefault(false);
        wallet3.setName("Second");
        wallet3.setId(2L);
        userProfile.getWallets().add(wallet3);
        wallet.setBalance(BigDecimal.ZERO);
        List<UserWalletDto> userWalletDtos = userProfilesService.getUserProfileWalletsList(user.getUsername());
        Assert.assertEquals(2, userWalletDtos.size());
    }


}
