package com.gcu.topic03.business;

import org.springframework.stereotype.Service;

@Service
public class SecurityBusinessService {

	public boolean authenticate(String username, String password) {
		System.out.println("SecurityBusinessService.authenticate(" + username + ", " + password + ")");
		return true;
	}
}
