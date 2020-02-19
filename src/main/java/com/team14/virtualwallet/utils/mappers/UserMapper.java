package com.team14.virtualwallet.utils.mappers;

import com.team14.virtualwallet.models.UserProfile;
import com.team14.virtualwallet.models.dtos.userdto.UserPersonalDataDto;
import com.team14.virtualwallet.models.dtos.userdto.UserPictureDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public static UserPersonalDataDto mapUserProfileToUpdateUser(UserProfile userProfile, boolean isEnabled) {
        UserPersonalDataDto userPersonalDataDto = new UserPersonalDataDto();

        userPersonalDataDto.setFullname(userProfile.getFullName());
        userPersonalDataDto.setPhone(userProfile.getPhoneNumber());
        userPersonalDataDto.setEmail(userProfile.getUser().getEmail());
        userPersonalDataDto.setUsername(userProfile.getUser().getUsername());
        userPersonalDataDto.setEnabled(isEnabled);
        userPersonalDataDto.setPictureUrl(userProfile.getPicture().getPictureUrl());
        userPersonalDataDto.setCreatedBeforeHours(getCreatedBeforeHoursString(userProfile));

        return userPersonalDataDto;
    }

    public static UserPersonalDataDto mapUserProfileToUpdateUser(UserProfile userProfile) {
        UserPersonalDataDto userPersonalDataDto = new UserPersonalDataDto();

        userPersonalDataDto.setFullname(userProfile.getFullName());
        userPersonalDataDto.setPhone(userProfile.getPhoneNumber());
        userPersonalDataDto.setEmail(userProfile.getUser().getEmail());
        userPersonalDataDto.setUsername(userProfile.getUser().getUsername());
        userPersonalDataDto.setBlocked(userProfile.getUser().isBlocked());
        userPersonalDataDto.setPictureUrl(userProfile.getPicture().getPictureUrl());
        userPersonalDataDto.setCreatedBeforeHours(getCreatedBeforeHoursString(userProfile));

        return userPersonalDataDto;
    }

    public static UserPictureDto mapUserProfileToPictureDto(UserProfile userProfile) {
        UserPictureDto userPictureDto = new UserPictureDto();
        userPictureDto.setUsername(userProfile.getUser().getUsername());
        userPictureDto.setValue(userProfile.getUser().getUsername());
        userPictureDto.setPictureUrl(userProfile.getPicture().getPictureUrl());
        return userPictureDto;
    }

    public static List<UserPersonalDataDto> mapUserProfilesToList(Page<UserProfile> userProfileList) {
        List<UserPersonalDataDto> userPersonalDataDtoList = new ArrayList<>();
        userProfileList.get()

                .forEach(userProfile -> userPersonalDataDtoList.add(mapUserProfileToUpdateUser(userProfile)));
        return userPersonalDataDtoList;
    }

    public static List<UserPersonalDataDto> mapUserProfilesToList(List<UserProfile> userProfileList) {
        List<UserPersonalDataDto> userPersonalDataDtoList = new ArrayList<>();
        userProfileList.stream()

                .forEach(userProfile -> userPersonalDataDtoList.add(mapUserProfileToUpdateUser(userProfile)));
        return userPersonalDataDtoList;
    }

    public static List<UserPictureDto> mapUserProfilesToPictureList(List<UserProfile> userProfileList) {
        List<UserPictureDto> userPictureDtoList = new ArrayList<>();
        userProfileList.stream()

                .forEach(userProfile -> userPictureDtoList.add(mapUserProfileToPictureDto(userProfile)));
        return userPictureDtoList;
    }

    private static String getCreatedBeforeHoursString(UserProfile userProfile) {

        if (userProfile.getUser().getCreatedOn().isAfter(LocalDateTime.now().minusMinutes(59))) {
            return String.format("%d Minutes ago",  ChronoUnit.MINUTES.between(userProfile.getUser().getCreatedOn(), LocalDateTime.now()));
        } else {
            return String.format("%d Hours ago",  ChronoUnit.HOURS.between(userProfile.getUser().getCreatedOn(), LocalDateTime.now()));
        }
    }
}
