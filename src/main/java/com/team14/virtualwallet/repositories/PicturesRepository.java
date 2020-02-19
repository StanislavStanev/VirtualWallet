package com.team14.virtualwallet.repositories;

import com.team14.virtualwallet.models.BankCard;
import com.team14.virtualwallet.models.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PicturesRepository extends JpaRepository<Picture, Long> {
}
