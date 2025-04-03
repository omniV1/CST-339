package cst339.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cst339.data.DataAccessInterface;
import cst339.data.entity.OrderEntity;
import cst339.data.entity.UserEntity;
import cst339.data.repository.UsersRepository;

public class UsersDataService implements UsersDataAccessInterface, DataAccessInterface {

	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public UserEntity findByUsername(String username) {

		return usersRepository.findByUsername(username);
	}

	@Override
	public List findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderEntity create(OrderEntity order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

}
