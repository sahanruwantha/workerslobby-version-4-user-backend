package com.sahan.workerslobby.Listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahan.workerslobby.Entities.User;
import com.sahan.workerslobby.Services.LoginAttemptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener
{
    private LoginAttemptsService loginAttemptsService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptsService loginAttemptsService)
    {
        this.loginAttemptsService = loginAttemptsService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event)
    {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof User)
        {
            User user = (User)event.getAuthentication().getPrincipal();
            loginAttemptsService.evictUserFromLoginAttemptCache(user.getUserName());
        }
    }
}
