package com.ntuc.vendorservice.foundationcontext.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author I305675
 *
 */
public class ClassUtils {
	/**
	 * Remote fields not yet annotated
	 * @param declaredFields
	 * @param annotation
	 * @return
	 */
	public static ArrayList<Field> declaredFieldsWithAnnotation(Field[] declaredFields,Class<? extends Annotation>  annotation) {
		ArrayList<Field> arrayList = new ArrayList<Field>();
		if(annotation==null){
			 throw new RuntimeException("Annotation class cannot be null");
		} 
		for (Field field : declaredFields) {
			if (field.getAnnotation(annotation) != null) {
				arrayList.add(field);
			}
		} 
		return arrayList;
	}
	/**
	 * Get object From Request parameters
	 * 
	 * 
	 * @param request
	 * @param clazz
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <E> E getObjectFromRequestParamater(HttpServletRequest request, Class<E> clazz){  
		try {
			E newInstance = clazz.newInstance(); 
			for(Field declaredField:clazz.getDeclaredFields()){
				declaredField.setAccessible(true);
				String parameterName = declaredField.getName();
				if(parameterName.equalsIgnoreCase("serialVersionUID")){
					continue;
				}
				 
				Object parameterValue = getParameterValue(request, parameterName, declaredField);
				declaredField.set(newInstance, parameterValue);
			}
			return newInstance; 
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} 
	}
	/**
	 * Get Parameter 
	 * 
	 * @param request
	 * @param parameterName
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <E> E getParameterValue(HttpServletRequest request, String parameterName,Field declaredField){ 
		Enumeration<String> parameterNames = request.getParameterNames(); 
		Class<?> class1 = declaredField.getType();
		while(parameterNames.hasMoreElements()){
			String parameter = parameterNames.nextElement();
			if(parameterName.equalsIgnoreCase(parameter)){
				String parameter2 = request.getParameter(parameter);
				if(class1.isEnum()){
					return (E) Enum.valueOf((Class<Enum>)class1, parameter2);
				}
				else if(class1==List.class){ 
					//Handle only strings
					return (E) retriveListItem(parameter2,String.class);
				}
				else{
				   return ((E)parameter2); 
				}
			}
		}
		return null;
	}
	/**
	 * 
	 * @param value
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <E> List<E>retriveListItem(String value,Class<E> e ){
		List<E> es=new ArrayList<E>();
		if(e == String.class){
			String[] split = value.split(",");
			for(String str:split){
				es.add((E)str.trim());
			}
		}
		return es;
	}
}
