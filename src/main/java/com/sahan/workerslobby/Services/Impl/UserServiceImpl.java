package com.sahan.workerslobby.Services.Impl;

import com.sahan.workerslobby.Entities.User;
import com.sahan.workerslobby.Enumerations.Role;
import com.sahan.workerslobby.Exceptions.EmailExistsException;
import com.sahan.workerslobby.Exceptions.EmailNotFoundException;
import com.sahan.workerslobby.Exceptions.UserNameExistsException;
import com.sahan.workerslobby.Exceptions.UserNotFoundException;
import com.sahan.workerslobby.Repositories.UserRepository;
import com.sahan.workerslobby.Services.LoginAttemptsService;
import com.sahan.workerslobby.Services.UserService;
import com.sahan.workerslobby.Utils.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.sahan.workerslobby.Constants.FileConstant.*;
import static com.sahan.workerslobby.Constants.FileConstant.DEFAULT_USER_IMAGE_PATH;
import static com.sahan.workerslobby.Constants.UserImplConstants.*;
import static com.sahan.workerslobby.Enumerations.Role.ROLE_ENGINEER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService
{

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private LoginAttemptsService loginAttemptsService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           LoginAttemptsService loginAttemptsService)
    {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptsService = loginAttemptsService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findUserByUserName(username);
        if (user == null)
        {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }else
        {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(RETURNING_FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNameExistsException, EmailExistsException, UserNotFoundException
    {
        validateNewUsernameAndEmail(EMPTY , username, email);
        User user = new User();
        user.setUserID(generateUserId());
        String password = generatePassword();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(username);
        user.setEmail(email);

        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_ENGINEER.toString());
        user.setAuthorities(ROLE_ENGINEER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));

        userRepository.save(user);
        log.info("New user password: " + password);

        return user;
    }

    @Override
    public List<User> getUsers()
    {
        return userRepository.findAll();
    }

    @Override
    public User findByUserName(String username)
    {
        return userRepository.findUserByUserName(username);
    }

    @Override
    public User findUserByEmail(String email)
    {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setUserID(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUserName(username);

        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);

        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        return null;
    }

    @Override
    public User updateUser(String currentUserName, String newFirstName, String newLastname, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {
        User currentUser =  validateNewUsernameAndEmail(currentUserName, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastname);
        currentUser.setEmail(newEmail);
        currentUser.setUserName(newUsername);

        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);

        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(long id)
    {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user   = userRepository.findUserByEmail(email);
        if (user == null)
        {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }

        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
    }

    @Override
    public User updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, newProfileImage);
        return user;
    }


    @Override
    public User validateUser(long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            throw new UserNotFoundException("invalid User Id");
        return user;
    }

    @Override
    public User validateUserById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            throw new UserNotFoundException("Invalid user id");
        return user;
    }

    @Override
    public List<User> getAllEngineers() {
        return userRepository.findUsersByRole("ROLE_ENGINEER");
    }

    private String getTemporaryProfileImageUrl(String username)
    {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private Role getRoleEnumName(String role)
    {
        return Role.valueOf(role.toUpperCase());
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword()
    {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId()
    {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUserName, String newEmail) throws UserNameExistsException, EmailExistsException, UserNotFoundException {
        User newUserByUserName = findByUserName(newUserName);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findByUserName(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }

            if (newUserByUserName != null && !currentUser.getId().equals(newUserByUserName.getId())) {
                throw new UserNameExistsException(USER_NAME_ALREADY_EXISTS);
            }

            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        }else
        {
            if (newUserByUserName != null)
            {
                throw new UserNameExistsException(USER_NAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null)
            {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private void validateLoginAttempt(User user)
    {
        if (user.isNotLocked())
        {
            if (loginAttemptsService.hasExceededMaxAttempts(user.getUserName()))
            {
                user.setNotLocked(false);
            }else
            {
                user.setNotLocked(true);
            }
        }else
        {
            loginAttemptsService.evictUserFromLoginAttemptCache(user.getUserName());
        }
    }


    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null)
        {
            Path userFolder = Paths
                    .get(USER_FOLDER + user.getUserName())
                    .toAbsolutePath().normalize();
            if (!Files.exists(userFolder))
            {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUserName() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUserName() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUserName()));
            userRepository.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String userName)
    {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH + userName + FORWARD_SLASH + userName + DOT + JPG_EXTENSION).toUriString();
    }


}
