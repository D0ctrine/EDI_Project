package com.edi.domain.application;

import java.util.List;
import java.util.Map;

public interface SQLService {
  List<Map<String, Object>> getReportQuery(String query);
  List<Map<String, Object>> getMESQuery(String query);
  List<Map<String, Object>> getComsQuery(String query);
}
