package com.example.demo.repository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import com.example.demo.model.Employee;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    /**
     * Spring Data repository query derivation mechanism supports implementation
     * of queries that follow a certain format and uses certain keywords.
     *
     * A query consists of subject keywords and predicate keywords and modifiers as well as return types
     * However, not all keywords, modifiers, return types are supported by every data store, so need to check.
     *
     * Query subject keywords:
     * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject
     *
     * Query predicate keywords & modifiers:
     * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.predicate
     *
     * Query return types:
     * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.return.types
     *
     * Examples of query creation can be found here:
     * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
     */

    /**
     * You can also opt to write declared queries using @Query when there is a need for custom queries or
     * if usage of Spring Data repository query derivation mechanism leads to wrong results.
     */

    // Queries
    <S extends Employee> S save(S entity);

    Optional<Employee> findById(Long id);
    Collection<Employee> findAll();
    long count();
    void delete(Employee entity);

    void deleteAll();

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

    /**
     * @Modifying annotation must be used with the @Query annotation to allow queries that aren't SELECT queries
     * to be defined.
     *
     * @Transactional annotation adds default configurations for the propagation type, isolation level,
     * timeout, readOnly flag & rollback rules for transactional management that is handled by spring context.
     * Note that by default, rollback happens for runtime & unchecked exceptions only. DataAccessException is
     * a runtime exception.
     *
     * @Query annotation when used without the nativeQuery flag set to true will reference the naming of
     * the model's respective Java class and properties.
     * Note that with the nativeQuery flag set to true, parameterized queries will not be possible.
     *
     * Also a specific update method is usually not necessary, because the saveAll and save methods perform
     * upsert operation.
     */
    @Query("UPDATE Employee e SET e.eligibility = ?2 WHERE e.id = ?1")
    @Modifying
    @Transactional(rollbackFor = SQLException.class)
    void updateEmployeeEligibility(Long id, boolean eligibility);

    long countByEligibility(boolean eligibility);
}
