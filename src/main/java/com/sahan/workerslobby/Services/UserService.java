package com.sahan.workerslobby.Services;

import com.sahan.workerslobby.Entities.User;
import com.sahan.workerslobby.Exceptions.EmailExistsException;
import com.sahan.workerslobby.Exceptions.EmailNotFoundException;
import com.sahan.workerslobby.Exceptions.UserNameExistsException;
import com.sahan.workerslobby.Exceptions.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService
{
    User register(String firstName,
                  String lastName,
                  String username,
                  String email) throws UserNameExistsException, EmailExistsException, UserNotFoundException;

    List<User> getUsers();

    User findByUserName(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName,
                    String lastname,
                    String username,
                    String email,
                    String role,
                    boolean isNonLocked,
                    boolean isActive,
                    MultipartFile profileImage
                    ) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException;
    User updateUser(String currentUserName,
                    String newFirstName,
                    String newLastname,
                    String newUsername,
                    String newEmail,
                    String role,
                    boolean isNonLocked,
                    boolean isActive,
                    MultipartFile profileImage
    ) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException;

    void deleteUser(long id);

    void resetPassword(String email) throws EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException;

    User validateUser(long userId) throws UserNotFoundException;

    User validateUserById(long id) throws UserNotFoundException;

    List<User> getAllEngineers();
}
