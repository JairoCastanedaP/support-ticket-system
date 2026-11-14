package com.co.support.it.support_ticket_system.generic.repository;

import com.co.support.it.support_ticket_system.generic.entity.GenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Repositorio base de las entidades que heredan de {@link GenericEntity}.
 */
@NoRepositoryBean
public interface GenericRepository<T extends GenericEntity, ID> extends JpaRepository<T, ID> {

    List<T> findByActiveTrue();
}
