package com.realo.adminservice.services;

import com.realo.adminservice.exception.NotFoundException;
import com.realo.adminservice.exception.NotSavedException;
import com.realo.adminservice.models.User;
import com.realo.adminservice.repository.ProjectUserRepository;
import com.realo.adminservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;


    @Autowired
    public UserService(UserRepository userRepository, ProjectUserRepository projectUserRepository) {
        this.userRepository = userRepository;
        this.projectUserRepository = projectUserRepository;
    }

    public Mono<User> save(User user) {
        return userRepository.save(user)
                .onErrorMap(originalException ->
                        new NotSavedException("Failed to save project. Details: " + originalException.getMessage(), originalException)
                );
    }

    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    public Mono<User> getById(Long id) {
        return userRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Can't found project id: " + id)));
    }

    public Mono<Void> delete(Long id) {
        return projectUserRepository.deleteByUserId(id).
                thenMany(userRepository.deleteById(id))
                .then()
                .onErrorMap(originalException ->
                        new NotFoundException("Failed to save project. Details: " + originalException.getMessage(), originalException)
                );
    }

    public Mono<User> update(Long id, User updatedUser) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User Not found")))
                .map(user -> {
                    user.setFullName(updatedUser.getFullName());
                    user.setLoginName(updatedUser.getLoginName());
                    user.setPassword(updatedUser.getPassword());

                    return user;
                })
                .flatMap(this::save);
    }

}

