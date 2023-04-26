package hu.areus.oauth2.rest;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig 
{
	@Autowired
	private OpenAMAuthenticationProvider openAMAuthenticationProvider;
	
	@Value("${development.auth.username:weblogic}")
	private String authUserName;
	
	@Value("${development.auth.password:weblogic0}")
	private String authPassword;
	
	@Value("${auth.allowedGroup:}")
	private String allowedGroup;
	
	@Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception 
	{
        http
        	.cors().configurationSource(corsConfigurationSource())
        	.and().csrf().disable()
            .authorizeHttpRequests().anyRequest().authenticated()
            .and().httpBasic()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authenticationProvider(openAMAuthenticationProvider);
        
        return http.build();
    }
	
	/**
	 * CORS configuration.
	 * Specify origins here.
	 * @return
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource()
	{
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("*"));
		
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	/**
     * Configuring basic authentication.
     * @param auth
     * @throws Exception
     */
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception 
//    {
//    	log.warn("DEVELOPMENT MODE: enabling BASIC auth with {}/{}!", authUserName, authPassword);
//	    auth
//	    	.inMemoryAuthentication()
//	        .withUser(authUserName)
//	        .password(passwordEncoder.encode(authPassword))
//	        .authorities(allowedGroup)
//	        ;
//    }
}