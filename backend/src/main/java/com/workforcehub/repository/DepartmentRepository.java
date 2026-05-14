package com.workforcehub.repository;

import com.workforcehub.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    Optional<Department> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);

    @Query("SELECT d FROM Department d WHERE d.deleted = false AND d.active = true")
    List<Department> findAllActive();
}
