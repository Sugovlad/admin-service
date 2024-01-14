//package com.realo.adminservice;
//
//import com.realo.adminservice.exception.NotFoundException;
//import com.realo.adminservice.exception.NotSavedException;
//import com.realo.adminservice.models.User;
//import com.realo.adminservice.repository.ProjectUserRepository;
//import com.realo.adminservice.repository.UserRepository;
//import com.realo.adminservice.services.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//class UserServiceTest {
//
//    private UserService userService;
//    private UserRepository userRepository;
//    private ProjectUserRepository projectUserRepository;
//
//    @BeforeEach
//    void setUp() {
//        userRepository = Mockito.mock(UserRepository.class);
//        projectUserRepository = Mockito.mock(ProjectUserRepository.class);
//        userService = new UserService(userRepository, projectUserRepository);
//    }
//
//    @Test
//    void testSaveUserSuccess() {
//        User user = User.builder().id(1L).fullName("John Doe").loginName("johndoe").password("password").build();
//        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
//
//        StepVerifier.create(userService.save(user))
//                .expectNext(user)
//                .verifyComplete();
//    }
//
//    @Test
//    void testSaveUserFailure() {
//        User user = User.builder().fullName("Jane Doe").loginName("janedoe").password("secret").build();
//        when(userRepository.save(any(User.class))).thenReturn(Mono.error(new RuntimeException("DB error")));
//
//        StepVerifier.create(userService.save(user))
//                .expectError(NotSavedException.class)
//                .verify();
//    }
//    @Test
//    void testGetUserByIdSuccess() {
//        Long userId = 1L;
//        User user = new User(userId, "John Doe", "johndoe", "password");
//        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
//
//        StepVerifier.create(userService.getById(userId))
//                .expectNext(user)
//                .verifyComplete();
//    }
//
//    @Test
//    void testGetUserByIdNotFound() {
//        Long userId = 1L;
//        when(userRepository.findById(userId)).thenReturn(Mono.empty());
//
//        StepVerifier.create(userService.getById(userId))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void testDeleteUserSuccess() {
//        Long userId = 1L;
//        when(userRepository.deleteById(userId)).thenReturn(Mono.empty());
//        when(projectUserRepository.deleteByUserId(userId)).thenReturn(Flux.empty());
//
//        StepVerifier.create(userService.delete(userId))
//                .verifyComplete();
//    }
//
//    @Test
//    void testDeleteUserFailure() {
//        Long userId = 1L;
//        when(userRepository.deleteById(userId)).thenReturn(Mono.error(new RuntimeException("DB error")));
//
//        StepVerifier.create(userService.delete(userId))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//    @Test
//    void testUpdateUserSuccess() {
//        Long userId = 1L;
//        User existingUser = new User(userId, "John Doe", "johndoe", "password");
//        User updatedUser = new User(userId, "John Updated", "johndoe", "newpass");
//        when(userRepository.findById(userId)).thenReturn(Mono.just(existingUser));
//        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));
//
//        StepVerifier.create(userService.update(userId, updatedUser))
//                .expectNext(updatedUser)
//                .verifyComplete();
//    }
//
//    @Test
//    void testUpdateUserNotFound() {
//        Long userId = 1L;
//        User updatedUser = new User(userId, "John Updated", "johndoe", "newpass");
//        when(userRepository.findById(userId)).thenReturn(Mono.empty());
//
//        StepVerifier.create(userService.update(userId, updatedUser))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//}