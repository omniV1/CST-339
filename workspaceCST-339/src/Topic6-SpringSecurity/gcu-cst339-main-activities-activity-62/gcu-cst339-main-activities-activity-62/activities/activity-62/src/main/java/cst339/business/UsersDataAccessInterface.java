package cst339.business;

public interface UsersDataAccessInterface <T> {

	public T findByUsername(String username);
}
