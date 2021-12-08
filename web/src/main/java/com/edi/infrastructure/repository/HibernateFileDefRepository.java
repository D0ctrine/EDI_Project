package com.edi.infrastructure.repository;

import javax.persistence.EntityManager;

import com.edi.domain.model.settingfile.Setting.fileDefRepository;
import com.edi.domain.model.settingfile.Setting.file_define;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateFileDefRepository extends HibernateSupport<file_define> implements fileDefRepository{
  @Autowired
  public HibernateFileDefRepository(@Qualifier("masterEntityManager") EntityManager entityManager) {
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
  public file_define getFileDefData(String configId) {
    file_define fList = getSession().createQuery("FROM file_define WHERE CFG_ID=:id AND DELETE_DATE IS NULL",file_define.class)
                                          .setParameter("id", configId)
                                          .uniqueResult();
    return fList;
  }

  @Override
  public file_define updateFileDefData(file_define fdef) {
      getSession().createQuery("UPDATE FROM file_define SET FILE_NAME=:fileName,FILE_DESC=:file_desc,FILE_SAVE_FOLDER=:fileSaveFolder,FILE_CHARSET=:fileCharset,FILE_TYPE=:fileType,DATA_TYPE=:dataType"
                            +",FTP_ENV_ID=:ftpEnvId,SCHEDULE_MIN=:schMin,SCHEDULE_HOUR=:schHour,SCHEDULE_DAY=:schDay,SCHEDULE_WEEK=:schWeek,SCHEDULE_MONTH=:schMonth"
                            +",CM_F01=:cm_f01, CM_F02=:cm_f02, UPDATE_USER=:update_user, UPDATE_DATE=:update_date, NODATASEND=:noDataSend WHERE ID=:id")
                               .setParameter("id", fdef.getId())
                               .setParameter("fileName", fdef.getFileName())
                               .setParameter("file_desc", fdef.getFile_desc())
                               .setParameter("fileSaveFolder", fdef.getFileSaveFolder())
                               .setParameter("fileCharset", fdef.getFileCharset())
                               .setParameter("fileType", fdef.getFileType())
                               .setParameter("dataType", fdef.getDataType())
                               .setParameter("ftpEnvId", fdef.getFtpEnvId())
                               .setParameter("schMin", fdef.getScheduleMin())
                               .setParameter("schHour", fdef.getScheduleHour())
                               .setParameter("schDay", fdef.getScheduleDay())
                               .setParameter("schWeek", fdef.getScheduleWeek())
                               .setParameter("schMonth", fdef.getScheduleMonth())
                               .setParameter("cm_f01", fdef.getCm_f01())
                               .setParameter("cm_f02", fdef.getCm_f02())
                               .setParameter("update_date", fdef.getUpdate_date())
                               .setParameter("update_user", fdef.getUpdate_user())
                               .setParameter("noDataSend", fdef.getNoDataSend())
                               .executeUpdate();
    return fdef;
  }

  @Override
  public int updateFileDefSendFlag(String id, String flag) {

    int result = getSession().createQuery("UPDATE FROM file_define SET SEND_FLAG = :sendFlag WHERE ID=:id")
                            .setParameter("sendFlag", flag)
                            .setParameter("id", id)
                            .executeUpdate();
    return result;
  }

}
