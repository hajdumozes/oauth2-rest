package hu.areus.oauth2.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.endpoint.DefaultPasswordTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2PasswordGrantRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
public class Oauth2RestConfig {

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2PasswordGrantRequest> customAccessTokenResponseClient() {
        return new DefaultPasswordTokenResponseClient();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> userService() {
        return new DefaultOAuth2UserService();
    }
}
