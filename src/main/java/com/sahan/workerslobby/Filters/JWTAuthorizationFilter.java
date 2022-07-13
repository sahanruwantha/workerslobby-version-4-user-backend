package com.sahan.workerslobby.Filters;

import static com.sahan.workerslobby.Constants.SecurityConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import com.sahan.workerslobby.Utils.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/* whatever happening to this class it's going to be happened once */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter
{
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public JWTAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        /* if the request is coming to an open end point just let it through*/
        if (request.getMethod().equals(OPTIONS_HTTP_METHOD))
        {
            response.setStatus(OK.value());
        }else
        {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            /* if not check of there is any on in it or if the header belongs to us */
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX))
            {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username = jwtTokenProvider.getSubject(token);
            if (jwtTokenProvider.isTokenValid(username, token)
                    && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);

                /* set user as an authenticated user in security context holder */
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else
            {
                SecurityContextHolder.clearContext();;
            }
        }
        filterChain.doFilter(request,response);
    }
}
