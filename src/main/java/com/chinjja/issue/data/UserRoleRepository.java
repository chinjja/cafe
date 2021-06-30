package com.chinjja.issue.data;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, UserRole.Id> {
}
