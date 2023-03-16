package hexlet.code.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthorizationUtils {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String BEARER = "Bearer ";

    private String getToken(String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = jwtUtil.generateToken(userDetails);
        return BEARER + token;
    }

    public MockHttpServletRequestBuilder getAuthRequest(MockHttpServletRequestBuilder request, String email) {
        final String token = getToken(email);
        request.header(AUTHORIZATION, token);
        return request;
    }
}
