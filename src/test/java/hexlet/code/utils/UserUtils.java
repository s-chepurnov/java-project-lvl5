package hexlet.code.utils;

import hexlet.code.dtos.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static hexlet.code.controller.UserControllerIT.DEFAULT_USER_EMAIL;
import static hexlet.code.utils.JsonUtils.asJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class UserUtils {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    private static final String USER_CONTROLLER_PATH =  "/api/users";

    public ResultActions addDefaultUser() throws Exception {
        final UserDto testUser = getUserDto(DEFAULT_USER_EMAIL, "Test", "Test", "test");
        final MockHttpServletRequestBuilder request = post(USER_CONTROLLER_PATH)
                .content(asJson(testUser))
                .contentType(APPLICATION_JSON);

        return mockMvc.perform(request);
    }

    public UserDto getUserDto(String email, String firstName, String lastName, String password) {
        return new UserDto(
                email,
                firstName,
                lastName,
                passwordEncoder.encode(password)
        );
    }
}
