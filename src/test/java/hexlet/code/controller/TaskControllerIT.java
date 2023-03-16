package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dtos.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repositories.LabelRepository;
import hexlet.code.repositories.TaskRepository;
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
import java.util.Set;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelControllerIT.DEFAULT_LABEL_NAME;
import static hexlet.code.controller.TaskStatusControllerIT.DEFAULT_TASK_STATUS_NAME;
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
public class TaskControllerIT {

    @Autowired
    private AuthorizationUtils authUtils;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

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
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private static final String TASK_CONTROLLER_PATH =  "/api/tasks";

    private static final String TASK_CONTROLLER_PATH_ID =  "/api/tasks/{id}";

    private static final String DEFAULT_TASK_NAME = "Task";

    @Test
    public void createTask() throws Exception {

        assertEquals(0, taskRepository.count());

        addDefaultTask();

        assertEquals(1, taskRepository.count());
    }

    @Test
    public void getTaskById() throws Exception {

        final Task defaultTask = addDefaultTask();

        final MockHttpServletRequestBuilder request = authUtils.getAuthRequest(
                get(TASK_CONTROLLER_PATH_ID, defaultTask.getId()),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertEquals(defaultTask.getId(), task.getId());
        assertEquals(defaultTask.getName(), task.getName());
        assertEquals(defaultTask.getDescription(), task.getDescription());
        assertEquals(defaultTask.getAuthor().getId(), task.getAuthor().getId());
    }

    @Test
    public void getAllTasks() throws Exception {
        addDefaultTask();

        final MockHttpServletResponse response = mockMvc.perform(get(TASK_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertNotNull(tasks);
        assertEquals(tasks.size(), 1);
    }

    @Test
    public void updateTask() throws Exception {
        final Task defaultTask = addDefaultTask();
        final Long defaultTaskId = defaultTask.getId();

        final String newTaskName = "Code Refactoring";
        final String newTaskDescription = "Refactor old code";
        final TaskDto taskDto = getNewTask(newTaskName, newTaskDescription);

        final MockHttpServletRequestBuilder updateRequest = authUtils.getAuthRequest(
                put(TASK_CONTROLLER_PATH_ID, defaultTaskId)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertTrue(taskRepository.existsById(defaultTaskId));
        assertNull(taskRepository.findByName(DEFAULT_TASK_NAME).orElse(null));
        assertNotNull(taskRepository.findByName(newTaskName).orElse(null));

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertNotNull(task);
        assertEquals(defaultTaskId, task.getId());
        assertEquals(newTaskName, task.getName());
        assertEquals(newTaskDescription, task.getDescription());
    }

    @Test
    public void deleteTask() throws Exception {
        final Task task = addDefaultTask();

        final MockHttpServletRequestBuilder deleteRequest = authUtils.getAuthRequest(
                delete(TASK_CONTROLLER_PATH_ID, task.getId()),
                DEFAULT_USER_EMAIL
        );

        mockMvc.perform(deleteRequest).andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }


    private Task addDefaultTask() throws Exception {
        return addTask(DEFAULT_TASK_NAME, "");
    }

    private Task addTask(String taskName, String taskDescription) throws Exception {
        final TaskDto taskDto = getNewTask(taskName, taskDescription);

        final MockHttpServletRequestBuilder request = authUtils.getAuthRequest(
                post(TASK_CONTROLLER_PATH)
                        .content(asJson(taskDto))
                        .contentType(APPLICATION_JSON),
                DEFAULT_USER_EMAIL
        );

        final MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() { });
        return task;
    }

    private TaskDto getNewTask(String taskName, String taskDescription) {
        final User user = userRepository.findByEmail(DEFAULT_USER_EMAIL).get();

        final TaskStatus taskStatus = taskStatusRepository.save(
                new TaskStatus(DEFAULT_TASK_STATUS_NAME));

        final Label label = labelRepository.save(new Label(DEFAULT_LABEL_NAME));

        final TaskDto task = new TaskDto(
                taskName,
                taskDescription,
                user.getId(),
                taskStatus.getId(),
                Set.of(label.getId())
        );
        return task;
    }
}
