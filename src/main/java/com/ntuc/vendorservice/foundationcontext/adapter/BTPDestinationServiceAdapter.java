package com.ntuc.vendorservice.foundationcontext.adapter;

import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

public class BTPDestinationServiceAdapter {


	public enum DestinationReference {
		UME_DEST,SCIM_ATTR_DEST,SCIM_DEST
	}
	private HttpDestination umeDestination;
	private HttpDestination attrDestination;
	private HttpDestination scimDestination;
	
	///XXX Review redundancy

	@Deprecated
	public HttpDestination getUmeDestinationHttpClient(HttpServletRequest request) throws NamingException, DestinationAccessException {
		if(umeDestination==null){
		    final String destinationName = request.getParameter("epm_api_destination")==null?"umeservice":request.getParameter("epm_api_destination");
			umeDestination = DestinationAccessor.getDestination(destinationName).asHttp();
		}
		return umeDestination;
	 }
	///XXX Review redundancy
	public HttpDestination getSCIMAttributeDestinationHttpClient(HttpServletRequest request) throws NamingException {
		 if(attrDestination==null){
			 final String destinationName = request.getParameter("scim_attribute_destination")==null?"scimservice-attributes":request.getParameter("epm_api_destination");
			 attrDestination = DestinationAccessor.getDestination(destinationName).asHttp();
		 }
		 return attrDestination;
	 }
	///XXX Review redundancy
	public HttpDestination getSCIMDestinationHttpClient(HttpServletRequest request) throws NamingException, DestinationAccessException {
		if(scimDestination==null){ 
			final String destinationName = request.getParameter("scim_api_destination")==null?"scimservice":request.getParameter("scimservice");
			scimDestination = DestinationAccessor.getDestination(destinationName).asHttp();
		}
		return scimDestination;
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	public DestinationProperties getMailDestinationProperties(final HttpServletRequest request){
	 	final String emailDestinationName = request.getSession().getServletContext().getInitParameter("MailDestinationName");
		final String destinationName = emailDestinationName == null ? "Smtp_Destination" : emailDestinationName;
		final DestinationProperties emailDestination  = DestinationAccessor.getDestination(destinationName);
		return emailDestination;
	}

}
