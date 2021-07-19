package com.docker.first.configuration;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import io.jsonwebtoken.Claims;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.docker.first.dao.ResponseAuthServer;
import com.docker.first.repositories.UsersRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // @Autowired
    // private UsersRepository userRepo;

    @Autowired
    private WebClient webClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || header.contains("null") || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();

        // check token from auth server
        ResponseAuthServer r = new ResponseAuthServer();

        try {
            r = webClient.post()
                .uri("/api/auth/checkToken")
                .body(Mono.just(Map.of("value", token)), Object.class)
                .retrieve()
                .bodyToMono(ResponseAuthServer.class)
                .block()
                ;
                
        } catch (Exception ex) {
            String e = ex.getMessage();
        }

        if (r.code != 1 /* || !jwtTokenUtil.validate(token) */) {
            chain.doFilter(request, response);
            return;
        }

        String email = r.email;// jwtTokenUtil.getByClaim(token, "email");
        String role = r.role;// jwtTokenUtil.getByClaim(token, "role");

        // add role user to current context, for matter of autorization in controller
        // level
        List<GrantedAuthority> list = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, token,
                list);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
