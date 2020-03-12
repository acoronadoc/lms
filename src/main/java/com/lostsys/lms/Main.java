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

	@Value("${google.analytics.id}")
	private String idAnalytics;

	@Value("${lms.dev.mode}")
	private int devMode;
	
	@Value("${app.title}")
	private String appTitle;
	
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
		}

	@RequestMapping(value="/")
	@ResponseBody
	public ModelAndView home() {
		ModelAndView mav=new ModelAndView("index");
		
		mav.addObject("appTitle", appTitle);
		mav.addObject("googleSiginClient", googleSiginClient);
		mav.addObject("devMode", devMode);
		mav.addObject("idAnalytics", idAnalytics);
		
		return mav;
		}		
}
