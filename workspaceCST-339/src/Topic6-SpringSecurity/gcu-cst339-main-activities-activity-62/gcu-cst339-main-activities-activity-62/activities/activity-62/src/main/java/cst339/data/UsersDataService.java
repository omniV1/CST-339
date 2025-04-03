package cst339.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cst339.data.entity.UserEntity;
import cst339.data.repository.UsersRepository;

@Service
public class UsersDataService {
    
    private final UsersRepository usersRepository;
    
    @Autowired
    public UsersDataService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    
    public UserEntity findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }
} 