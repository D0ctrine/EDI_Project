package com.edi.domain.application.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.edi.domain.application.HeadnTailService;
import com.edi.domain.application.commands.setting.headntail.CreateHnTCommand;
import com.edi.domain.application.commands.setting.headntail.UpdateHnTCommand;
import com.edi.domain.model.settingfile.headntail.headntail;
import com.edi.domain.model.settingfile.headntail.hntRepository;
import com.edi.domain.model.user.UserId;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class HeadnTailServiceImpl implements HeadnTailService{
  private hntRepository hRepository;

  public HeadnTailServiceImpl(hntRepository hRepository) {
    this.hRepository = hRepository;
  }

  @Override
  public List<headntail> create(List<CreateHnTCommand> ec) {
    List<headntail> hntList = new ArrayList<headntail>();
    for(int i=0;i<ec.size();i++){
      headntail hDef = headntail.create(ec.get(i).getUserId(), ec.get(i).getCfgId(), ec.get(i).getDataType(), ec.get(i).getNote(), ec.get(i).getValue());
      hRepository.save(hDef);
      hntList.add(hDef);
    }
    return hntList;
  }

  @Override
  public Boolean createCopy(List<headntail> ec, UserId userid, String cfgId) {
    try {
      for(int i=0;i<ec.size();i++){
        headntail hDef = headntail.create(userid, cfgId, ec.get(i).getData_type(), ec.get(i).getNote(), ec.get(i).getValue());
        hRepository.save(hDef);
      }
      return true;
    } catch (Exception e) {
      new Error(e.getMessage());
    }
    return false;
  }

  @Override
  public headntail delete(UpdateHnTCommand htc) {
    headntail hnt = headntail.update(htc.getId(), htc.getUserId(), htc.getCfgId(), htc.getDataType(), htc.getNote(),htc.getValue());
    hRepository.deleteHnTData(hnt);
    return hnt;
  }

  @Override
  public List<headntail> getList(String cfgId) {
    List<headntail> hntList = hRepository.getHnTData(cfgId);
    return hntList;
  }

  @Override
  public List<headntail> update(List<UpdateHnTCommand> htc) {
    List<headntail> hList = new ArrayList<headntail>();
    for(int i=0;i<htc.size();i++){
      headntail hnt = headntail.update(htc.get(i).getId(), htc.get(i).getUserId(), htc.get(i).getCfgId(), htc.get(i).getDataType(), htc.get(i).getNote(), htc.get(i).getValue());
      hList.add(hnt);
    }
    hRepository.updateHnTData(hList);
    return hList;
  }

}
