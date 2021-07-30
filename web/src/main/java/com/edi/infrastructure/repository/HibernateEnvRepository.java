package com.edi.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.edi.domain.model.commonfile.environment.EnvRepository;
import com.edi.domain.model.commonfile.environment.EnvSetting;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateEnvRepository extends HibernateSupport<EnvSetting> implements EnvRepository{
  @Autowired
  public HibernateEnvRepository(@Qualifier("masterEntityManager") EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public void deleteEnvData(EnvSetting envSetting) {
    getSession().createQuery("delete from EnvSetting where id = :id")
    .setParameter("id", envSetting.getId())
    .executeUpdate();
  }

  @Override
  public EnvSetting findEnvData(String envId) {
    Query<EnvSetting> query = getSession().createQuery("FROM EnvSetting where id=:id",EnvSetting.class)
                                          .setParameter("id", envId);
    return query.uniqueResult();
  }

  @Override
  public List<EnvSetting> getEnvData(String configId) {
    Query<EnvSetting> query = getSession().createQuery("FROM EnvSetting where CFG_ID=:cfgid",EnvSetting.class)
                                      .setParameter("cfgid", configId);
    return query.list();
  }

  @Override
  public List<EnvSetting> updateEnvData(List<EnvSetting> envSetting) {
    List<EnvSetting> eList = new ArrayList<>();

    for(int i=0;i<envSetting.size();i++){
      EnvSetting eSetting = envSetting.get(i);
      int result = getSession().createQuery("UPDATE from EnvSetting set item = :item, value = :value, note = :note, update_user = :update_user, update_date = :update_date where id = :id")
      .setParameter("id", eSetting.getId())
      .setParameter("item", eSetting.getItem())
      .setParameter("value", eSetting.getValue())
      .setParameter("note", eSetting.getNote())
      .setParameter("update_user", eSetting.getUpdate_user())
      .setParameter("update_date", eSetting.getUpdate_date())
      .executeUpdate();
      if(result == 1) eList.add(eSetting);
    }
    return eList;

  }

}
