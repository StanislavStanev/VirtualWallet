package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.models.*;
import com.team14.virtualwallet.repositories.UserProfilesRepository;
import com.team14.virtualwallet.repositories.WalletsRepository;
import com.team14.virtualwallet.services.contracts.WalletsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class WalletsServiceImplTests {

    public static final String USER = "USER";
    public static final String DEFAULT_WALLET = "Default wallet";
    public static final String DEFAULT = "default";

    private WalletsRepository walletsRepository;
    private WalletsService walletsService;
    private Wallet wallet;
    private UserProfilesRepository userProfilesRepository;
    private User user;
    private UserProfile userProfile;
    private User user2;
    private UserProfile userProfile2;
    private Role role;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private Picture picture;
    private Transaction transaction;
    private Transaction transactionTopUp;

    @Before
    public void init() {
        walletsRepository = Mockito.mock(WalletsRepository.class);
        userProfilesRepository = Mockito.mock(UserProfilesRepository.class);
        walletsService = new WalletsServiceImpl(walletsRepository, userProfilesRepository);
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

        }};
        user2 = new User() {{
            setId(2L);
            setUsername("user2");
            setEmail("user2@gmail.com");
        }};
        userProfile2 = new UserProfile() {{
            setId(2L);
            setUser(user2);
            setFullName("User2");
            setPicture(picture);
        }};
        transaction = new Transaction() {{
            setReceiver(userProfile);
            setSender(userProfile2);
            setNote("Test");
            setExecutedOn(LocalDateTime.now());
            setAmount(BigDecimal.valueOf(100L));
            setTopUp(false);
            setId(1L);
        }};

        transactionTopUp = new Transaction() {{
            setReceiver(userProfile);
            setSender(userProfile);
            setNote("Test");
            setExecutedOn(LocalDateTime.now());
            setAmount(BigDecimal.valueOf(90L));
            setTopUp(true);
            setId(2L);
        }};
    }


    @Test
    public void whenNewUserCreated_Then_CheckWalletInitialBalanceEqualsZERO() {
        //Arrange
        wallet = walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        //Act//Assert
        Assert.assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    public void whenNewUserCreated_Then_CheckRepositoryIsCalledOnce() {
        wallet = walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        verify(walletsRepository, times(1)).save(wallet);
    }

    @Test(expected = DuplicateEntityException.class)
    public void whenNewWalletCreated_With_SameName_Then_ThrowDuplicateEntityException() {
        wallet = walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        wallet = walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
    }

    @Test
    public void whenSecondDefaultWalletCreated_Then_GetDefaultMethodWorks() {
        //Arrange
        walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        walletsService.create(userProfile, "New Wallet", DEFAULT, null);
        //Act
        wallet = walletsService.getDefault(userProfile);
        //Assert
        Assert.assertNotEquals(DEFAULT_WALLET, wallet.getName());
        Assert.assertEquals("New Wallet", wallet.getName());

    }

    @Test
    public void whenSecondNOTDefaultWalletCreated_Then_GetDefaultMethodWorks() {
        //Arrange
        walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        walletsService.create(userProfile, "New Wallet", null, null);
        //Act
        wallet = walletsService.getDefault(userProfile);
        //Assert
        Assert.assertEquals(DEFAULT_WALLET, wallet.getName());
        Assert.assertNotEquals("New Wallet", wallet.getName());
    }

    @Test
    public void whenSharedWalletCreated_Then_CheckIfUserIsAdmin() {
        //Arrange
        Mockito.when(walletsRepository.findUserProfileWalletRole(Mockito.anyLong(), Mockito.anyLong())).thenReturn(3L);
        walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        wallet = walletsService.create(userProfile, "New Shared Wallet", null, "true");
        wallet.setId(1L);
        Long sharedWalletRoleId = walletsService.getSharedWalletRole(userProfile.getId(), wallet.getId());
        //Assert
        Assert.assertEquals(userProfile, wallet.getAdmin());
        Assert.assertEquals(1, wallet.getMembers().size());
        Assert.assertEquals(3, Integer.parseInt(sharedWalletRoleId.toString()));
    }

    @Test
    public void whenSecondNOTDefaultWalletCreated_Then_SetDefaultMethodWorks() {
        //Arrange
        walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        walletsService.create(userProfile, "New Wallet", null, null);
        //Act
        wallet = walletsService.setDefault(userProfile, "New Wallet");
        //Assert
        Assert.assertEquals("New Wallet", wallet.getName());
        Assert.assertNotEquals(DEFAULT_WALLET, wallet.getName());
    }

    @Test
    public void whenFindByName_Then_Success() {
        //Arrange
        walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        walletsService.create(userProfile, "New Wallet", null, null);
        //Act
        wallet = walletsService.findByName(userProfile, "New Wallet");
        //Assert
        Assert.assertEquals("New Wallet", wallet.getName());
        Assert.assertNotEquals(DEFAULT_WALLET, wallet.getName());
    }

    @Test
    public void whenAddMoney_Then_CheckResult() {
        //Arrange
        wallet = walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        wallet.setBalance(BigDecimal.valueOf(1000L));
        //Act
        walletsService.addToBalance(userProfile, transaction, wallet, false);
        //Assert
        Assert.assertEquals(BigDecimal.valueOf(1100), wallet.getBalance());
    }

    @Test
    public void whenAddMoneyNoWallet_Then_CheckResult() {
        //Arrange
        wallet = walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        wallet.setBalance(BigDecimal.valueOf(1000L));
        //Act
        walletsService.addToBalance(userProfile, transaction, null, false);
        //Assert
        Assert.assertEquals(BigDecimal.valueOf(1100), wallet.getBalance());
    }

    @Test
    public void whenTakeMoney_Then_CheckResult() {
        //Arrange
        wallet = walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        wallet.setBalance(BigDecimal.valueOf(1000L));
        //Act
        walletsService.subtractFromBalance(transaction, wallet);
        //Assert
        Assert.assertEquals(BigDecimal.valueOf(900), wallet.getBalance());
    }

    @Test
    public void whenAddMemberToSharedWallet_Then_CheckWalletSize() {
        //Arrange        //Act
        walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        wallet = walletsService.create(userProfile, "New Shared Wallet", null, "true");
        walletsService.addMember(userProfile, userProfile2, "New Shared Wallet");
        //Assert
        Assert.assertEquals(2, wallet.getMembers().size());
    }

    @Test
    public void whenRemoveMemberToSharedWallet_Then_CheckWalletSize() {
        //Arrange
        wallet = walletsService.create(userProfile, "New Shared Wallet", null, "true");
        wallet.getMembers().add(userProfile2);
        wallet.setId(1L);
        Mockito.when(walletsRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.ofNullable(wallet));
        // Act
        walletsService.removeMember(userProfile2, wallet.getId());
        //Assert
        Assert.assertEquals(1, wallet.getMembers().size());
    }

    @Test
    public void whenAddMemberToSharedWallet_Then_ReturnListWithSize() {
        //Arrange        //Act
        walletsService.create(userProfile, DEFAULT_WALLET, DEFAULT, null);
        wallet = walletsService.create(userProfile, "New Shared Wallet", null, "true");
        wallet.setId(1L);
        walletsService.addMember(userProfile, userProfile2, "New Shared Wallet");
        Mockito.when(walletsRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.ofNullable(wallet));
        //Mockito.when(ModelFactory.createSharedWalletUsersListDto()).thenReturn(new SharedWalletUsersListDto());
        Mockito.when(walletsService.getSharedWalletRole(Mockito.anyLong(), Mockito.anyLong())).thenReturn(3L);

        //Assert
        Assert.assertEquals(2, walletsService.getAllUsersInSharedWallet(1L).size());
    }


}
