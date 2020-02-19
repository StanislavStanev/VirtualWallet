package com.team14.virtualwallet.repositories;


import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.models.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfilesRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);

    Page<UserProfile> findAll(Pageable pageable);

    @Query("select up from UserProfile up where up.user.username like :username% ")
    Page<UserProfile> findAllByUsername(Pageable pageable, String username);

    @Query("select up from UserProfile up where up.user.email like :email% ")
    Page<UserProfile> findAllByEmail(Pageable pageable, String email);

    @Query("select up from UserProfile up where up.phoneNumber like :phone% ")
    Page<UserProfile> findAllByPhone(Pageable pageable, String phone);

    @Query("SELECT count(up) FROM UserProfile up where up.user.username like %:keyword%")
    long findAllByUserName(@Param("keyword") String keyword);

    @Query("SELECT count(up) FROM UserProfile up where up.phoneNumber like %:keyword%")
    long findAllByPhone(@Param("keyword") String keyword);

    @Query("SELECT count(up) FROM UserProfile up where up.user.email like %:keyword%")
    long findAllByEmail(@Param("keyword") String keyword);

    @Query("SELECT up.user.username FROM UserProfile up where up.user.username like %:keyword%")
    List<String> searchByUsername(@Param("keyword") String keyword);

    @Query("SELECT up.user.username FROM UserProfile up where up.user.username = :keyword")
    String searchByExactUsername(@Param("keyword") String keyword);

    @Query("SELECT up.user.username FROM UserProfile up where up.phoneNumber like %:keyword%")
    List<String> searchByPhone(@Param("keyword") String keyword);

    @Query("SELECT up.user.username FROM UserProfile up where up.user.email like %:keyword%")
    List<String> searchByEmail(@Param("keyword") String keyword);

    @Query("SELECT up.user.username FROM UserProfile up where up.phoneNumber = :keyword")
    String searchByExactPhone(@Param("keyword") String keyword);

    @Query("SELECT up.user FROM UserProfile up where up.user.username = :username")
    User findByUserName(@Param("username") String username);

    @Query("SELECT up FROM UserProfile up where up.user.username like %:keyword%")
    List<UserProfile> searchByUsernameGetPictureUrl(@Param("keyword") String keyword);

    @Query("SELECT up FROM UserProfile up where up.user.email like %:keyword%")
    List<UserProfile> searchByEmailGetPictureUrl(@Param("keyword") String keyword);

    @Query("SELECT up FROM UserProfile up where up.phoneNumber like %:keyword%")
    List<UserProfile> searchByPhoneGetPictureUrl(@Param("keyword") String keyword);

    List<UserProfile> findAll();

    @Query("SELECT up FROM UserProfile up")
    List<UserProfile> findFiveOldest(Pageable pageable);
}
