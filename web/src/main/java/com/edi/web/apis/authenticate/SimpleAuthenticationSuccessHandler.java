package com.edi.web.apis.authenticate;

import com.edi.web.results.ApiResult;
import com.edi.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
    response.setStatus(HttpStatus.OK.value());
    request.getSession().setMaxInactiveInterval(60*60);
    JsonUtils.write(response.getWriter(), ApiResult.message("authenticated"));
  }
}
