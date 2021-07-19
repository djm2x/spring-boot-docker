package com.docker.first.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
@NoRepositoryBean
public interface GenericRepository<T extends Serializable, ID > extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {}


