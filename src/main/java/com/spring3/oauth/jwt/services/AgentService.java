package com.spring3.oauth.jwt.services;


import com.spring3.oauth.jwt.dtos.SubAgentListResponse;
import com.spring3.oauth.jwt.models.UserInfo;

import java.util.Optional;

public interface AgentService  {
    SubAgentListResponse getSubAgentList(String userId);
    UserInfo getAgentProfile(String userId);
    Optional<UserInfo> saveAgent(UserInfo userInfo);

    void deleteAgent(String  userId);
    UserInfo updateAgentInfo(UserInfo userInfo);
}
