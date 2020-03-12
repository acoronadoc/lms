package com.lostsys.lms.api;

import com.lostsys.lms.model.Course;
import com.lostsys.lms.model.History;
import com.lostsys.lms.model.User;
import com.lostsys.lms.repository.CourseRepository;
import com.lostsys.lms.repository.HistoryRepository;
import com.lostsys.lms.repository.UserRepository;
import com.lostsys.lms.service.LmsService;
import com.lostsys.lms.utils.CommUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
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

	@Autowired
	private HistoryRepository historyRepository;
	
	@RequestMapping(
		    value = "/api/content", 
		    method = RequestMethod.POST)	
	public HashMap<String, Object> content( @RequestBody Map<String, Object> payload ) {
		CommUtils r=new CommUtils();
		
		if ( payload.get("content").equals("coursesmarketplace") ) r.appendHtmlPart("#main-content", getCourses(payload) );
		else if ( payload.get("content").equals("mycourses") ) r.appendHtmlPart("#main-content", getMyCourses(userRepository, templateEngine, payload) );
		else if ( payload.get("content").equals("showcourse") ) r.appendHtmlPart("#main-content", getShowCourse(payload) );
		else if ( payload.get("content").equals("showsyllabuspart") ) r.appendHtmlPart("#syllabus_"+((Map<String, Object>) payload.get("other")).get("part"), getSyllabusPart(payload) );
		
		return r.toHashMap();
		}

	public String getSyllabusPart( Map<String, Object> payload ) {
		String mail=""+((Map<String, Object>) payload.get("logininfo")).get("mail");
		String courseId=""+((Map<String, Object>) payload.get("other")).get("courseId");
		String part=""+((Map<String, Object>) payload.get("other")).get("part");
		
		User u=userRepository.getUserByMail(mail);
		Course course=null;
		
		for (Iterator<Course> i=u.getCourse().iterator(); i.hasNext(); ) {
			Course c=i.next();
			
			if ( c.getId()==Integer.parseInt( courseId ) ) {
				course=c;
				break;
				}
			}
		
		if ( course==null )
			return "<div class='error-msg'>Debes estar subscrito para ver el contenido del curso.</div>";
		
		
		Context ctx = new Context();
		JSONObject definition=new JSONObject( course.getDefinition() );
		
		ctx.setVariable("parts", definition.getJSONArray( "syllabus" ).getJSONObject( Integer.parseInt( part ) ).getJSONArray("parts").toList() );
		
		return templateEngine.process("showcourse-syllabus", ctx);
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

					History h=new History();
					h.setEvent("UNSUBSCRIBE");
					h.setUserId( u.getId() );
					h.setCourseId( c.getId() );
					h.setDate( new Date() );
					historyRepository.save( h );
					
					ctx.setVariable("okmsg", "Te has desubscrito del curso '"+c.getTitle()+"'." );
					
					break;
					}
					
				}
			
			
			}
		
		if ( u==null ) u=userRepository.getUserByMail(mail);
		Course c=courseRepository.findById( Integer.parseInt( ""+other.get("id") ) ).get();
		
		JSONObject definition=new JSONObject();		
		try { definition=new JSONObject( c.getDefinition() ); } catch (Exception ex) { ex.printStackTrace(); }
		
		if ( !definition.has("syllabus") ) definition.put("syllabus", new JSONArray() );
		
		ctx.setVariable("c", c );
		ctx.setVariable("isSubscribed", false );
		ctx.setVariable("syllabus", definition.getJSONArray("syllabus").toList() );
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
				ctx.setVariable("errormsg", "Tu nivel de subscripción no te permiten mas de dos subscripciones al mismo tiempo." );
			} else if ( !u.getCourse().contains(c) ) {
				u.getCourse().add( c );
				
				u=userRepository.save( u );
				
				History h=new History();
				h.setEvent("SUBSCRIBE");
				h.setUserId( u.getId() );
				h.setCourseId( c.getId() );
				h.setDate( new Date() );
				historyRepository.save( h );				
				
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
