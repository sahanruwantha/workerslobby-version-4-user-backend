package com.sahan.workerslobby.Enumerations;

import lombok.Getter;

import static com.sahan.workerslobby.Constants.Authorities.*;

@Getter
public enum Role
{
    ROLE_ENGINEER(ENGINEER_AUTHORITIES),
    ROLE_PROJECT_MANAGER(PROJECT_MANAGER_AUTHORITIES),
    ROLE_CLIENT(CLIENT_AUTHORITIES);
    private String[] authorities;

    Role(String... authorities)
    {
        this.authorities = authorities;
    }
}
