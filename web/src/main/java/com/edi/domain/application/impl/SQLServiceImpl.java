package com.edi.domain.application.impl;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.edi.domain.application.SQLService;
import com.edi.infrastructure.SecondRepository.HibernateComsRepository;
import com.edi.infrastructure.SecondRepository.HibernateMESRepository;
import com.edi.infrastructure.SecondRepository.HibernateReportRepository;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class SQLServiceImpl implements SQLService{
  private HibernateReportRepository reportRepository;
  private HibernateMESRepository mesRepository;
  private HibernateComsRepository comsRepository;

  public SQLServiceImpl(HibernateReportRepository reportRepository, HibernateMESRepository mesRepository,HibernateComsRepository comsRepository) {
    this.reportRepository = reportRepository;
    this.mesRepository = mesRepository;
    this.comsRepository = comsRepository;
  }
  @Override
  public List<Map<String, Object>> getReportQuery(String query) {
    return reportRepository.selectQuery(query);
  }

  @Override
  public List<Map<String, Object>> getMESQuery(String query) {
    return mesRepository.selectQuery(query);
  }

  @Override
  public List<Map<String, Object>> getComsQuery(String query) {
    return comsRepository.selectQuery(query);
  }

}
