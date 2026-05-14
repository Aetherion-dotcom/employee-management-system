package com.workforcehub.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Document entity for employee document management.
 */
@Entity
@Table(name = "documents", indexes = {
        @Index(name = "idx_doc_employee", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "description", length = 500)
    private String description;
}
