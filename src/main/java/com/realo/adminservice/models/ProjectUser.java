package com.realo.adminservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("project_user")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUser {
    @Id
    private Long id;
    private Long projectId;
    private Long userId;
}
