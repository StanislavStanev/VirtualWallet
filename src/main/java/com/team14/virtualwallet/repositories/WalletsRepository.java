package com.team14.virtualwallet.repositories;

import com.team14.virtualwallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WalletsRepository extends JpaRepository<Wallet, Long> {

    @Query(value = "SELECT coalesce(upw.role_id,0) \n" +
            "FROM user_profiles_wallets upw \n" +
            "WHERE upw.user_profile_id=:user_profile_id \n" +
            "and upw.wallets_id=:wallet_id",
            nativeQuery = true)
    Long findUserProfileWalletRole(Long user_profile_id, Long wallet_id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profiles_wallets \n" +
            "SET role_id = :role \n" +
            "WHERE user_profile_id=:user_profile_id \n" +
            "and wallets_id=:wallet_id",
            nativeQuery = true)
    void updateUserProfileWalletRole(@Param("user_profile_id") Long user_profile_id,
                                     @Param("wallet_id") Long wallet_id,
                                     @Param("role") Long role);
}
