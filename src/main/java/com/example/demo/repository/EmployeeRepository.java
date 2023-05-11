package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;

import com.example.demo.model.Employee;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
            "(PARTITION BY e.FirstName ORDER BY e.Id) AS Row FROM Employees e) AS a " +
            "WHERE a.Row = 1",
            nativeQuery = true
    )
    Collection<Employee> findDistinctFirstName();

    /**
     * Chain multiple query predicate keywords together.
     * Just make sure that the parameters are ordered correctly.
     */
    Collection<Employee> findByAgeBetweenOrderByAgeAsc(Integer startAge, Integer endAge);
    Collection<Employee> findByAgeAfterOrderByAgeAsc(Integer age);
    long countByAgeBetween(Integer startAge, Integer endAge);
    long countByAgeAfter(Integer age);

    @Query(value = "UPDATE Employees e set e.Eligibility = ?2 WHERE e.Id = ?1", nativeQuery = true)
    @Modifying
    void updateEmployeeEligibility(Long id, boolean isEligible);
}
