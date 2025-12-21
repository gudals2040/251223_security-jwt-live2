package kr.java.jwt.service;

import kr.java.jwt.model.entity.CustomUserDetails;
import kr.java.jwt.model.entity.UserAccount;
import kr.java.jwt.model.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음: " + email));
        return CustomUserDetails.from(user);
    }

    public UserDetails loadUserById(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음: ID=" + userId));
        return CustomUserDetails.from(user);
    }
}
