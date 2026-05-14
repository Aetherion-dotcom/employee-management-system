package com.workforcehub.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Department response DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private String headName;
    private Long headId;
    private int employeeCount;
    private LocalDateTime createdAt;
}
