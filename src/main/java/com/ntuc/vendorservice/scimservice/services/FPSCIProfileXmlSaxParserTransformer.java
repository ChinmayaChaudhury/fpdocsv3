package com.ntuc.vendorservice.scimservice.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ntuc.vendorservice.scimservice.models.FPSCIUserAccount;

/**
 * 
 * @author I305675
 *
 */
public class FPSCIProfileXmlSaxParserTransformer {
	private List<FPSCIUserAccount> parsedAccounts;

	public enum UserMetaDataProperty {
		USER("user"), USER_PROFILE("user_profile_id"), ORG_WIDE("organization_wide_id"), NAME_ID("name_id"),UID("uid"), 
		STATUS("status"), EMAIL("email"), FIRST_NAME("first_name"), LAST_NAME("last_name"),
		ORGANIZATION_USER_TYPE("organization_user_type"),
		LOGIN_NAME("login_name"),
		CITY("city"), 
		USER_PROVISIONING_ROLE("user_provisioning_role"), 
		LANGUAGE("language"), 
		COUNTRY("country"), 
		SP_CUST_ATTR1("spCustomAttribute1"),
		SP_CUST_ATTR2("spCustomAttribute2"),
		SP_CUST_ATTR3("spCustomAttribute3"),
		SP_CUST_ATTR4("spCustomAttribute4"),
		SP_CUST_ATTR5("spCustomAttribute5"),
		NONE("none");
		String property;

		UserMetaDataProperty(String property) {
			this.property = property;
		}

		public String getProperty() {
			return property;
		}
		public void setProperty(String property) {
			this.property = property;
		}

		public static UserMetaDataProperty fromValue(String value) {
			NONE.setProperty(NONE.name().toLowerCase());
			for (UserMetaDataProperty type : UserMetaDataProperty.values()) {
				if (type.property.equalsIgnoreCase(value)) {
					return type;
				}
				continue;
			}
			NONE.setProperty(value);
			return NONE;
		}
	}

	/**
	 * 
	 * @param inputStream
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * 
	 */
	public List<FPSCIUserAccount> parseXmlResponse(InputStream inputStream) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			UserProfileDetailHandler userhandler = new UserProfileDetailHandler();
			saxParser.parse(inputStream, userhandler);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return getParsedAccounts();
	}

	/**
	 * @return the parsedAccounts
	 */
	public List<FPSCIUserAccount> getParsedAccounts() {
		if (parsedAccounts == null) {
			parsedAccounts = new ArrayList<FPSCIUserAccount>();
		}
		return parsedAccounts;
	}

	class UserProfileDetailHandler extends DefaultHandler {
		private FPSCIUserAccount userAccount;
		private UserMetaDataProperty currentUserProperty;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			UserMetaDataProperty userProperty = UserMetaDataProperty.fromValue(qName);
			if(userProperty== UserMetaDataProperty.USER){
				userAccount=new FPSCIUserAccount();
			}
			this.currentUserProperty = userProperty;
		}
		public UserMetaDataProperty getCurrentUserProperty() {
			return currentUserProperty;
		}
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (UserMetaDataProperty.fromValue(qName) == UserMetaDataProperty.USER) {
				getParsedAccounts().add(userAccount);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException { 
			String valueOf = String.valueOf(ch, start, length);
			UserMetaDataProperty currentUserProperty2 = getCurrentUserProperty();
			switch (currentUserProperty2) {
			case EMAIL:
				userAccount.setEmail(valueOf);
				break;
			case FIRST_NAME:
				userAccount.setFirstName(valueOf);
				break;
			case LAST_NAME:
				userAccount.setLastName(valueOf);
				break;
			case NAME_ID:
				userAccount.setUserID(valueOf);
				break;
			case ORG_WIDE:
				userAccount.setOrganisationWideID(valueOf);
				break;
			case STATUS:
				userAccount.setStatus(valueOf);
				break; 
			case USER_PROFILE:
				userAccount.setProfileID(valueOf);
				break;
			case LOGIN_NAME:
				userAccount.setLoginName(valueOf);
				break;
			case ORGANIZATION_USER_TYPE:
				userAccount.setUserType(valueOf);
				break;
			case SP_CUST_ATTR1:
				userAccount.setSpCustomAttribute1(valueOf);
				break;
			case  SP_CUST_ATTR2:
				userAccount.setSpCustomAttribute2(valueOf);
				break;
			case  SP_CUST_ATTR3:
				userAccount.setSpCustomAttribute3(valueOf);
				break;	
			case  SP_CUST_ATTR4:
				userAccount.setSpCustomAttribute4(valueOf);
				break;
			case  SP_CUST_ATTR5:
				userAccount.setSpCustomAttribute5(valueOf);
				break;
			case USER_PROVISIONING_ROLE:
				userAccount.setUserProvisioningRole(valueOf);
				break; 
			case LANGUAGE:
				userAccount.setLanguage(valueOf);
				break;
			case COUNTRY:
				userAccount.setCountry(valueOf);
				break; 
			case CITY:
				userAccount.setCity(valueOf);
				break;
			case NONE:
				userAccount.addProperty(currentUserProperty2.getProperty(), valueOf);
				break;
			default:
				break;
			}
			currentUserProperty= UserMetaDataProperty.NONE;
		}
	}
}