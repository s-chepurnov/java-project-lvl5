package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dtos.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repositories.TaskStatusRepository;
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
public class TaskStatusControllerIT {

    @Autowired
    private AuthorizationUtils authUtils;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

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
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    private static final String TASK_STATUS_CONTROLLER_PATH =  "/api/statuses";

    private static final String TASK_STATUS_CONTROLLER_PATH_ID =  "/api/statuses/{id}";

    public static final String DEFAULT_TASK_STATUS_NAME = "New";


    @Test
    public void createNewStatus() throws Exception {

        assertEquals(0, taskStatusRepository.count());

        addDefaultTaskStatus();

        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void getStatusById() throws Exception {

        final TaskStatus defaultTaskStatus = addDefaultTaskStatus();
        final Long defaultTaskStatusId = defaultTaskStatus.getId();

        final MockHttpServletRequestBuilder request = authUtils.getAuthRequest(
                get(TASK_STATUS_CONTROLLER_PATH_ID, defaultTaskStatusId),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(defaultTaskStatus.getId(), taskStatus.getId());
        assertEquals(defaultTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    public void getAllStatuses() throws Exception {
        addDefaultTaskStatus();

        final MockHttpServletResponse response = mockMvc.perform(get(TASK_STATUS_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertNotNull(taskStatuses);
        assertEquals(taskStatuses.size(), 1);
    }

    @Test
    public void updateTaskStatus() throws Exception {
        final TaskStatus defaultTaskStatus = addDefaultTaskStatus();
        final Long defaultTaskStatusId = defaultTaskStatus.getId();

        final String newTaskStatusName = "In progress";
        final TaskStatusDto taskStatusDto = new TaskStatusDto(newTaskStatusName);

        final MockHttpServletRequestBuilder updateRequest = authUtils.getAuthRequest(
                put(TASK_STATUS_CONTROLLER_PATH_ID, defaultTaskStatusId)
                        .content(asJson(taskStatusDto))
                        .contentType(APPLICATION_JSON),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertTrue(taskStatusRepository.existsById(defaultTaskStatusId));
        assertNull(taskStatusRepository.findByName(DEFAULT_TASK_STATUS_NAME).orElse(null));
        assertNotNull(taskStatusRepository.findByName(newTaskStatusName).orElse(null));

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertNotNull(taskStatus);
        assertEquals(defaultTaskStatusId, taskStatus.getId());
        assertEquals(newTaskStatusName, taskStatus.getName());
    }

    @Test
    public void deleteTaskStatus() throws Exception {
        final TaskStatus defaultTaskStatus = addDefaultTaskStatus();

        final MockHttpServletRequestBuilder deleteRequest = authUtils.getAuthRequest(
                delete(TASK_STATUS_CONTROLLER_PATH_ID, defaultTaskStatus.getId()),
                DEFAULT_USER_EMAIL
        );

        mockMvc.perform(deleteRequest).andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }

    private TaskStatus addDefaultTaskStatus() throws Exception {
        return addTaskStatus(DEFAULT_TASK_STATUS_NAME);
    }

    private TaskStatus addTaskStatus(String statusName) throws Exception {

        final TaskStatusDto taskStatusDto = new TaskStatusDto(statusName);

        final MockHttpServletRequestBuilder request = authUtils.getAuthRequest(
                post(TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(taskStatusDto))
                        .contentType(APPLICATION_JSON),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() { });
        return taskStatus;
    }
}
