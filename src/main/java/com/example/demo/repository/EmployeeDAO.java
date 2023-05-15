package com.example.demo.repository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.example.demo.model.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * EmployeeDAO implements both the master & slave repositories to decide which of these databases to use
 * for each operation.
 */
@Repository
public class EmployeeDAO implements EmployeeMasterRepository, EmployeeSlaveRepository {

    private EmployeeMasterRepository masterRepository;
    private EmployeeSlaveRepository slaveRepository;

    @Autowired
    public EmployeeDAO(EmployeeMasterRepository employeeMasterRepository,
                       EmployeeSlaveRepository employeeSlaveRepository) {
        this.masterRepository = employeeMasterRepository;
        this.slaveRepository = employeeSlaveRepository;
    }

    @Override
    public <S extends Employee> S save(S entity) {
        return masterRepository.save(entity);
    }

    @Override
    public void delete(Employee entity) {
        masterRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        masterRepository.deleteAllById(longs);
    }

    @Override
    public void deleteAll(Iterable<? extends Employee> entities) {
        masterRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        masterRepository.deleteAll();
    }

    @Override
    public void updateEmployeeEligibility(Long id, boolean eligibility) {
        masterRepository.updateEmployeeEligibility(id, eligibility);
    }

    @Override
    public Collection<Employee> findDistinctFirstName() {
        return slaveRepository.findDistinctFirstName();
    }

    @Override
    public Collection<Employee> findByEligibilityAndAgeAfterOrderByAgeAsc(boolean eligibility, Integer age) {
        return slaveRepository.findByEligibilityAndAgeAfterOrderByAgeAsc(eligibility, age);
    }

    @Override
    public long countByEligibility(boolean eligibility) {
        return slaveRepository.countByEligibility(eligibility);
    }

    @Override
    public void flush() {
        masterRepository.flush();
    }

    @Override
    public <S extends Employee> S saveAndFlush(S entity) {
        return masterRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends Employee> List<S> saveAllAndFlush(Iterable<S> entities) {
        return masterRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<Employee> entities) {
        masterRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        masterRepository.deleteAllByIdInBatch(longs);
    }

    @Override
    public void deleteAllInBatch() {
        masterRepository.deleteAllInBatch();
    }

    @Override
    @Deprecated
    public Employee getOne(Long aLong) {
        return slaveRepository.getOne(aLong);
    }

    @Override
    @Deprecated
    public Employee getById(Long aLong) {
        return slaveRepository.getById(aLong);
    }

    /**
     * getReferenceById gets an instance, whose state may be lazily fetched.
     * If requested instance does not exist, EntityNotFoundException is thrown when
     * the instance state is first accessed.
     */
    @Override
    public Employee getReferenceById(Long aLong) {
        return slaveRepository.getReferenceById(aLong);
    }

    @Override
    public <S extends Employee> Optional<S> findOne(Example<S> example) {
        return slaveRepository.findOne(example);
    }

    @Override
    public <S extends Employee> List<S> findAll(Example<S> example) {
        return slaveRepository.findAll(example);
    }

    @Override
    public <S extends Employee> List<S> findAll(Example<S> example, Sort sort) {
        return slaveRepository.findAll(example, sort);
    }

    @Override
    public <S extends Employee> Page<S> findAll(Example<S> example, Pageable pageable) {
        return slaveRepository.findAll(example, pageable);
    }

    @Override
    public <S extends Employee> long count(Example<S> example) {
        return slaveRepository.count(example);
    }

    @Override
    public <S extends Employee> boolean exists(Example<S> example) {
        return slaveRepository.exists(example);
    }

    @Override
    public <S extends Employee, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return slaveRepository.findBy(example, queryFunction);
    }

    @Override
    public <S extends Employee> List<S> saveAll(Iterable<S> entities) {
        return slaveRepository.saveAll(entities);
    }

    public Optional<Employee> findByIdInMaster(Long aLong) {
        return masterRepository.findById(aLong);
    }

    @Override
    public Optional<Employee> findById(Long aLong) {
        return slaveRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return slaveRepository.existsById(aLong);
    }

    @Override
    public List<Employee> findAll() {
        return slaveRepository.findAll();
    }

    @Override
    public List<Employee> findAllById(Iterable<Long> longs) {
        return slaveRepository.findAllById(longs);
    }

    @Override
    public long count() {
        return slaveRepository.count();
    }

    @Override
    public void deleteById(Long aLong) {
        masterRepository.deleteById(aLong);
    }

    @Override
    public List<Employee> findAll(Sort sort) {
        return slaveRepository.findAll(sort);
    }

    @Override
    public Page<Employee> findAll(Pageable pageable) {
        return slaveRepository.findAll(pageable);
    }
}
