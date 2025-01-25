package com.example.demo.repository;

import com.example.demo.model.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiverRepository extends JpaRepository<Archive , Long> {
}
