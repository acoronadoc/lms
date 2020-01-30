package com.lostsys.lms.service;

import com.lostsys.lms.model.User;
import com.lostsys.lms.repository.UserRepository;

import java.util.Collections;
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
	@Value("${google.signin.client}")
	private String googleSiginClient;

	@Value("${lms.dev.mode}")
	private int devMode;
	
	@Autowired
	private UserRepository userRepository;

	public User initUser(String mail, String name, String img) {
		
		User u=userRepository.getUserByMail( mail );
		if ( u==null ) u=new User();
		
		u.setMail(mail);
		u.setName(name);
		u.setImg(img);
		
		u=userRepository.save(u);
		
		return u;
		}
	
	public boolean checkLogin( Map<String, Object> payload ) {
		
		/* Dev login */
		if ( devMode==1 && payload.get("mail").equals("developer@dev") ) 
			return true;

		/* Google login */
		if ( payload.containsKey("idtoken") ) {
			RestTemplate restTemplate = new RestTemplate();
			
		   	HttpHeaders headers = new HttpHeaders();
	    	headers.setContentType(MediaType.APPLICATION_JSON);
	    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));    
	    	
	    	Map<String, Object> reqBody = new HashMap<>();
	    	HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reqBody, headers);    
	    	Map<String, String> urlParams = new HashMap<>();
	    	
	    	ResponseEntity<Map> response = restTemplate.exchange(
	    			"https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="+payload.get("idtoken"), 
	    			HttpMethod.GET,
	    			entity,
	    			java.util.Map.class,
	    			urlParams
	    			);		
	    	
	    	if ( response.getBody().containsKey("aud") )
	    		if ( response.getBody().get("aud").equals( googleSiginClient ) )
	    			return true;
			}
		
		return false;
		}	
	
}
