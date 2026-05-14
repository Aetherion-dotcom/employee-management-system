package com.workforcehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Department create/update request DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(max = 20, message = "Department code must not exceed 20 characters")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Long headId;
    private boolean active = true;
}
