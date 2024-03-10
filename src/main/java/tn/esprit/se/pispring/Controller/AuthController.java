package tn.esprit.se.pispring.Controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.se.pispring.DTO.Request.AuthenticationRequest;
import tn.esprit.se.pispring.DTO.Request.UserSignupRequest;
import tn.esprit.se.pispring.DTO.Response.AuthenticationResponse;
import tn.esprit.se.pispring.Service.UserService;
import tn.esprit.se.pispring.entities.User;
import tn.esprit.se.pispring.secConfig.JwtUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest userReq) throws Exception {

        try {
            String result = userService.signup(userReq);

            return ResponseEntity.ok(result);
        }catch (Exception e) {
            throw new Exception(e);
        }

    }

    @PostMapping()
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest, final HttpServletResponse response) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad Credentials");
        }

        final UserDetails user = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        User user1 = userService.findByEmail(user.getUsername());

//        String refreshToken = refreshTokenService.generateNewRefreshTokenForUser(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
//        Cookie cookie = createRefreshTokenCookie(refreshToken);
//        response.addCookie(cookie);
        AuthenticationResponse auth = new AuthenticationResponse(user1.getId(), jwtUtils.generateToken(user, claims), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return ResponseEntity.ok(auth);
    }



}
