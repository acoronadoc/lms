package com.lostsys.lms.repository;

import com.lostsys.lms.model.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {

	public User getUserByMail(String mail);
	
	}
