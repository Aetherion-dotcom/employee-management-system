package com.workforcehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Department entity representing organizational departments.
 */
@Entity
@Table(name = "departments", indexes = {
        @Index(name = "idx_dept_name", columnList = "name"),
        @Index(name = "idx_dept_code", columnList = "code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id")
    private Employee head;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    /**
     * Get the number of employees in this department.
     */
    public int getEmployeeCount() {
        return employees != null ? (int) employees.stream().filter(e -> !e.isDeleted()).count() : 0;
    }
}
