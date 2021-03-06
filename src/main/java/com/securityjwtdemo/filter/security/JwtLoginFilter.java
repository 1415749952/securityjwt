package com.securityjwtdemo.filter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityjwtdemo.entity.LoginBody;
import com.securityjwtdemo.entity.security.JwtLoginToken;
import com.securityjwtdemo.exception.LoginInvalidationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author : KingFish
 * @Email : XXXXXX
 * @Data: 2019/10/30
 * @Des: 用户登录验证拦截器 --  执行顺序在UsernamePasswordAuthenticationFilter 拦截器后
 */

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter
{

    /**
     * 拦截逻辑
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
    {
        LoginBody loginBody = null;
        try
        {
            InputStream inputStream = request.getInputStream();
            loginBody = new ObjectMapper().readValue(inputStream, LoginBody.class);
        }
        catch (IOException e)
        {
            throw new LoginInvalidationException(e.getMessage());
        }
        //创建未认证的凭证(etAuthenticated(false)),注意此时凭证中的主体principal为用户名
        JwtLoginToken jwtLoginToken = new JwtLoginToken(loginBody.getUsername(), loginBody.getPassword());
        //将认证详情(ip,sessionId)写到凭证
        jwtLoginToken.setDetails(new WebAuthenticationDetails(request));
        //AuthenticationManager获取受支持的AuthenticationProvider(这里也就是JwtAuthenticationProvider),
        //生成已认证的凭证,此时凭证中的主体为userDetails  --- 这里会委托给AuthenticationProvider实现类来验证
        // 即 跳转到 JwtAuthenticationProvider.authenticate 方法中认证
        Authentication authenticatedToken = this.getAuthenticationManager().authenticate(jwtLoginToken);
        return authenticatedToken;
    }
}