package com.lostsys.lms.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@RestController
public class Login {
	@Value("${google.signin.client}")
	private String googleSiginClient;

	@Value("${lms.dev.mode}")
	private int devMode;
	
	@Autowired
	private TemplateEngine templateEngine;

	@RequestMapping(
		    value = "/api/login", 
		    method = RequestMethod.POST)	
	public HashMap<String, Object> login( @RequestBody Map<String, Object> payload ) {
		CommUtils r=new CommUtils();
		
		if ( !checkLogin( payload ) ) {
			r.appendHtmlPart(".msgs","<div class='error-msg'>Error al hacer login.</div>");
			
			return r.toHashMap();
			}
		
		/* TO DO: generar registro de usuario */

		Context ctx = new Context();
		ctx.setVariable("name", payload.get("name") );
		ctx.setVariable("img", payload.get("img") );

		r.appendScriptPart( "document.querySelector('#login-box').setAttribute( 'style', 'display: none;' );" );
		r.appendHtmlPart("#wrapper", templateEngine.process("main-intranet", ctx) );
		
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
	
	private boolean checkLogin( Map<String, Object> payload ) {
		
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
		
		/* TO DO: validar login google */
		
		return false;
		}
	
	}
