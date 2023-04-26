package hu.areus.oauth2.rest.auth;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenAMAuthenticationProvider implements AuthenticationProvider {
    private static final String CLIENT_REGISTRATION_ID = "openam";

    OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> accessTokenResponseClient;
    OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;
    ClientRegistrationRepository clientRegistrationRepository;
    GrantedAuthoritiesMapper authoritiesMapper = (authorities -> authorities);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken usernamePassword)) {
            return null;
        }

        String username = usernamePassword.getPrincipal().toString();
        String password = usernamePassword.getCredentials().toString();

        ClientRegistration openam = clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID);

        // -- get access token from authorization server. OAuth2PasswordGrantRequest is deprecated but EBH uses it.
        OAuth2PasswordGrantRequest request = new OAuth2PasswordGrantRequest(openam, username, password);
        OAuth2AccessTokenResponse accessTokenResponse = accessTokenResponseClient.getTokenResponse(request);
        OAuth2User oauth2User = userService.loadUser(new OAuth2UserRequest(openam, accessTokenResponse.getAccessToken(), accessTokenResponse.getAdditionalParameters()));
        Collection<? extends GrantedAuthority> mappedAuthorities = authoritiesMapper.mapAuthorities(oauth2User.getAuthorities());
        return new OAuth2AuthenticationToken(oauth2User, mappedAuthorities, CLIENT_REGISTRATION_ID);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
