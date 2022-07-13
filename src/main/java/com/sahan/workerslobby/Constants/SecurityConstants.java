package com.sahan.workerslobby.Constants;

public class SecurityConstants
{
    public static final long EXPIRATION_TIME = 432_000_00; // 5 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String SAHAN_SYSTEMS = "Sahan systems and solutions";
    public static final String SAHAN_SYSTEMS_ADMINISTRATION = "workers lobby";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "you need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/user/log-in", "/user/register", "/user/reset-password/**", "/user/image/**"};

}
