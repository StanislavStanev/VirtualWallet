package com.team14.virtualwallet.repositories;

import com.team14.virtualwallet.models.ConfirmationToken;
import com.team14.virtualwallet.models.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConfirmationTokensRepository extends JpaRepository<ConfirmationToken, String> {

    ConfirmationToken findByConfirmationToken(String confirmationToken);

    @Query("select c from ConfirmationToken c where c.user.id=:userId and c.confirmationToken=:confirmationToken and c.isUsed=false")
    ConfirmationToken findByUserAndToken(Long userId,String confirmationToken);
}
