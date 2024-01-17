package com.realo.adminservice.services;

import com.realo.adminservice.exception.NotFoundException;
import com.realo.adminservice.exception.NotSavedException;
import com.realo.adminservice.models.Project;
import com.realo.adminservice.models.ProjectUser;
import com.realo.adminservice.models.User;
import com.realo.adminservice.repository.ProjectRepository;
import com.realo.adminservice.repository.ProjectUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectService {
    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, ProjectUserRepository projectUserRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.projectUserRepository = projectUserRepository;
        this.userService = userService;
    }

    @Transactional
    public Mono<Project> save(Project project) {
        return projectRepository.save(project)
                .onErrorMap((originalException) ->
                        new NotSavedException(
                                String.format("Failed to save project {%s}. Details: {%s} origin exception {%s} ",
                                        project,
                                        originalException.getMessage(),
                                        originalException),
                                originalException
                        )
                )
                .flatMap(createdProject -> {
                    if (project.getAssignedUsers().isEmpty()) {
                        return Mono.just(createdProject);
                    }

                    var usersByProject = project.getAssignedUsers()
                            .stream()
                            .map(user -> ProjectUser.builder().projectId(project.getId()).userId(user.getId()).build())
                            .toList();
                    createdProject.getAssignedUsers().addAll(project.getAssignedUsers());

                    return projectUserRepository.saveAll(usersByProject).then(Mono.just(createdProject));
                });
    }

    @Transactional
    public Flux<Project> getAll() {
        return projectRepository.findAll().flatMap(this::enrichProjectWithUsers);
    }

    @Transactional
    public Mono<Project> getByIdWithUsers(Long id) {
        return getById(id)
                .flatMap(this::enrichProjectWithUsers);
    }

    private Mono<Project> getById(Long id) {
        return projectRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Can't found project id: " + id)));
    }

    @Transactional
    public Mono<Project> update(Long id, Project updatedProject) {
        return getById(id)
                .map(existedProject -> {
                    existedProject.setProjectName(updatedProject.getProjectName());
                    existedProject.setStatus(updatedProject.getStatus());

                    return existedProject;
                })
                .flatMap(this::save)
                .onErrorMap((originalException) ->
                        new NotSavedException(
                                String.format("Failed to update project {%s} with data {%s}. Details: {%s} origin exception {%s} ",
                                        id,
                                        updatedProject,
                                        originalException.getMessage(),
                                        originalException),
                                originalException
                        ));
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return projectUserRepository.deleteByProjectId(id)
                .thenMany(projectRepository.deleteById(id))
                .then()
                .onErrorMap(originalException -> new NotFoundException("Project not found", originalException));
    }

    @Transactional
    public Mono<Project> addUser(Long projectId, Long userId) {
        var projectMono = getByIdWithUsers(projectId);
        var userMono = userService.getById(userId);

        return Mono
                .zip(projectMono, userMono)
                .flatMap(tuple -> addUser(tuple.getT1(), tuple.getT2()));
    }

    @Transactional
    public Mono<Project> removeUser(Long projectId, Long userId) {
        var userMono = userService.getById(userId);
        var projectMono = getByIdWithUsers(projectId);

        return Mono
                .zip(projectMono, userMono)
                .flatMap(tuple -> removeUser(tuple.getT1(), tuple.getT2()));
    }

    private Mono<Project> addUser(Project project, User user) {
        return enrichProjectWithUsers(project).flatMap(fullFilledProject -> {
            if (project.getAssignedUsers().contains(user)) {
                return Mono.just(project);
            }
            fullFilledProject.getAssignedUsers().add(user);

            return saveUserByProject(fullFilledProject.getId(), user.getId())
                    .then(Mono.just(fullFilledProject));
        });
    }

    private Mono<ProjectUser> saveUserByProject(Long projectId, Long userId) {
        return projectUserRepository
                .save(ProjectUser
                        .builder()
                        .projectId(projectId)
                        .userId(userId)
                        .build()
                ).onErrorMap((originalException) ->
                        new NotSavedException(
                                String.format("Failed to add user {%s} to project {%s}. Details: {%s} origin exception {%s} ",
                                        userId,
                                        projectId,
                                        originalException.getMessage(),
                                        originalException),
                                originalException
                        )
                );
    }

    private Mono<Project> removeUser(Project project, User user) {
        return enrichProjectWithUsers(project)
                .flatMap(fullFilledProject -> {
                    fullFilledProject.getAssignedUsers().remove(user);

                    return projectUserRepository.deleteByProjectIdAndUserId(project.getId(), user.getId()).then(Mono.just(fullFilledProject));
                });
    }

    private Mono<Project> enrichProjectWithUsers(Project project) {
        return getUsersByProjectId(project.getId())
                .collect(Collectors.toSet())
                .flatMap(users -> {
                    project.setAssignedUsers(users);

                    return Mono.just(project);
                });
    }

    private Flux<User> getUsersByProjectId(Long projectId) {
        return projectUserRepository.findByProjectId(projectId)
                .flatMap(usersByProject -> userService.getById(usersByProject.getUserId()));
    }
}
