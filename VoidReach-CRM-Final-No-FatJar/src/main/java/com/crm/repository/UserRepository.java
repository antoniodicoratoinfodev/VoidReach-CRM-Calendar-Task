package com.crm.repository;

import com.crm.model.UserAccount;
import java.util.Optional;

/** Persistence boundary. Replace LocalUserRepository with a JDBC implementation later. */
public interface UserRepository {
    Optional<UserAccount> findByEmail(String email);
    void save(UserAccount user);
}
