package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.DuplicateEntityException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.exceptions.InvalidPasswordException;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.UserProfile;
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
import com.team14.virtualwallet.utils.ModelFactory;
import com.team14.virtualwallet.utils.PictureUploader;
import com.team14.virtualwallet.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

    public static final String USER = "USER";
    public static final String USER_WITH_SUCH_USERNAME_ALREADY_EXISTS = "User with username %s already exists.";
    public static final String USER_WITH_SUCH_EMAIL_ALREADY_EXISTS = "User with email %s already exists.";
    public static final String USER_WITH_USERNAME_DOESNT_EXIST = "User with username %s doesn't exists.";
    public static final int DATA_RECORDS_PER_PAGE = 5;

    private static final String PASSWORDS_ARE_NOT_SAME = "Password and Confirm Password don't match.";
    private static final String PASSWORD_USES_WRONG_SYMBOLS = "Password must contain only letters, numbers and special characters (!@#$%^&*()?) and be at least 8 symbols long.";
    private final UserProfilesService userProfilesService;

    private final UsersRepository usersRepository;
    private final UserProfilesRepository userProfilesRepository;
    private final RolesRepository rolesRepository;
    private final TransactionsRepository transactionsRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PictureUploader pictureUploader;
    private Pageable fiveUsersPerPage;

    @Autowired
    public UsersServiceImpl(UserProfilesService userProfilesService, UsersRepository usersRepository, UserProfilesRepository userProfilesRepository, RolesRepository rolesRepository, TransactionsRepository transactionsRepository, BCryptPasswordEncoder bCryptPasswordEncoder, PictureUploader pictureUploader) {
        this.userProfilesService = userProfilesService;
        this.usersRepository = usersRepository;
        this.userProfilesRepository = userProfilesRepository;
        this.rolesRepository = rolesRepository;
        this.transactionsRepository = transactionsRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.pictureUploader = pictureUploader;
    }

    @Override
    public User register(UserRegisterDto userRegisterDto) {


        User user = this.usersRepository.findByUsername(userRegisterDto.getUsername()).orElse(null);

        if (user != null) {
            throw new DuplicateEntityException(String.format(USER_WITH_SUCH_USERNAME_ALREADY_EXISTS, userRegisterDto.getUsername()));
        }

        boolean userWithEmailExists = usersRepository.existsByEmail(userRegisterDto.getEmail());

        if (userWithEmailExists) {
            throw new IllegalArgumentException(String.format(USER_WITH_SUCH_EMAIL_ALREADY_EXISTS, userRegisterDto.getEmail()));
        }

        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            throw new InvalidPasswordException(PASSWORDS_ARE_NOT_SAME);
        }

        //checktoken

        user = ModelFactory.createUser();

        user.setUsername(userRegisterDto.getUsername());
        user.setEmail(userRegisterDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDto.getPassword()));
        user.getAuthorities().add(rolesRepository.findByAuthority(USER));
        user.setCreatedOn(LocalDateTime.now());

        User createdUser = usersRepository.save(user);

        UserProfile userProfile = userProfilesService.create(createdUser);

        userProfilesService.saveToDb(userProfile);

        //give money

        return createdUser;
    }

    @Override
    public void passwordUpdate(UserPasswordUpdateDto userPasswordUpdateDto, String username) {
        User userFromDb = usersRepository.findByUsername(username).orElse(null);

        if (userFromDb == null) {
            throw new EntityNotFoundException(String.format(USER_WITH_USERNAME_DOESNT_EXIST, username));
        }

        if (!userPasswordUpdateDto.getPassword().equals(userPasswordUpdateDto.getConfirmPassword())) {
            throw new InvalidPasswordException(PASSWORDS_ARE_NOT_SAME);
        }

        if (!passwordSymbolsValid(userPasswordUpdateDto.getPassword())) {
            throw new InvalidPasswordException(PASSWORD_USES_WRONG_SYMBOLS);
        }

        userFromDb.setPassword(bCryptPasswordEncoder.encode(userPasswordUpdateDto.getPassword()));
        usersRepository.save(userFromDb);
    }

    @Override
    public void personalDataUpdate(UserPersonalDataUpdateDto userPersonalDataUpdateDto, String username) {
        User userFromDb = usersRepository.findByUsername(username).orElse(null);
        UserProfile userProfile = userProfilesRepository.findByUser(userFromDb).orElse(null);

        if (userFromDb == null || userProfile == null) {
            throw new EntityNotFoundException(String.format(USER_WITH_USERNAME_DOESNT_EXIST, username));
        }

        updateEmail(userFromDb, userPersonalDataUpdateDto.getEmail());
        updateFullname(userProfile, userPersonalDataUpdateDto.getFullName());
        updatePhoneNumber(userProfile, userPersonalDataUpdateDto.getPhone());

        usersRepository.save(userFromDb);
        userProfilesService.saveToDb(userProfile);
    }

    @Override
    public void profilePictureUpdate(String username, MultipartFile picturePart) throws IOException {
        User user = findByUsername(username);
        UserProfile userProfile = this.userProfilesService.findByUser(user);

        if (user == null || userProfile == null) {
            throw new EntityNotFoundException(String.format(USER_WITH_USERNAME_DOESNT_EXIST, username));
        }

        String pictureUrl = pictureUploader.uploadPicture(picturePart);

        userProfile.getPicture().setPictureUrl(pictureUrl);
        this.userProfilesRepository.save(userProfile);
    }

    @Override
    public User findByUsername(String username) {
        User user = this.usersRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new EntityNotFoundException(String.format(USER_WITH_USERNAME_DOESNT_EXIST, username));
        }

        return user;
    }

    @Override
    public List<UserPersonalDataDto> getAdminPageDtoUserProfiles() {
        return UserMapper.mapUserProfilesToList(userProfilesRepository.findAll());
    }

    @Override
    public List<UserPersonalDataDto> findFiveOldest() {
        return UserMapper.mapUserProfilesToList(userProfilesRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())));
    }

    public long getNumberOfResults(String username, String email, String phone) {

        if (username.length() > 0) {
            return userProfilesRepository.findAllByUserName(username);
        }

        if (email.length() > 0) {
            return userProfilesRepository.findAllByEmail(email);
        }

        if (phone.length() > 0) {
            return userProfilesRepository.findAllByPhone(phone);
        }

        return userProfilesRepository.count();
    }

    @Override
    public List<UserPersonalDataDto> getAdminPageDtoUserProfiles(int page, String sort, String username, String email, String phone) {

        Page<UserProfile> result;

        if (sort.equals("name:asc")) {
            fiveUsersPerPage = PageRequest.of(page, DATA_RECORDS_PER_PAGE, Sort.by("fullName").ascending());
        }

        if (sort.equals("name:desc")) {
            fiveUsersPerPage = PageRequest.of(page, DATA_RECORDS_PER_PAGE, Sort.by("fullName").descending());
        }

        if (sort.equals("username:asc")) {
            fiveUsersPerPage = PageRequest.of(page, DATA_RECORDS_PER_PAGE, Sort.by("user.username").ascending());
        }

        if (sort.equals("username:desc")) {
            fiveUsersPerPage = PageRequest.of(page, DATA_RECORDS_PER_PAGE, Sort.by("user.username").descending());
        }

        if (sort.equals("email:asc")) {
            fiveUsersPerPage = PageRequest.of(page, DATA_RECORDS_PER_PAGE, Sort.by("user.email").ascending());
        }

        if (sort.equals("email:desc")) {
            fiveUsersPerPage = PageRequest.of(page, DATA_RECORDS_PER_PAGE, Sort.by("user.email").descending());
        }

        if (sort.length() == 0) {
            fiveUsersPerPage = PageRequest.of(page, DATA_RECORDS_PER_PAGE);
        }

        if (username.length() > 0) {
            result = userProfilesRepository.findAllByUsername(fiveUsersPerPage, username);
            return UserMapper.mapUserProfilesToList(result);
        }

        if (email.length() > 0) {
            result = userProfilesRepository.findAllByEmail(fiveUsersPerPage, email);
            return UserMapper.mapUserProfilesToList(result);
        }

        if (phone.length() > 0) {
            result = userProfilesRepository.findAllByPhone(fiveUsersPerPage, phone);
            return UserMapper.mapUserProfilesToList(result);
        }

        result = userProfilesRepository.findAll(fiveUsersPerPage);

        return UserMapper.mapUserProfilesToList(result);
    }

    @Override
    public User activateUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(true);
        this.usersRepository.save(user);
        return user;
    }

    @Override
    public UserPersonalDataDto getUserPersonalData(String username) {
        User user = findByUsername(username);
        UserProfile userProfile = this.userProfilesService.findByUser(user);

        UserPersonalDataDto userPersonalDataDto = UserMapper.mapUserProfileToUpdateUser(userProfile, user.isEnabled());

        return userPersonalDataDto;
    }

    @Override
    public void blockUser(String username) {
        User blockedUser = findByUsername(username);
        blockedUser.setBlocked(true);
        usersRepository.save(blockedUser);
    }

    @Override
    public void unBlockUser(String username) {
        User unBlockedUser = findByUsername(username);
        unBlockedUser.setBlocked(false);
        usersRepository.save(unBlockedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);

        Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public boolean userIsAdmin(String username) {
        User user = findByUsername(username);
        return user.getAuthorities().stream().anyMatch(r -> r.getAuthority().contains("ADMIN"));
    }

    @Override
    public List<String> getUserRecipients(String username) {
        UserProfile sender = userProfilesService.findByUser(findByUsername(username));
        return this.transactionsRepository.findAllBySender(sender)
                .stream()
                .filter(t -> !t.isTopUp())
                .map(t -> t.getReceiver().getUser().getUsername())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllUsernames() {
        return this.usersRepository.findAll()
                .stream()
                .map(u -> u.getUsername())
                .collect(Collectors.toList());
    }

    @Override
    public long getNumberOfButtons(long numberOfPages) {
        //If number of result is 16 then 4 pages, if 15 then 3 pages
        return (long) Math.ceil((double) numberOfPages / DATA_RECORDS_PER_PAGE);
    }

    private boolean passwordSymbolsValid(String password) {
        Pattern pwPattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()?]{8,55}$");
        Matcher matcher = pwPattern.matcher(password);
        return matcher.matches();
    }

    private void updatePhoneNumber(UserProfile userProfile, String phoneNumber) {
        if (!phoneNumber.equals("")) {
            userProfile.setPhoneNumber(phoneNumber);
        }
    }

    private void updateFullname(UserProfile userProfile, String fullName) {
        if (!fullName.equals("")) {
            userProfile.setFullName(fullName);
        }
    }

    private void updateEmail(User userFromDb, String email) {
        if (!email.equals("")) {
            userFromDb.setEmail(email);
        }
    }
}