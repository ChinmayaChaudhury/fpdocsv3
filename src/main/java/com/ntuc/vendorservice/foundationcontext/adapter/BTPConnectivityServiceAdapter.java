package com.ntuc.vendorservice.foundationcontext.adapter;

import com.ntuc.vendorservice.emailingservice.services.EmailerService;
import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.xsuaa.client.*;
import com.sap.cloud.security.xsuaa.tokenflows.TokenFlowException;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;
import com.sap.xsa.security.container.XSUserInfoException; 
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.ntuc.vendorservice.foundationcontext.utils.AdapterUtils;
/**
 *
 */
public class BTPConnectivityServiceAdapter {
    final static Logger LOGGER = LoggerFactory.getLogger(EmailerService.class);
    static final String OAUTH_TOKEN_SERVICE_URL = "token_service_url";
    ///XXX subject to review
    static final String OAUTH_CERT_SERVICE_URL = "cert_service_url";
    static final String OAUTH_CLIENT_ID = "clientid";
    static final String OAUTH_CLIENT_SECRET = "clientsecret";

     public  interface SocketDetailCompletedInterface {
         void  resolveOnPremiseDetail(String onPremiseProxyHost,String onPremiseProxyPort, String onPremiseSocks5ProxyPort, String onPremiseProxyHttpPort);
     }



   
    /**
     * Get AccessToken
     * @return
     * @throws XSUserInfoException
     */
    public static String getConnectivityServiceAccessToken(final SocketDetailCompletedInterface socketDetailCompletedInterface) throws XSUserInfoException {

        // Obtain the connection destination details
        final JSONObject connectivityAuthServiceCredentials = AdapterUtils.getConnectivityAuthServiceCredentials();
        final String baseURI = connectivityAuthServiceCredentials.getString(OAUTH_TOKEN_SERVICE_URL);
        //final String certURI =   connectivityAuthServiceCredentials.getString(OAUTH_CERT_SERVICE_URL);
        final String clientID =  connectivityAuthServiceCredentials.getString(OAUTH_CLIENT_ID);
        final String clientSecret =   connectivityAuthServiceCredentials.getString(OAUTH_CLIENT_SECRET);
        final OAuth2ServiceEndpointsProvider xsuaaDefaultEndpoints = new XsuaaDefaultEndpoints(baseURI, null);
        final ClientIdentity clientCredentials = new ClientCredentials(clientID, clientSecret);
        //XSUAA Token Service (XsuaaOAuth2TokenService) use spring, whereas default not.
        final OAuth2TokenService defaultOAuth2TokenService = new DefaultOAuth2TokenService(HttpClientFactory.create(clientCredentials));
        final XsuaaTokenFlows tokenFlows = new XsuaaTokenFlows(defaultOAuth2TokenService, xsuaaDefaultEndpoints, clientCredentials);
        final OAuth2TokenResponse serviceTokenResponse;
        try {
            serviceTokenResponse = tokenFlows.clientCredentialsTokenFlow().execute();
        } catch (TokenFlowException e) {
            LOGGER.error("Error performing Client Credentials Flow", e);
            throw new XSUserInfoException("Error performing Client Credentials Flow.", e);
        }
        //
        final String onPremiseProxyHost= connectivityAuthServiceCredentials.getString("onpremise_proxy_host");
        final String onPremiseProxyPort =  connectivityAuthServiceCredentials.getString("onpremise_proxy_port");
        final String onPremiseSocks5ProxyPort= connectivityAuthServiceCredentials.getString("onpremise_socks5_proxy_port");
        final String onPremiseProxyHttpPort= connectivityAuthServiceCredentials.getString("onpremise_proxy_http_port");
        socketDetailCompletedInterface.resolveOnPremiseDetail(onPremiseProxyHost, onPremiseProxyPort,onPremiseSocks5ProxyPort,onPremiseProxyHttpPort);
        //
        final String accessToken = serviceTokenResponse.getAccessToken();
        return accessToken;
    }
}
