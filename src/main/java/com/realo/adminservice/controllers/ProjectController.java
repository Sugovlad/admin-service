package com.realo.adminservice.controllers;

import com.realo.adminservice.models.Project;
import com.realo.adminservice.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/project")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Project> createProject(@RequestBody Project project) {
        return projectService.save(project);
    }

    @GetMapping
    public Flux<Project> getAllProjects() {
        return projectService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<Project> getProjectById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @PutMapping("/{id}")
    public Mono<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        return projectService.update(id, project);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProject(@PathVariable Long id) {
        return projectService.delete(id);
    }

    @PutMapping("/{projectId}/add-user/{userId}")
    public Mono<Project> assignUser(@PathVariable Long projectId, @PathVariable Long userId) {
        return projectService.addUser(projectId, userId);
    }

    @PutMapping("/{projectId}/remove-user/{userId}")
    public Mono<Project> removeUser(@PathVariable Long projectId, @PathVariable Long userId) {
        return projectService.removeUser(projectId, userId);
    }
}
