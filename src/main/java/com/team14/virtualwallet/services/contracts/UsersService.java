package com.team14.virtualwallet.services.contracts;

import com.team14.virtualwallet.models.User;

import com.team14.virtualwallet.models.dtos.userdto.UserPasswordUpdateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataUpdateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserRegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UsersService extends UserDetailsService {

    User register(UserRegisterDto model);

    void passwordUpdate(UserPasswordUpdateDto userPasswordUpdateDto, String username);

    void personalDataUpdate(UserPersonalDataUpdateDto userPersonalDataUpdateDto, String username);

    void profilePictureUpdate(String username, MultipartFile picturePart) throws IOException;

    User findByUsername(String username);

    User activateUser(String username);

    UserPersonalDataDto getUserPersonalData(String username);

    void blockUser(String username);

    void unBlockUser(String username);

    List<UserPersonalDataDto> getAdminPageDtoUserProfiles();

    long getNumberOfResults(String username, String email, String phone);

    List<UserPersonalDataDto> getAdminPageDtoUserProfiles(int page, String sort, String filter, String email, String phone);

    boolean userIsAdmin(String username);

    List<String> getUserRecipients(String username);

    List<String> getAllUsernames();

	List<UserPersonalDataDto> findFiveOldest();

	long getNumberOfButtons(long numberOfPages);}
