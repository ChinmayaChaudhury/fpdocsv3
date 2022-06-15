package com.ntuc.vendorservice.foundationcontext.listeners;

import java.util.Enumeration;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Lifecycle Listener implementation class SessionListener
 *
 */
@WebListener
public class SessionListener implements ServletRequestListener, HttpSessionListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);
    /**
     * Default constructor. 
     */
    public SessionListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent httpSessionEvent)  {  
    	HttpSession session = httpSessionEvent.getSession();
    	if(session!=null){
	    	Enumeration<String> attributeNames = session.getAttributeNames();
	    	while(attributeNames.hasMoreElements()){ 
	    		LOGGER.error("<<Element>>: "+attributeNames.nextElement()); 
	    	}
			/*Subject subject = (Subject)session.getAttribute("javax.security.auth.subject"); 
	    	Iterator<Principal> iterator = subject.getPrincipals().iterator();
	    	while(iterator.hasNext()){
	    		String name = iterator.next().getName();
	    		LOGGER.error("Session Created for: "+name); 
	    	}*/
    	}
    	LOGGER.info("<Session>Created!!!!! "); 
    	/*User idmUser=null;
    	if(subject!=null){
    		UserProvider provider = UserManagementAccessor.getUserProvider();
            idmUser = provider.getUser(principal.getName()); 
    	}*/
    	
    }

	/**
     * @see ServletRequestListener#requestDestroyed(ServletRequestEvent)
     */
    public void requestDestroyed(ServletRequestEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletRequestListener#requestInitialized(ServletRequestEvent)
     */
    public void requestInitialized(ServletRequestEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent arg0)  { 
         // TODO Auto-generated method stub
    }
	
}
