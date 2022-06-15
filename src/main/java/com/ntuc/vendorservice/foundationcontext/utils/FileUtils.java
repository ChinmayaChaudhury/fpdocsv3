package com.ntuc.vendorservice.foundationcontext.utils;

import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class FileUtils {
	
	public static String getFileName(final Part part) { 
		String fileName="";
		if(part==null){
			return "";
		}
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	        	fileName= content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	        	break;
	        } 
	    }
	    if(StringUtils.contains(fileName, "\\")){
	    	fileName=FilenameUtils.getName(fileName);
	    }
	    return fileName;
	}  
}
