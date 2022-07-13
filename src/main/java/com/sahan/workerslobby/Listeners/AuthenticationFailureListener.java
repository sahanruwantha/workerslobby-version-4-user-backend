package com.sahan.workerslobby.Listeners;

import com.sahan.workerslobby.Services.LoginAttemptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationFailureListener
{
    private LoginAttemptsService loginAttemptsService;

    @Autowired
    public AuthenticationFailureListener(LoginAttemptsService loginAttemptsService)
    {
        this.loginAttemptsService = loginAttemptsService;
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event)
    {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String)
        {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptsService.addUserToLoginAttemptCache(username);
        }
    }
}
