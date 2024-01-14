package com.realo.adminservice.repository;

import com.realo.adminservice.models.Project;
import com.realo.adminservice.models.ProjectUser;
import com.realo.adminservice.models.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectUserRepository extends ReactiveCrudRepository<ProjectUser, Long> {
    Flux<ProjectUser> findByProjectId(Long projectId);
    Flux<Void> deleteByUserId(Long userId);
    Flux<Void> deleteByProjectId(Long projectId);
    Flux<ProjectUser> findByUserId(Long userId);
    Mono<Void> deleteByProjectIdAndUserId(Long projectId, Long userId);


}