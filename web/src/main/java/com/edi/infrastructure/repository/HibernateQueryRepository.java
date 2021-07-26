package com.edi.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.edi.domain.model.commonfile.query.QueryRepository;
import com.edi.domain.model.commonfile.query.QuerySetting;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateQueryRepository extends HibernateSupport<QuerySetting> implements QueryRepository{
  public HibernateQueryRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public void deleteQueryData(QuerySetting qSetting) {
    getSession().createQuery("delete from QuerySetting where id = :id")
    .setParameter("id", qSetting.getId())
    .executeUpdate();
  }

  @Override
  public QuerySetting findQueryData(String qId) {
    Query<QuerySetting> query = getSession().createQuery("FROM QuerySetting where id=:id",QuerySetting.class)
                                          .setParameter("id", qId);
    return query.uniqueResult();
  }

  @Override
  public List<QuerySetting> getQueryData(String configId) {
    Query<QuerySetting> query = getSession().createQuery("FROM QuerySetting where setting_id=:cfgid",QuerySetting.class)
                                            .setParameter("cfgid", configId);
    return query.list();
  }

  @Override
  public List<QuerySetting> updateQueryData(List<QuerySetting> querySetting) {
    List<QuerySetting> qList = new ArrayList<>();

    for(int i=0;i<querySetting.size();i++){
      QuerySetting qSetting = querySetting.get(i);
      int result = getSession().createQuery("UPDATE from QuerySetting set key = :key, type = :type, query = :query where id = :id")
      .setParameter("id", qSetting.getId())
      .setParameter("key", qSetting.getKey())
      .setParameter("type", qSetting.getType())
      .setParameter("query", qSetting.getQuery())
      .executeUpdate();
      if(result == 1) qList.add(qSetting);
    }
    return qList;
  }

}
