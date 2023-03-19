package ru.simbir.projectmanagement.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.simbir.projectmanagement.model.User;
import ru.simbir.projectmanagement.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
        return JwtUserDetails.builder()
                .username(user.getEmail())
                .id(user.getId())
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .password(user.getPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .build();
    }
}
