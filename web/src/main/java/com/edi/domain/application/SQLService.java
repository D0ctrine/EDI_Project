package com.edi.domain.application;

import java.util.List;

public interface SQLService {
  List getReportQuery(String query);
  List getMESQuery(String query);
  List getComsQuery(String query);
}
