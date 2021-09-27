package com.edi.infrastructure.SecondRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import com.edi.web.payload.SelectSQLPayload;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateComsRepository {
  private EntityManager entityManager;

  @Autowired
  public HibernateComsRepository(@Qualifier("masterEntityManager") EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  Session getSession() {
    return entityManager.unwrap(Session.class);
  }

  public List<Map<String, Object>> selectQuery(SelectSQLPayload payload) {
    Query sql = entityManager.createNativeQuery(payload.getQuery(), Tuple.class);
    ArrayList<HashMap<String,Object>> list = payload.getItems();
    if(list!=null && !list.isEmpty()){
      for(int i=0;i<list.size();i++){
        HashMap<String,Object> map = list.get(i);
        // key가 쿼리에 존재한다면
        if(payload.getQuery().indexOf((":" + map.get("key").toString() + " ")) != -1){
          sql.setParameter(map.get("key").toString(), map.get("query").toString().trim());
        }
      }
    }
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
