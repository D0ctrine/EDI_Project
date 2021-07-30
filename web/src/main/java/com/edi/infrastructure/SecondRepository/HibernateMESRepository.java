package com.edi.infrastructure.SecondRepository;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateMESRepository {
  private EntityManager entityManager;

  @Autowired
  public HibernateMESRepository(@Qualifier("mesEntityManager") EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  Session getSession() {
    return entityManager.unwrap(Session.class);
  }

  public List selectQuery(String query) {
    return getSession().createSQLQuery(query).getResultList();
  }
}
