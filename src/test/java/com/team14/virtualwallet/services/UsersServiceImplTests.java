package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.exceptions.InvalidPasswordException;
import com.team14.virtualwallet.models.*;
import com.team14.virtualwallet.models.dtos.userdto.UserPasswordUpdateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataUpdateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserRegisterDto;
import com.team14.virtualwallet.repositories.RolesRepository;
import com.team14.virtualwallet.repositories.TransactionsRepository;
import com.team14.virtualwallet.repositories.UserProfilesRepository;
import com.team14.virtualwallet.repositories.UsersRepository;
import com.team14.virtualwallet.services.contracts.UserProfilesService;
import com.team14.virtualwallet.services.contracts.UsersService;
import com.team14.virtualwallet.utils.PictureUploader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.Part;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsersServiceImplTests {
    public static final String USER = "USER";
    public static final int DATA_RECORDS_PER_PAGE = 5;

    private UsersService usersService;
    private UserProfilesService userProfilesService;
    private User user;
    private UserProfile userProfile;
    private User user2;
    private UserProfile userProfile2;
    private UsersRepository usersRepository;
    private UserProfilesRepository userProfilesRepository;
    private RolesRepository rolesRepository;
    private TransactionsRepository transactionsRepository;
    private PictureUploader pictureUploader;
    private UserRegisterDto registerUserDto;
    private UserRegisterDto registerUserDto2;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private Role role;
    private UserPasswordUpdateDto userPasswordUpdateDto;
    private UserPersonalDataUpdateDto userPersonalDataUpdateDto;
    private Part part;
    private UserPersonalDataDto userPersonalDataDto;
    private UserPersonalDataDto userPersonalDataDto2;
    private List<UserProfile> userProfileList;
    private Pageable fiveUsersPerPage;
    private Picture picture;
    private Transaction transaction;
    private Transaction transaction2;


    @Before
    public void init() {
        role = new Role() {{
            setAuthority(USER);
        }};
        user = new User() {{
            setId(1L);
            setUsername("rarnaudov");
            setEmail("radi.arnaudov@gmail.com");
            setPassword(bCryptPasswordEncoder.encode("TestPass123@@@"));
            getAuthorities().add(role);
            setCreatedOn(LocalDateTime.now().minusDays(10L));
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
            setCreatedOn(LocalDateTime.now().minusDays(10L));
        }};
        userProfile2 = new UserProfile() {{
            setId(2L);
            setUser(user2);
            setFullName("User2");
            setPicture(picture);
        }};
        registerUserDto = new UserRegisterDto() {{
            setUsername("rarnaudov");
            setEmail("radi.arnaudov@gmail.com");
            setFullName("Radi Arnaudov");
            setPassword("TestPass123@@@");
        }};
        registerUserDto2 = new UserRegisterDto() {{
            setUsername("test123");
            setEmail("test123@gmail.com");
            setFullName("test123");
            setPassword("TestPass123@@@test123");
        }};

        userPasswordUpdateDto = new UserPasswordUpdateDto() {{
            setPassword("12345678");
            setConfirmPassword("12345678");
        }};

        userPersonalDataUpdateDto = new UserPersonalDataUpdateDto() {{
            setEmail("radi.arnaudov2@gmail.com");
            setFullName("Rady Arnaudov");
            setPhone("0884408213");
        }};

        userPersonalDataDto = new UserPersonalDataDto() {{
            setUsername("test123");
            setEmail("test123@gmail.com");
            setFullname("test123");
            setBlocked(false);
            setPhone("0884408213");
            setEnabled(true);
            setPictureUrl("https://res-console.cloudinary.com/dfiarn6oz/thumbnails/transform/v1/image/upload//v1578919853/YmVlci1tYWluLTBfeno3anVp/drilldown?0.0801866347465261");
        }};

        userPersonalDataDto2 = new UserPersonalDataDto() {{
            setUsername("rarnaudov");
            setEmail("radi.arnaudov@gmail.com");
            setFullname("Radi Arnaudov");
            setBlocked(false);
            setPhone("359884408213");
            setEnabled(true);
            setPictureUrl("https://res-console.cloudinary.com/dfiarn6oz/thumbnails/transform/v1/image/upload//v1578919853/YmVlci1tYWluLTBfeno3anVp/drilldown?0.0801866347465261");
        }};

        transaction = new Transaction() {{
            setId(1L);
            setTopUp(false);
            setAmount(BigDecimal.valueOf(100L));
            setExecutedOn(LocalDateTime.now());
            setNote("transaction 1");
            setSender(userProfile);
            setReceiver(userProfile2);
        }};

        transaction2 = new Transaction() {{
            setId(2L);
            setTopUp(false);
            setAmount(BigDecimal.valueOf(200L));
            setExecutedOn(LocalDateTime.now());
            setNote("transaction 2");
            setSender(userProfile);
            setReceiver(userProfile2);
        }};

        //transactionsRepository.save(transaction);
        //transactionsRepository.save(transaction2);


        userProfileList = new ArrayList<>();
        userProfileList.add(userProfile);
        userProfileList.add(userProfile2);


        userProfilesService = Mockito.mock(UserProfilesService.class);
        usersRepository = Mockito.mock(UsersRepository.class);
        userProfilesRepository = Mockito.mock(UserProfilesRepository.class);
        rolesRepository = Mockito.mock(RolesRepository.class);
        transactionsRepository = Mockito.mock(TransactionsRepository.class);
        pictureUploader = Mockito.mock(PictureUploader.class);

        usersService = new UsersServiceImpl(userProfilesService, usersRepository, userProfilesRepository, rolesRepository, transactionsRepository, bCryptPasswordEncoder, pictureUploader);
    }

    @Test(expected = DuplicateEntityException.class)
    public void whenRegisterUser_And_UserNameAlreadyInDb_Then_ThrowsError() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        usersService.register(registerUserDto);
    }

    @Test
    public void check_RegistrationSuccessful() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(null));
        Mockito.when(rolesRepository.findByAuthority(USER)).thenReturn(role);
        Mockito.when(usersRepository.save(Mockito.any(User.class))).thenReturn(user);
        registerUserDto.setConfirmPassword(registerUserDto.getPassword());
        user = usersService.register(registerUserDto);
        Assert.assertEquals(registerUserDto.getUsername(), user.getUsername());
        Assert.assertEquals(registerUserDto.getEmail(), user.getEmail());
        //CHECK IS PASSWORD ENCRYPTED
        Assert.assertNotEquals(registerUserDto.getPassword(), user.getPassword());
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenPasswordUpdate_Then_ThrowEntityNotFoundException() {
        usersService.passwordUpdate(userPasswordUpdateDto, user.getUsername());
    }

    @Test(expected = InvalidPasswordException.class)
    public void whenPasswordUpdate_Then_ThrowInvalidPasswordException_PassAndConfirmPassNotTheSame() {
        userPasswordUpdateDto.setConfirmPassword("");
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        usersService.passwordUpdate(userPasswordUpdateDto, user.getUsername());
    }

    @Test(expected = InvalidPasswordException.class)
    public void whenPasswordUpdate_Then_ThrowInvalidPasswordException_PasswordIsLessThan8Symbols() {
        userPasswordUpdateDto.setPassword("1234@@");
        userPasswordUpdateDto.setConfirmPassword("1234@@");
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        usersService.passwordUpdate(userPasswordUpdateDto, user.getUsername());
    }

    @Test(expected = InvalidPasswordException.class)
    public void whenPasswordUpdate_Then_ThrowInvalidPasswordException_PassContainsWrongChars() {
        userPasswordUpdateDto.setPassword("12345678./,;'[]()");
        userPasswordUpdateDto.setConfirmPassword("12345678./,;'[]()");
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        usersService.passwordUpdate(userPasswordUpdateDto, user.getUsername());
    }

    @Test
    public void whenPasswordUpdate_Then_Success() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        usersService.passwordUpdate(userPasswordUpdateDto, user.getUsername());
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenPersonalDataUpdate_Then_ThrowEntityNotFoundException() {
        usersService.personalDataUpdate(userPersonalDataUpdateDto, user.getUsername());
    }

    @Test
    public void check_PersonalDataChangeSuccessful() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userProfilesRepository.findByUser(user)).thenReturn(java.util.Optional.ofNullable(userProfile));
        Mockito.when(rolesRepository.findByAuthority(USER)).thenReturn(role);
        usersService.personalDataUpdate(userPersonalDataUpdateDto, user.getUsername());
        Assert.assertEquals(userPersonalDataUpdateDto.getEmail(), user.getEmail());
        Assert.assertEquals(userPersonalDataUpdateDto.getFullName(), userProfile.getFullName());
        Assert.assertEquals(userPersonalDataUpdateDto.getPhone(), userProfile.getPhoneNumber());
    }

    @Test(expected = EntityNotFoundException.class)
    public void whenFindByUsername_Then_ThrowEntityNotFoundException() {
        usersService.findByUsername(Mockito.anyString());
    }

    @Test
    public void whenFindByUsername_Then_ReturnUser() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        Assert.assertEquals(usersService.findByUsername(Mockito.anyString()), user);
    }

    @Test
    public void whenGetAdminPageDtoUserProfiles_Then_CheckReturnListSize() {
        Mockito.when(userProfilesRepository.findAll()).thenReturn(userProfileList);
        List<UserPersonalDataDto> userPersonalDataDtoList = usersService.getAdminPageDtoUserProfiles();
        Assert.assertEquals(userPersonalDataDtoList.size(), 2);
    }

    @Test
    public void whenGetNumberOfResults_thenCheckByUserName() {
        Mockito.when(userProfilesRepository.findAllByUserName(Mockito.anyString())).thenReturn(2L);
        long result = usersService.getNumberOfResults(" ", "", "");
        Assert.assertEquals(result, 2);
    }

    @Test
    public void whenGetNumberOfResults_thenCheckByEmail() {
        Mockito.when(userProfilesRepository.findAllByEmail(Mockito.anyString())).thenReturn(2L);
        long result = usersService.getNumberOfResults("", " ", "");
        Assert.assertEquals(result, 2);
    }

    @Test
    public void whenGetNumberOfResults_thenCheckByPhone() {
        Mockito.when(userProfilesRepository.findAllByPhone(Mockito.anyString())).thenReturn(2L);
        long result = usersService.getNumberOfResults("", "", " ");
        Assert.assertEquals(result, 2);
    }

    @Test
    public void whenGetNumberOfResults_thenCheckGeneral() {
        Mockito.when(userProfilesRepository.count()).thenReturn(2L);
        long result = usersService.getNumberOfResults("", "", "");
        Assert.assertEquals(result, 2);
    }

    @Test
    public void when_GetAdminPageDtoUserProfiles_CheckSorting_FullNameAsc() {
        Page<UserProfile> userProfilePage = new PageImpl(userProfileList);
        Mockito.when(userProfilesRepository.findAll(Mockito.any(Pageable.class))).thenReturn(userProfilePage);
        List<UserPersonalDataDto> userPersonalDataDtoList = usersService.getAdminPageDtoUserProfiles(1, "name:asc", "", "", "");
        Assert.assertEquals(2, userPersonalDataDtoList.size());
    }

    @Test
    public void when_activateUser_thenReturnEnabledUser() {
        user.setEnabled(false);
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        //Mockito.when(usersService.findByUsername(Mockito.anyString())).thenReturn(user);
        usersService.activateUser(Mockito.anyString());
        Assert.assertEquals(true, user.isEnabled());
    }

    @Test
    public void whenGetUserPersonalData_thenReturnUserPersonalDataDTO() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userProfilesService.findByUser(user)).thenReturn(userProfile);
        UserPersonalDataDto newDto = usersService.getUserPersonalData(user.getUsername());
        Assert.assertEquals(user.getUsername(), newDto.getUsername());
        Assert.assertEquals(user.getEmail(), newDto.getEmail());
        Assert.assertEquals(userProfile.getFullName(), newDto.getFullname());
        Assert.assertEquals(userProfile.getPhoneNumber(), newDto.getPhone());
        Assert.assertEquals(userProfile.getPicture().getPictureUrl(), newDto.getPictureUrl());
    }

    @Test
    public void whenBlockUser_thenCheckResult() {
        user.setBlocked(false);
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(usersRepository.save(user)).thenReturn(user);
        usersService.blockUser(user.getUsername());
        Assert.assertEquals(true, user.isBlocked());
    }

    @Test
    public void whenUnBlockUser_thenCheckResult() {
        user.setBlocked(true);
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(usersRepository.save(user)).thenReturn(user);
        usersService.unBlockUser(user.getUsername());
        Assert.assertEquals(false, user.isBlocked());
    }

    @Test
    public void when_userIsAdmin_checkNotAdmin() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        Assert.assertEquals(usersService.userIsAdmin(user.getUsername()), false);
    }

    @Test
    public void when_userIsAdmin_checkIsAdmin() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        role = new Role();
        role.setAuthority("ADMIN");
        user.getAuthorities().add(role);
        Assert.assertEquals(usersService.userIsAdmin(user.getUsername()), true);
    }

    @Test
    public void when_getUserRecipients_then_checkListSize_and_RecepientUserName() {
        Mockito.when(usersRepository.findByUsername(Mockito.anyString())).thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(userProfilesService.findByUser(Mockito.any(User.class))).thenReturn(userProfile);
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        transactionList.add(transaction2);
        Mockito.when(transactionsRepository.findAllBySender(userProfile)).thenReturn(transactionList);
        Assert.assertEquals(1, usersService.getUserRecipients(user.getUsername()).size());
        Assert.assertEquals(userProfile2.getUser().getUsername(), usersService.getUserRecipients(user.getUsername()).get(0));
    }

    @Test
    public void when_getAllUsernames_thenCheckSize_and_collectResults() {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);
        Mockito.when(usersRepository.findAll()).thenReturn(userList);
        Assert.assertEquals(2, usersService.getAllUsernames().size());
    }

    @Test
    public void when_getNumberOfButtons_thenCorrectResult() {
        Long result = usersService.getNumberOfButtons(16);
        Assert.assertEquals(4, Integer.parseInt(String.valueOf(result)));
    }

}
