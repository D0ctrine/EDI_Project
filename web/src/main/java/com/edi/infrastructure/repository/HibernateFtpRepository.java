package com.edi.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.edi.domain.model.commonfile.ftp.FtpRepository;
import com.edi.domain.model.commonfile.ftp.FtpSetting;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateFtpRepository extends HibernateSupport<FtpSetting> implements FtpRepository{
  @Autowired
  public HibernateFtpRepository(@Qualifier("masterEntityManager") EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public void deleteQueryData(FtpSetting fSetting) {
    getSession().createQuery("delete from FtpSetting where id = :id")
    .setParameter("id", fSetting.getId())
    .executeUpdate();

  }

  @Override
  public FtpSetting findFtpData(String ftpId) {
    Query<FtpSetting> query = getSession().createQuery("FROM FtpSetting where id=:id",FtpSetting.class)
                                          .setParameter("id", ftpId);
    return query.uniqueResult();
  }

  @Override
  public List<FtpSetting> getFtpData(String configId) {
    Query<FtpSetting> query = getSession().createQuery("FROM FtpSetting where CFG_ID=:cfgid",FtpSetting.class)
                                      .setParameter("cfgid", configId);
    return query.list();
  }

  @Override
  public List<FtpSetting> updateFtpData(List<FtpSetting> ftpSetting) {
    List<FtpSetting> fList = new ArrayList<>();

    for(FtpSetting fSetting : ftpSetting){
      int result = getSession().createQuery("UPDATE from FtpSetting set ftp_addr = :ftpAddr, user_id = :userId, pwd = :pwd, base_dir = :baseDir, type = :type, update_user = :update_user, update_date = :update_date where id = :id")
      .setParameter("id", fSetting.getId())
      .setParameter("ftpAddr", fSetting.getFtp_addr())
      .setParameter("userId", fSetting.getUser_id())
      .setParameter("pwd", fSetting.getPwd())
      .setParameter("baseDir", fSetting.getBase_dir())
      .setParameter("type", fSetting.getType())
      .setParameter("update_user", fSetting.getUpdate_user())
      .setParameter("update_date", fSetting.getUpdate_date())
      .executeUpdate();
      if(result == 1) fList.add(fSetting);
    }
    return fList;

  }


}
