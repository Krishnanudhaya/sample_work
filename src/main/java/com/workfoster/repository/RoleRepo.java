package com.workfoster.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workfoster.model.Role;
import com.workfoster.model.RoleName;


@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleName roleName);
}
