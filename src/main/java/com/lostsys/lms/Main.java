package com.lostsys.lms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SpringBootApplication
public class Main {
	@Value("${google.signin.client}")
	private String googleSiginClient;

	@Value("${lms.dev.mode}")
	private int devMode;
	
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
		}

	@RequestMapping(value="/")
	@ResponseBody
	public ModelAndView home() {
		ModelAndView mav=new ModelAndView("index");
		
		mav.addObject("googleSiginClient", googleSiginClient);
		mav.addObject("devMode", devMode);
		
		return mav;
		}		
}