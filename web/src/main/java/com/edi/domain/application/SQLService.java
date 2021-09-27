package com.edi.domain.application;

import java.util.List;
import java.util.Map;

import com.edi.web.payload.SelectSQLPayload;

public interface SQLService {
  List<Map<String, Object>> getReportQuery(SelectSQLPayload payload);
  List<Map<String, Object>> getMESQuery(SelectSQLPayload payload);
  List<Map<String, Object>> getComsQuery(SelectSQLPayload payload);
}
