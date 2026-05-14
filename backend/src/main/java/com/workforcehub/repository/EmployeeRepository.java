package com.workforcehub.repository;

import com.workforcehub.entity.Employee;
import com.workforcehub.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByUserId(Long userId);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false")
    Page<Employee> findAllActive(Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false AND e.status = :status")
    List<Employee> findByStatus(@Param("status") EmployeeStatus status);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false AND e.department.id = :departmentId")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.deleted = false AND e.status = :status")
    long countByStatus(@Param("status") EmployeeStatus status);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.deleted = false")
    long countActive();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.deleted = false AND e.department.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false AND " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Employee> searchEmployees(@Param("search") String search, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.deleted = false AND e.joiningDate BETWEEN :startDate AND :endDate")
    List<Employee> findByJoiningDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e.department.name, COUNT(e) FROM Employee e WHERE e.deleted = false GROUP BY e.department.name")
    List<Object[]> countByDepartment();

    @Query("SELECT MONTH(e.joiningDate), COUNT(e) FROM Employee e WHERE e.deleted = false AND YEAR(e.joiningDate) = :year GROUP BY MONTH(e.joiningDate)")
    List<Object[]> getMonthlyHiringStats(@Param("year") int year);
}
