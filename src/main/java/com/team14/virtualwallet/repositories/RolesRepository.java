package com.team14.virtualwallet.repositories;

import com.team14.virtualwallet.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Role, Long> {

    Role findByAuthority(String authority);

    @Override
    void deleteById(Long aLong);
}
