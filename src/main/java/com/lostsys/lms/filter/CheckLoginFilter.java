package com.lostsys.lms.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostsys.lms.utils.CommUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(1)
public class CheckLoginFilter implements Filter {
	
	@Value("${google.signin.client}")
	private String googleSiginClient;

	@Value("${lms.dev.mode}")
	private int devMode;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		if ( req.getRequestURI().startsWith("/api") ) {
			if ( !checkLogin( req.getHeader("Authorization-Type"), req.getHeader("Authorization")) ) {				
				response.getOutputStream().write( "{ \"parts\": [ { \"action\": \"html\", \"selector\": \".msgs\", \"content\": \"<div class='error-msg'>Error al hacer login.</div>\" } ] }".getBytes() );
				
				return;
				}
			}
		
		chain.doFilter(request, response);
		}	
	
	public boolean checkLogin( String authType, String auth ) {
		
		if ( authType==null || auth==null ) return false;
		
		/* Dev login */
		if ( devMode==1 && auth.equals("developer@dev") ) 
			return true;

		/* Google login */
		if ( authType.contentEquals("google") ) {
			RestTemplate restTemplate = new RestTemplate();
			
		   	HttpHeaders headers = new HttpHeaders();
	    	headers.setContentType(MediaType.APPLICATION_JSON);
	    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));    
	    	
	    	Map<String, Object> reqBody = new HashMap<>();
	    	HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reqBody, headers);    
	    	Map<String, String> urlParams = new HashMap<>();
	    	
	    	ResponseEntity<Map> response = restTemplate.exchange(
	    			"https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="+auth, 
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
