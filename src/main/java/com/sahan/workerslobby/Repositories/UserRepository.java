package com.sahan.workerslobby.Repositories;

import com.sahan.workerslobby.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long>
{
    User findUserByUserName(String username);
    User findUserByEmail(String email);

    List<User> findUsersByRole(String role);
}

