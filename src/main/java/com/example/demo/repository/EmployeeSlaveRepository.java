package com.example.demo.repository;

import com.example.demo.config.ReadOnlyRepository;
import com.example.demo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @ReadOnlyRepository together with the SlaveDataSourceConfig will route all DB calls to the
 * slave data source.
 * This is achieved via the includeFilter including the annotation @ReadOnlyRepository in SlaveDataSourceConfig.
 */
@Repository
@ReadOnlyRepository
public interface EmployeeSlaveRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findById(Long id);

    List<Employee> findAll();

    long count();

    /**
     * Distinct is mentioned by JPA to be problematic, so it is always better
     * to write a declared query when you need to use distinct.
     *
     * For instance, "select distinct u from User u" yields different results from
     * "select distinct u.lastname from User u" as the distinct keyword of the first query
     * applies to the id field, which has no duplicates.
     */
    // Collection<Employee> findDistinctByFirstName(String firstName);
    @Query(value = "SELECT * FROM " +
            "(SELECT *, ROW_NUMBER() OVER " +
            "(PARTITION BY e.first_name ORDER BY e.id) AS Row FROM Employees e) AS a " +
            "WHERE a.Row = 1",
            nativeQuery = true
    )
    Collection<Employee> findDistinctFirstName();

    /**
     * Chain multiple query predicate keywords together.
     * Just make sure that the parameters are ordered correctly.
     */
    Collection<Employee> findByEligibilityAndAgeAfterOrderByAgeAsc(boolean isEligible, Integer age);

    long countByEligibility(boolean eligibility);
}
