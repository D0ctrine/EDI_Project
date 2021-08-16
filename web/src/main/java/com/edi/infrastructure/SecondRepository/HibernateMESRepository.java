package com.edi.infrastructure.SecondRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
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

  public List<Map<String, Object>> selectQuery(String query) {
    Query sql = entityManager.createNativeQuery(query, Tuple.class);
    List<Tuple> lst = sql.getResultList();
    List<Map<String, Object>> result = convertTuplesToMap(lst);
    return result;
  }

  public static List<Map<String, Object>> convertTuplesToMap(List<Tuple> tuples) {
    List<Map<String, Object>> result = new ArrayList<>();
    for (Tuple single : tuples) {
        Map<String, Object> tempMap = new HashMap<>();
        for (TupleElement<?> key : single.getElements()) {
            tempMap.put(key.getAlias(), single.get(key));
        }
        result.add(tempMap);
    }
    return result;
}
}
