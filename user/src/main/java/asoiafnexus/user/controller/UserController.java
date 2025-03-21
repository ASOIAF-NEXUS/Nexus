package asoiafnexus.user.controller;

import asoiafnexus.user.model.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserDetailsService users;

    @Autowired
    JwtEncoder encoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Login login) {
        var mgr = ((InMemoryUserDetailsManager) users);

        if(mgr.userExists(login.username())) {
            return ResponseEntity.badRequest().build();
        }

        mgr.createUser(
                org.springframework.security.core.userdetails.User
                        .withUsername(login.username())
                        .password(login.password())
                        .build());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        var mgr = ((InMemoryUserDetailsManager) users);

        if(!mgr.userExists(login.username())) {
            return ResponseEntity.badRequest().build();
        }
        var auth = mgr.loadUserByUsername(login.username());
        if(!Objects.equals(login.password(), auth.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        var now = Instant.now();
        long expiry = 36000L;

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(auth.getUsername())
                .claim("scope", scope)
                .build();

        return ResponseEntity.ok()
                .body(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
    }
}
