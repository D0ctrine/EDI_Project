package com.edi.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.edi.domain.model.settingfile.Setting.fileDefRepository;
import com.edi.domain.model.settingfile.Setting.file_define;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateFileDefRepository extends HibernateSupport<file_define> implements fileDefRepository{
  public HibernateFileDefRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public void deleteFileDefData(file_define fdef) {
    getSession().createQuery("UPDATE file_define SET DELETE_DATE=:delete_date ,DELETE_USER=:delete_user WHERE ID=:id",file_define.class)
                .setParameter("id", fdef.getId())
                .setParameter("delete_date", fdef.getDelete_date())
                .setParameter("delete_user", fdef.getDelete_user())
                .executeUpdate();
  }

  @Override
  public file_define findFileDefData(String fdefId) {
    Query<file_define> query = getSession().createQuery("FROM file_define WHERE ID=:id AND DELETE_DATE IS NULL",file_define.class)
                                           .setParameter("id", fdefId);
    return query.uniqueResult();
  }

  @Override
  public List<file_define> getFileDefData(String configId) {
    List<file_define> fList = getSession().createQuery("FROM file_define WHERE CFG_ID=:id AND DELETE_DATE IS NULL",file_define.class)
                                          .setParameter("id", configId)
                                          .getResultList();
    return fList;
  }

  @Override
  public List<file_define> updateFileDefData(List<file_define> fdef) {
    List<file_define> fList = new ArrayList<>();

    for(int i=0;i<fdef.size();i++){
      file_define fdSetting = fdef.get(i);
      int result = getSession().createQuery("UPDATE FROM file_define SET FILE_DESC=:file_desc, SCHEDULE_MONTH=:schedule_month, SCHEDULE_WEEK =:schedule_week, SCHEDULE_DAY=:schedule_day, SCHEDULE_HOUR=:schedule_hour, SCHEDULE_MIN=:schedule_min, EXTRACT_TYPE=:extract_type, CM_F01=:cm_f01, CM_F02=:cm_f02, UPDATE_USER=:update_user, UPDATE_DATE=:update_date WHERE ID=:id",file_define.class)
                               .setParameter("id", fdSetting.getId())
                               .setParameter("file_desc", fdSetting.getFile_desc())
                               .setParameter("schedule_month", fdSetting.getFile_desc())
                               .setParameter("schedule_week", fdSetting.getFile_desc())
                               .setParameter("schedule_day", fdSetting.getFile_desc())
                               .setParameter("schedule_hour", fdSetting.getFile_desc())
                               .setParameter("schedule_min", fdSetting.getFile_desc())
                               .setParameter("extract_type", fdSetting.getFile_desc())
                               .setParameter("cm_f01", fdSetting.getFile_desc())
                               .setParameter("cm_f02", fdSetting.getFile_desc())
                               .setParameter("update_date", fdSetting.getFile_desc())
                               .setParameter("update_user", fdSetting.getFile_desc())
                               .executeUpdate();
      if(result == 1) fList.add(fdSetting);
    }
    return fList;
  }

}
