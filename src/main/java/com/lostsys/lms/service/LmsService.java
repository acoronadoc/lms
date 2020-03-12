package com.lostsys.lms.service;

import com.lostsys.lms.model.User;
import com.lostsys.lms.repository.UserRepository;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LmsService {
	
	@Autowired
	private UserRepository userRepository;

	public User initUser(String mail, String name, String img) {
		
		User u=userRepository.getUserByMail( mail );
		if ( u==null ) {
			u=new User();
			u.setCreated( new Date() );
			}
		
		u.setMail(mail);
		u.setName(name);
		u.setImg(img);
		u.setLastLogin( new Date() );
		
		u=userRepository.save(u);
		
		return u;
		}
	
}
