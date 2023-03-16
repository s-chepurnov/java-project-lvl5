package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dtos.UserDto;
import hexlet.code.model.User;
import hexlet.code.repositories.UserRepository;
import hexlet.code.utils.AuthorizationUtils;
import hexlet.code.utils.UserUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.JsonUtils.asJson;
import static hexlet.code.utils.JsonUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

    @Autowired
    private AuthorizationUtils authUtils;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }

    private static final String USER_CONTROLLER_PATH =  "/api/users";
    private static final String USER_CONTROLLER_PATH_ID =  "/api/users/{id}";
    public static final String DEFAULT_USER_EMAIL =  "test@gmail.com";

    @Test
    public void addUser() throws Exception {
        assertEquals(0, userRepository.count());
        userUtils.addDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void getUserById() throws Exception {
        userUtils.addDefaultUser();

        final User expectedUser = userRepository.findAll().get(0);
        final String email = expectedUser.getEmail();

        final MockHttpServletRequestBuilder request = authUtils.getAuthRequest(
                get(USER_CONTROLLER_PATH_ID, expectedUser.getId()),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Test
    public void getAllUsers() throws Exception {
        userUtils.addDefaultUser();
        final MockHttpServletResponse response = mockMvc.perform(get(USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertNotNull(users);
        assertEquals(users.size(), 1);
    }

    @Test
    public void updateUser() throws Exception {
        userUtils.addDefaultUser();

        final User defaultUser = userRepository.findByEmail(DEFAULT_USER_EMAIL).get();
        final Long userId = defaultUser.getId();

        final String updatedEmail = "test2@gmail.com";
        final UserDto userDto = userUtils.getUserDto(updatedEmail, "Test2", "Test2", "test2");

        final MockHttpServletRequestBuilder updateRequest = authUtils.getAuthRequest(
                put(USER_CONTROLLER_PATH_ID, userId)
                        .content(asJson(userDto))
                        .contentType(APPLICATION_JSON),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(DEFAULT_USER_EMAIL).orElse(null));
        assertNotNull(userRepository.findByEmail(updatedEmail).orElse(null));

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(userId, user.getId());
        assertEquals(updatedEmail, user.getEmail());
        assertEquals("Test2", user.getFirstName());
        assertEquals("Test2", user.getLastName());
    }

    @Test
    public void deleteUser() throws Exception {
        userUtils.addDefaultUser();

        final Long userId = userRepository.findByEmail(DEFAULT_USER_EMAIL).get().getId();

        final MockHttpServletRequestBuilder deleteRequest = authUtils.getAuthRequest(
                delete(USER_CONTROLLER_PATH_ID, userId),
                DEFAULT_USER_EMAIL
        );

        mockMvc.perform(deleteRequest).andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }

}
