package com.edi.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.edi.domain.model.settingfile.headntail.headntail;
import com.edi.domain.model.settingfile.headntail.hntRepository;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateHnTRepository  extends HibernateSupport<headntail> implements hntRepository{
  @Autowired
  public HibernateHnTRepository(@Qualifier("masterEntityManager") EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public void deleteHnTData(headntail hnt) {
    getSession().createQuery("DELETE FROM headntail WHERE ID=:id")
                .setParameter("id", hnt.getId())
                .executeUpdate();
  }

  @Override
  public headntail findHnTData(String hntId) {
    Query<headntail> hnt = getSession().createQuery("FROM file_define WHERE ID=:id",headntail.class)
                                       .setParameter("id", hntId);

    return hnt.uniqueResult();
  }

  @Override
  public List<headntail> getHnTData(String configId) {
    List<headntail> hnt = getSession().createQuery("FROM headntail WHERE CFG_ID=:cfg_id",headntail.class)
                                      .setParameter("cfg_id", configId)
                                      .getResultList();
    return hnt;
  }

  @Override
  public List<headntail> updateHnTData(List<headntail> hnt) {
    List<headntail> hntList = new ArrayList<headntail>();
    for(headntail hList : hnt){
     int result = getSession().createQuery("UPDATE FROM headntail SET DATA_TYPE=:data_type, NOTE=:note, VALUE=:value, CM_F01=:cm_f01, CM_F02=:cm_f02, UPDATE_USER=:update_user, UPDATE_DATE=:update_date WHERE ID=:id")
                              .setParameter("id", hList.getId())
                              .setParameter("data_type", hList.getData_type())
                              .setParameter("note", hList.getNote())
                              .setParameter("value", hList.getValue())
                              .setParameter("cm_f01", hList.getCm_f01())
                              .setParameter("cm_f02", hList.getCm_f02())
                              .setParameter("update_user", hList.getUpdate_user())
                              .setParameter("update_date", hList.getUpdate_date())
                              .executeUpdate();
      if(result == 1) hntList.add(hList);
    }
    return hntList;
  }

}
