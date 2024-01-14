//package com.realo.adminservice;
//
//import com.realo.adminservice.exception.NotFoundException;
//import com.realo.adminservice.exception.NotSavedException;
//import com.realo.adminservice.models.Project;
//import com.realo.adminservice.models.ProjectUser;
//import com.realo.adminservice.models.User;
//import com.realo.adminservice.repository.ProjectRepository;
//import com.realo.adminservice.repository.ProjectUserRepository;
//import com.realo.adminservice.services.ProjectService;
//import com.realo.adminservice.services.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//class ProjectServiceTest {
//
//    private ProjectService projectService;
//
//    private ProjectRepository projectRepository;
//    private ProjectUserRepository projectUserRepository;
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        projectUserRepository = Mockito.mock(ProjectUserRepository.class);
//        projectRepository = Mockito.mock(ProjectRepository.class);
//        userService = Mockito.mock(UserService.class);
//        projectService = new ProjectService(projectRepository, projectUserRepository, userService);
//    }
//
//    @Test
//    void testSaveProjectSuccess() {
//        User user = new User(1L, "John Doe", "john", "password");
//        Set<User> assignedUsers = new HashSet<>();
//        assignedUsers.add(user);
//
//        Project project = new Project(1L, "Project X", 1, assignedUsers);
//        when(projectRepository.save(any(Project.class))).thenReturn(Mono.just(project));
//        when(projectUserRepository.saveAll(any(Iterable.class))).thenReturn(Flux.just(new ProjectUser(1L, 1L, 1L)));
//
//        StepVerifier.create(projectService.save(project))
//                .expectNextMatches(savedProject ->
//                        savedProject.getProjectName().equals("Project X") &&
//                                savedProject.getAssignedUsers().contains(user))
//                .verifyComplete();
//    }
//
//    @Test
//    void testSaveProjectFailure() {
//        Project project = new Project(1L, "Project X", 1, new HashSet<>());
//        when(projectRepository.save(any(Project.class))).thenReturn(Mono.error(new RuntimeException("DB error")));
//
//        StepVerifier.create(projectService.save(project))
//                .expectErrorMatches(throwable -> throwable instanceof NotSavedException &&
//                        throwable.getMessage().contains("Failed to save project"))
//                .verify();
//    }
//
//    @Test
//    void testGetAllProjectsSuccess() {
//        Long projectId = 1L;
//        Long secondProject = 2L;
//        HashSet<User> assignedUsers = new HashSet<>();
//        User user = User.builder().id(1L).fullName("user").loginName("user").password("password").build();
//        ProjectUser userByProject = new ProjectUser(1L, projectId, user.getId());
//        assignedUsers.add(user);
//
//        Project project1 = new Project(projectId, "Project 1", 0, assignedUsers);
//        Project project2 = new Project(secondProject, "Project 2", 1, new HashSet<>());
//
//
//        when(projectUserRepository.findByProjectId(projectId)).thenReturn(Flux.just(userByProject));
//        when(projectUserRepository.findByProjectId(secondProject)).thenReturn(Flux.just());
//        when(userService.getById(user.getId())).thenReturn(Mono.just(user));
//        when(projectRepository.findAll()).thenReturn(Flux.just(project1, project2));
//
//        StepVerifier.create(projectService.getAll())
//                .expectNext(project1, project2)
//                .verifyComplete();
//    }
//
//    @Test
//    void testGetProjectByIdSuccess() {
//        Long projectId = 1L;
//        Project project = new Project(projectId, "Project X", 1, new HashSet<>());
//        when(projectRepository.findById(projectId)).thenReturn(Mono.just(project));
//        when(projectUserRepository.findByProjectId(projectId)).thenReturn(Flux.empty());
//
//        StepVerifier.create(projectService.getById(projectId))
//                .expectNext(project)
//                .verifyComplete();
//    }
//
//    @Test
//    void testGetProjectByIdNotFound() {
//        Long projectId = 1L;
//        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());
//
//        StepVerifier.create(projectService.getById(projectId))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void testUpdateProjectSuccess() {
//        Long projectId = 1L;
//        Project existingProject = new Project(projectId, "Old Project", 1, new HashSet<>());
//        User user = User.builder().id(1L).fullName("user").loginName("user").password("password").build();
//        ProjectUser userByProject = new ProjectUser(1L, existingProject.getId(), user.getId());
//
//        HashSet<User> assignedUsers = new HashSet<>();
//        assignedUsers.add(user);
//
//        Project updatedProject = new Project(projectId, "Updated Project", 2, assignedUsers);
//        when(projectRepository.findById(projectId)).thenReturn(Mono.just(existingProject));
//        when(projectUserRepository.findByProjectId(projectId)).thenReturn(Flux.just(userByProject));
//        when(projectUserRepository.saveAll(any(Iterable.class))).thenReturn(Flux.just(userByProject));
//        when(userService.getById(user.getId())).thenReturn(Mono.just(user));
//        when(projectRepository.save(any(Project.class))).thenReturn(Mono.just(updatedProject));
//
//        StepVerifier.create(projectService.update(projectId, updatedProject))
//                .expectNext(updatedProject)
//                .verifyComplete();
//    }
//
//    @Test
//    void testUpdateProjectNotFound() {
//        Long projectId = 1L;
//        Project updatedProject = new Project(projectId, "Updated Project", 1, new HashSet<>());
//        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());
//
//        StepVerifier.create(projectService.update(projectId, updatedProject))
//                .expectErrorMatches(throwable -> throwable instanceof NotSavedException &&
//                        throwable.getCause() instanceof NotFoundException
//                )
//                .verify();
//    }
//
//    @Test
//    void testDeleteProjectSuccess() {
//        Long projectId = 1L;
//        when(projectRepository.deleteById(projectId)).thenReturn(Mono.empty());
//        when(projectUserRepository.deleteByProjectId(projectId)).thenReturn(Flux.empty());
//
//        StepVerifier.create(projectService.delete(projectId))
//                .verifyComplete();
//    }
//
//    @Test
//    void testDeleteProjectFailure() {
//        Long projectId = 1L;
//        when(projectRepository.deleteById(projectId)).thenReturn(Mono.error(new RuntimeException("DB error")));
//
//        StepVerifier.create(projectService.delete(projectId))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void testAddUserToProjectSuccess() {
//        Long projectId = 1L;
//        Long userId = 2L;
//
//        HashSet<User> assignedUsers = new HashSet<>();
//        User user = new User(userId, "John Doe", "john", "password");
//        assignedUsers.add(user);
//
//        Project project = new Project(projectId, "Project X", 1, new HashSet<>());
//        ProjectUser userByProject = new ProjectUser(null, projectId, userId);
//
//        when(projectRepository.findById(projectId)).thenReturn(Mono.just(project));
//        when(projectService.getById(projectId)).thenReturn(Mono.just(project));
//        when(userService.getById(userId)).thenReturn(Mono.just(user));
//        when(projectUserRepository.findByProjectId(projectId)).thenReturn(Flux.just());
//        when(projectUserRepository.save(userByProject)).thenReturn(Mono.just(new ProjectUser(1L, projectId, userId)));
//
//        StepVerifier.create(projectService.addUser(projectId, userId))
//                .expectNextMatches(p -> p.getAssignedUsers().contains(user))
//                .verifyComplete();
//    }
//
//    @Test
//    void testAddUserToProjectFailure() {
//        Long projectId = 1L;
//        Long userId = 2L;
//        when(projectRepository.findById(projectId)).thenReturn(Mono.error(new NotFoundException("Project not found")));
//        when(userService.getById(userId)).thenReturn(Mono.just(new User(userId, "John Doe", "john", "password")));
//
//        StepVerifier.create(projectService.addUser(projectId, userId))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void testRemoveUserFromProjectSuccess() {
//        Long projectId = 1L;
//        Long userId = 2L;
//
//        User user = new User(userId, "John Doe", "john", "password");
//        Set<User> assignedUsers = new HashSet<>();
//        assignedUsers.add(user);
//        Project project = new Project(projectId, "Project X", 1, assignedUsers);
//
//        when(projectRepository.findById(projectId)).thenReturn(Mono.just(project));
//        when(userService.getById(userId)).thenReturn(Mono.just(user));
//        when(projectService.getById(projectId)).thenReturn(Mono.just(project));
//        when(projectUserRepository.findByProjectId(projectId)).thenReturn(Flux.just());
//        when(projectUserRepository.deleteByProjectIdAndUserId(projectId, userId)).thenReturn(Mono.empty());
//
//        StepVerifier.create(projectService.removeUser(projectId, userId))
//                .expectNextMatches(p -> !p.getAssignedUsers().contains(user))
//                .verifyComplete();
//    }
//
//    @Test
//    void testRemoveUserFromProjectFailure() {
//        Long projectId = 1L;
//        Long userId = 2L;
//        when(projectRepository.findById(projectId)).thenReturn(Mono.error(new NotFoundException("Project not found")));
//        when(userService.getById(userId)).thenReturn(Mono.just(new User(userId, "John Doe", "john", "password")));
//
//        StepVerifier.create(projectService.removeUser(projectId, userId))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//}
//
