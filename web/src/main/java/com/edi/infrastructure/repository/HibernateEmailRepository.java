package com.edi.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.edi.domain.model.commonfile.email.EmailRepository;
import com.edi.domain.model.commonfile.email.EmailSetting;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateEmailRepository extends HibernateSupport<EmailSetting> implements EmailRepository {

  @Autowired
  public HibernateEmailRepository(@Qualifier("masterEntityManager") EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public void deleteEmailData(EmailSetting emailSetting) {
      getSession().createQuery("delete from EmailSetting where id = :id")
                  .setParameter("id", emailSetting.getId())
                  .executeUpdate();
  }

  @Override
  public EmailSetting findEmailData(String emailId) {
    Query<EmailSetting> query = getSession().createQuery("FROM EmailSetting where id=:id",EmailSetting.class)
                                      .setParameter("id", emailId);
    return query.uniqueResult();
  }

  @Override
  public List<EmailSetting> getEmailData(String configId) {
    Query<EmailSetting> query = getSession().createQuery("FROM EmailSetting where CFG_ID=:cfgid",EmailSetting.class)
                                      .setParameter("cfgid", configId);
    return query.list();
  }

  @Override
  public List<EmailSetting> updateEmailData(List<EmailSetting> emailSetting) {
    List<EmailSetting> eList = new ArrayList<>();

    for(int i=0;i<emailSetting.size();i++){
      EmailSetting eSetting = emailSetting.get(i);
      int result = getSession().createQuery("UPDATE from EmailSetting set host = :host, port = :port, access_id = :accessId, access_pwd = :accessPwd, update_user = :update_user, update_date = :update_date where id = :id")
      .setParameter("id", eSetting.getId())
      .setParameter("host", eSetting.getHost())
      .setParameter("port", eSetting.getPort())
      .setParameter("accessId", eSetting.getAccess_id())
      .setParameter("accessPwd", eSetting.getAccess_pwd())
      .setParameter("update_user", eSetting.getUpdate_user())
      .setParameter("update_date", eSetting.getUpdate_date())
      .executeUpdate();
      if(result == 1) eList.add(eSetting);
    }
    return eList;

  }

}
