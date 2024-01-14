package com.realo.adminservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Data
@Table("projects")
@AllArgsConstructor
public class Project {

    public Project() {
        assignedUsers = new HashSet<>();
    }

    @Id
    private Long id;

    private String projectName;

    private int status;

    @Transient
    private Set<User> assignedUsers;
}