package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dtos.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repositories.LabelRepository;
import hexlet.code.repositories.UserRepository;
import hexlet.code.utils.AuthorizationUtils;
import hexlet.code.utils.UserUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static hexlet.code.controller.UserControllerIT.DEFAULT_USER_EMAIL;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerIT {

    @Autowired
    private AuthorizationUtils authUtils;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void before() throws Exception {
        userUtils.addDefaultUser();
    }

    @AfterEach
    public void tearDown() {
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private static final String LABEL_CONTROLLER_PATH =  "/api/labels";

    private static final String LABEL_CONTROLLER_PATH_ID =  "/api/labels/{id}";

    public static final String DEFAULT_LABEL_NAME = "Bug";

    @Test
    public void createLabel() throws Exception {

        assertEquals(0, labelRepository.count());

        addDefaultLabel();

        assertEquals(1, labelRepository.count());
    }

    @Test
    public void getLabelById() throws Exception {

        final Label defaultLabel = addDefaultLabel();
        final Long defaultLabelId = defaultLabel.getId();

        final MockHttpServletRequestBuilder request = authUtils.getAuthRequest(
                get(LABEL_CONTROLLER_PATH_ID, defaultLabelId),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(defaultLabel.getId(), label.getId());
        assertEquals(defaultLabel.getName(), label.getName());
    }

    @Test
    public void getAllLabels() throws Exception {
        addDefaultLabel();

        final MockHttpServletResponse response = mockMvc.perform(get(LABEL_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertNotNull(labels);
        assertEquals(labels.size(), 1);
    }

    @Test
    public void updateLabel() throws Exception {
        final Label defaultLabel = addDefaultLabel();
        final Long defaultLabelId = defaultLabel.getId();

        final String newLabelName = "Feature";
        final LabelDto labelDto = new LabelDto(newLabelName);

        final MockHttpServletRequestBuilder updateRequest = authUtils.getAuthRequest(
                put(LABEL_CONTROLLER_PATH_ID, defaultLabelId)
                        .content(asJson(labelDto))
                        .contentType(APPLICATION_JSON),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertTrue(labelRepository.existsById(defaultLabelId));
        assertNull(labelRepository.findByName(DEFAULT_LABEL_NAME).orElse(null));
        assertNotNull(labelRepository.findByName(newLabelName).orElse(null));

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertNotNull(label);
        assertEquals(defaultLabelId, label.getId());
        assertEquals(newLabelName, label.getName());
    }

    @Test
    public void deleteLabel() throws Exception {
        final Label label = addDefaultLabel();

        final MockHttpServletRequestBuilder deleteRequest = authUtils.getAuthRequest(
                delete(LABEL_CONTROLLER_PATH_ID, label.getId()),
                DEFAULT_USER_EMAIL
        );

        mockMvc.perform(deleteRequest).andExpect(status().isOk());

        assertEquals(0, labelRepository.count());
    }

    private Label addDefaultLabel() throws Exception {
        return addLabel(DEFAULT_LABEL_NAME);
    }

    private Label addLabel(String labelName) throws Exception {
        final LabelDto labelDto = new LabelDto(labelName);

        final MockHttpServletRequestBuilder request = authUtils.getAuthRequest(
                post(LABEL_CONTROLLER_PATH)
                        .content(asJson(labelDto))
                        .contentType(APPLICATION_JSON),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() { });
        return label;
    }
}


