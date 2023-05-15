package com.example.demo.repository;

import com.example.demo.model.SerializeObj;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerializeRepository extends CrudRepository<SerializeObj, Integer> {
}
