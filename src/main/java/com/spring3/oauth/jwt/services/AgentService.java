package com.spring3.oauth.jwt.services;


import com.spring3.oauth.jwt.dtos.ApiResponse;
import com.spring3.oauth.jwt.dtos.SubAgentListResponse;
import com.spring3.oauth.jwt.models.UserInfo;
import org.springframework.http.ResponseEntity;

public interface AgentService  {
    SubAgentListResponse getSubAgentList(String userId);
    UserInfo saveAgent(UserInfo userInfo);
}
