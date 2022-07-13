package com.sahan.workerslobby.Constants;

public class Authorities
{
    public static final String[] ENGINEER_AUTHORITIES = { "user:read", "ticket:create", "task:read"};

    public static final String[]    CLIENT_AUTHORITIES = { "user:read", "ticket:create" };

    public static final String[] PROJECT_MANAGER_AUTHORITIES = { "user:read", "user:create", "user:update", "user:delete", "ticket:delete, task:create" };
}
