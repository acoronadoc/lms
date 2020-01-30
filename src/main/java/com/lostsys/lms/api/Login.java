package com.lostsys.lms.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostsys.lms.repository.UserRepository;
import com.lostsys.lms.service.LmsService;
import com.lostsys.lms.utils.CommUtils;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RestController
public class Login {
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private LmsService lmsService;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(
		    value = "/api/login", 
		    method = RequestMethod.POST)	
	public HashMap<String, Object> login( @RequestBody Map<String, Object> payload ) {
		CommUtils r=new CommUtils();
		
		if ( !lmsService.checkLogin( payload ) ) {
			r.appendHtmlPart(".msgs","<div class='error-msg'>Error al hacer login.</div>");
			
			return r.toHashMap();
			}
		
		lmsService.initUser(
				payload.get("mail").toString(), 
				payload.get("name").toString(), 
				payload.get("img").toString()
				);

		Context ctx = new Context();
		ctx.setVariable("name", payload.get("name") );
		ctx.setVariable("img", payload.get("img") );

		r.appendScriptPart( "document.querySelector('#login-box').setAttribute( 'style', 'display: none;' );" );
		r.appendHtmlPart("#wrapper", templateEngine.process("main-intranet", ctx) );
		r.appendHtmlPart("#main-content", Content.getMyCourses(userRepository, templateEngine, payload) );
		
		try {
			r.appendScriptPart( "logininfo = "+new ObjectMapper().writeValueAsString(payload)+";" );
		} catch (Exception ex) { ex.printStackTrace(); }
		
		return r.toHashMap();
		}

	@RequestMapping(
		    value = "/api/logout", 
		    method = RequestMethod.POST)	
	public HashMap<String, Object> logout() {
		CommUtils r=new CommUtils();
		
		r.appendScriptPart( "document.querySelector('#login-box').setAttribute( 'style', '' );" );
		r.appendScriptPart( "logininfo = null;" );
		r.appendHtmlPart("#wrapper", "" );
		
		return r.toHashMap();
		}
	

	
	}
