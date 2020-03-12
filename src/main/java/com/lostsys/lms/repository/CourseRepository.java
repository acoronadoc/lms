package com.lostsys.lms.repository;

import com.lostsys.lms.model.Course;
import com.lostsys.lms.model.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course,Integer> {

	
	}
