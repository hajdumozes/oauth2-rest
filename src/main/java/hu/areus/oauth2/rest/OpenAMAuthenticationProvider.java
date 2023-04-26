package hu.areus.oauth2.rest;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom authentication provider to meet EBH requirements as the REST API must use
 * basic authentication but during the authentication flow the application
 * must call OPENAM access_token URI to get access token. 
 * 
 * @author gabor.horvath
 *
 */
@Slf4j
@Component
public class OpenAMAuthenticationProvider implements AuthenticationProvider 
{
	private static final String CLIENT_REGISTRATION_ID = "openam";

	private final OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> accessTokenResponseClient;    
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;    
    private final ClientRegistrationRepository clientRegistrationRepository;
    
    private GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    public OpenAMAuthenticationProvider(OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> accessTokenResponseClient,
            							OAuth2UserService<OAuth2UserRequest, OAuth2User> userService,
            							ClientRegistrationRepository clientRegistrationRepository) {
        super();
        this.accessTokenResponseClient = accessTokenResponseClient;
        this.userService = userService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }
    
    /**
     * Custom authentication.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException 
    {
    	if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            return null;
        }

        // -- during basic auth we have username and password
        final UsernamePasswordAuthenticationToken usernamePassword = (UsernamePasswordAuthenticationToken) authentication;

        final String username = (String) usernamePassword.getPrincipal();
        final String password = (String) usernamePassword.getCredentials();
        
        final ClientRegistration openam = clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID);

        try
        {
	        // -- get access token from authorization server. OAuth2PasswordGrantRequest is deprecated but EBH uses it.
	        final OAuth2PasswordGrantRequest request = new OAuth2PasswordGrantRequest(openam, username, password);
	        final OAuth2AccessTokenResponse accessTokenResponse = accessTokenResponseClient.getTokenResponse(request);
	        final OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(openam, accessTokenResponse.getAccessToken(), accessTokenResponse.getAdditionalParameters()));
	        final Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper.mapAuthorities(oauth2User.getAuthorities());
	        final OAuth2AuthenticationToken authenticationResult = new OAuth2AuthenticationToken(oauth2User, mappedAuthorities, CLIENT_REGISTRATION_ID);
	        
	        return authenticationResult;
        }
        catch (Exception e)
        {
        	System.out.println("ERROR: " + e.getMessage());
        	throw e;
        }
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
