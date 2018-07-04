package com.cs673.teamA.Iteration2;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	public List<User> findByUsernameContaining(String username);
}
