package com.lostsys.lms.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommUtils {
	private ArrayList<HashMap<String, Object>> parts=new ArrayList<HashMap<String, Object>>();
	
	public void appendHtmlPart(String selector, String content) {
		HashMap<String, Object> part=new HashMap<String, Object>();
		part.put("action", "html");
		part.put("selector", selector);
		part.put("content", content);
		
		parts.add( part );
		}

	public void appendScriptPart(String content) {
		HashMap<String, Object> part=new HashMap<String, Object>();
		part.put("action", "script");
		part.put("content", content);
		
		parts.add( part );
		}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> r=new HashMap<String, Object>();		
		
		r.put("parts", parts);
		
		return r;
		}

	}
