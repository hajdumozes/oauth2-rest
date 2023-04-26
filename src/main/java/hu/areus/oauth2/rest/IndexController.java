package hu.areus.oauth2.rest;

import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IndexController 
{

	@CrossOrigin
    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello(OAuth2AuthenticationToken auth) {
        return auth.getName() + " says hello";
    }
    
	@CrossOrigin
    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test() 
    {
        return "TEST";
    }

}
