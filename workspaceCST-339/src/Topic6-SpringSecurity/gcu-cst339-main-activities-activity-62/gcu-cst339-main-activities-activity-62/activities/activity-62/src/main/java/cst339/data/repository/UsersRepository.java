package cst339.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import cst339.data.entity.UserEntity;

public interface UsersRepository extends MongoRepository<UserEntity, String> {

	UserEntity findByUsername(String username);
}
