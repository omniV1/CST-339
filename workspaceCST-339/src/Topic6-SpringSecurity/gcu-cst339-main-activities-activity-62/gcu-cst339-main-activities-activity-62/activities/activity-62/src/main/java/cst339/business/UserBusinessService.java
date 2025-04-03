package cst339.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cst339.data.UsersDataService;
import cst339.data.entity.UserEntity;

@Service
public class UserBusinessService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserBusinessService.class);
    private final UsersDataService service;
    
    @Autowired
    public UserBusinessService(UsersDataService service) {
        this.service = service;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        UserEntity user = service.findByUsername(username);
        if (user != null) {
            logger.debug("Found user: {}", user.getUsername());
            logger.debug("User password hash: {}", user.getPassword());
            
            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        }
        
        logger.debug("User not found: {}", username);
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
