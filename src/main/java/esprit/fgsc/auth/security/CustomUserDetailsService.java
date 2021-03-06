package esprit.fgsc.auth.security;

import esprit.fgsc.auth.exception.ResourceNotFoundException;
import esprit.fgsc.auth.model.UserAccount;
import esprit.fgsc.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info(email);
        UserAccount user = userRepository.findFirstByEmail(email).blockOptional().orElseThrow(() ->
            new UsernameNotFoundException("User not found with email : " + email)
        );
        return UserPrincipal.create(user);
    }
    @Transactional
    public UserDetails loadUserById(String id) {
        log.info("ID FROM TOKEN : {}",id);
        UserAccount user = userRepository.findById(id).blockOptional().orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );
        return UserPrincipal.create(user);
    }
}