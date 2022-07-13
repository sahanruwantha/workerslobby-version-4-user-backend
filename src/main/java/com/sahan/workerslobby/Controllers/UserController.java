package com.sahan.workerslobby.Controllers;

import com.sahan.workerslobby.Entities.User;
import com.sahan.workerslobby.Exceptions.*;
import com.sahan.workerslobby.Services.UserService;
import com.sahan.workerslobby.Utils.HttpResponse;
import com.sahan.workerslobby.Utils.JWTTokenProvider;
import com.sahan.workerslobby.Utils.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.sahan.workerslobby.Constants.FileConstant.*;
import static com.sahan.workerslobby.Constants.SecurityConstants.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Slf4j
@RestController
@RequestMapping(path = { "/","/user"})
public class UserController extends ExceptionHandling
{
    public static final String EMAIL_SENT = "Email with new password sent to";
    public static final String USER_DELETED_SUCCESSFULLY = "user deleted successfully";
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JWTTokenProvider jwtTokenProvider)
    {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/home")
    public String showUser() throws EmailExistsException {
        throw new EmailExistsException("boom chaka laka");
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNameExistsException, EmailExistsException, UserNotFoundException
    {
        log.info(String.valueOf(user));
       User newUser =  userService.register(user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail());
       return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/log-in")
    public ResponseEntity<User> login(@RequestBody User user)
    {
        log.info(String.valueOf(user));
        authenticate(user.getUserName(), user.getPassword());
        User loginUser = userService.findByUserName(user.getUserName());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser( @RequestParam("firstName") String firstName,
                                            @RequestParam("lastName") String lastName,
                                            @RequestParam("username") String username,
                                            @RequestParam("email") String email,
                                            @RequestParam("role") String role,
                                            @RequestParam("isActive") String isActive,
                                            @RequestParam("isNonLocked") String isNonLocked,
                                            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
                                            ) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException {
        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);

        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException
        {
            User updatedUser = userService.updateUser(currentUsername ,firstName, lastName, username, email, role,
                    Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);

            return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username)
    {
        User user = userService.findByUserName(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<User>> getUsers()
    {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/getEngineers")
    public ResponseEntity<List<User>> getEngineers()
    {
        List<User> users = userService.getAllEngineers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable() long id)
    {
        userService.deleteUser(id);
        return response(NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<User> updateUser(
            @RequestParam("username") String username,
            @RequestParam(value = "profileImage") MultipartFile profileImage
    ) throws UserNotFoundException, UserNameExistsException, EmailExistsException, IOException
    {
        User updatedUser = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping(path = "/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("filename")  String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH +filename));
    }

    @GetMapping(path = "image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException
    {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0)
            {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message)
    {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),
                httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String userName, String password)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
    }
}
