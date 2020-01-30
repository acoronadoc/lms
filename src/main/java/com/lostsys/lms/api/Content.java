package com.lostsys.lms.api;

import com.lostsys.lms.model.Course;
import com.lostsys.lms.model.User;
import com.lostsys.lms.repository.CourseRepository;
import com.lostsys.lms.repository.UserRepository;
import com.lostsys.lms.service.LmsService;
import com.lostsys.lms.utils.CommUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RestController
public class Content {
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private LmsService lmsService;
	
	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(
		    value = "/api/content", 
		    method = RequestMethod.POST)	
	public HashMap<String, Object> content( @RequestBody Map<String, Object> payload ) {
		CommUtils r=new CommUtils();
		
		if ( !lmsService.checkLogin( (Map<String, Object>) payload.get("logininfo") ) ) return r.toHashMap();
		
		if ( payload.get("content").equals("coursesmarketplace") ) r.appendHtmlPart("#main-content", getCourses(payload) );
		else if ( payload.get("content").equals("mycourses") ) r.appendHtmlPart("#main-content", getMyCourses(userRepository, templateEngine, payload) );
		else if ( payload.get("content").equals("showcourse") ) r.appendHtmlPart("#main-content", getShowCourse(payload) );
		
		return r.toHashMap();
		}

	public String getShowCourse( Map<String, Object> payload ) {
		String mail=""+((Map<String, Object>) payload.get("logininfo")).get("mail");
		Map<String, Object> other=(Map<String, Object>) payload.get("other");
		
		Context ctx = new Context();
		ctx.setVariable("okmsg", "" );
		ctx.setVariable("errormsg", "" );
		
		User u=null;
		
		if ( other.get("unsubscribe")!=null ) {
			u=userRepository.getUserByMail(mail);

			for (Iterator<Course> i=u.getCourse().iterator(); i.hasNext(); ) {
				Course c=i.next();
				
				if ( c.getId() == Integer.parseInt( ""+other.get("unsubscribe") ) ) {
					u.getCourse().remove( c );
					
					u=userRepository.save( u );
					
					ctx.setVariable("okmsg", "Te has desubscrito del curso '"+c.getTitle()+"'." );
					
					break;
					}
					
				}
			
			}
		
		if ( u==null ) u=userRepository.getUserByMail(mail);
		Course c=courseRepository.findById( Integer.parseInt( ""+other.get("id") ) ).get();
		
		ctx.setVariable("c", c );
		ctx.setVariable("isSubscribed", false );
		if ( u.getCourse().contains( c ) ) ctx.setVariable("isSubscribed", true );
		
		return templateEngine.process("showcourse", ctx);
		}
	
	public static String getMyCourses( UserRepository userRepository, TemplateEngine templateEngine, Map<String, Object> payload ) {
		String mail="";
		
		if ( payload.get("mail")!=null ) mail=""+payload.get("mail");
		else mail=""+((Map<String, Object>) payload.get("logininfo")).get("mail");
		
		User u=userRepository.getUserByMail(mail);
		
		Context ctx = new Context();
		ctx.setVariable("courses", u.getCourse() );
		ctx.setVariable("okmsg", "" );
		ctx.setVariable("errormsg", "" );
		
		return templateEngine.process("mycourses", ctx);		
		}
	
	public String getCourses( Map<String, Object> payload ) {
		String mail=""+((Map<String, Object>) payload.get("logininfo")).get("mail");
		
		Context ctx = new Context();
		ctx.setVariable("courses", courseRepository.findAll() );
		ctx.setVariable("okmsg", "" );
		ctx.setVariable("errormsg", "" );
		
		User u=null;
		
		if ( payload.get("other")!=null ) {

			Map<String, Object> other=(Map<String, Object>) payload.get("other");
			
			Course c=courseRepository.findById( Integer.parseInt( ""+other.get("subscribe") ) ).get();
			u=userRepository.getUserByMail(mail);
			
			if ( u.getCourse().size()>=2 ) {
				ctx.setVariable("errormsg", "No mas de dos subscripciones por usuario." );
			} else if ( !u.getCourse().contains(c) ) {
				u.getCourse().add( c );
				
				u=userRepository.save( u );
				
				ctx.setVariable("okmsg", "Te has subscrito correctamente a '"+c.getTitle()+"'." );
				} 
			
			}

		if ( u==null ) u=userRepository.getUserByMail(mail);
		
		ArrayList<Integer> couresList=new ArrayList<Integer>();
		for (Iterator<Course> i=u.getCourse().iterator(); i.hasNext(); )
			couresList.add( i.next().getId() );		

		ctx.setVariable("couresList", couresList );

		return templateEngine.process("coursesmarketplace", ctx);		
		}
}
