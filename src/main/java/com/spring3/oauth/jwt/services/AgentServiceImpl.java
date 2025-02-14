package com.spring3.oauth.jwt.services;

import com.spring3.oauth.jwt.dtos.SubAgentListResponse;
import com.spring3.oauth.jwt.models.UserInfo;
import com.spring3.oauth.jwt.repositories.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AgentServiceImpl implements  AgentService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    AgentRepository agentRepository;

    @Override
    public SubAgentListResponse getSubAgentList(String userId) {
        SubAgentListResponse subAgentListResponse = null;
        List<UserInfo> userInfoList = agentRepository.findSubAgentsByAgentId(userId);
        UserInfo userInfo = agentRepository.findAgentProfileByAgentId(Long.valueOf(userId));
        if (userInfo != null) {
            subAgentListResponse = new SubAgentListResponse();
            subAgentListResponse.setUserInfo(userInfo);
            subAgentListResponse.setSubAgentList(userInfoList);
        }
        return subAgentListResponse;
    }

    @Override
    public UserInfo getAgentProfile(String userId) {
        return agentRepository.findAgentProfileByAgentId(Long.valueOf(userId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Optional<UserInfo> saveAgent(UserInfo user) {
        {
            if(user.getUsername()== null){
                throw new RuntimeException("Parameter account number is not found in request..!!");
            } else if(user.getPassword() == null){
                throw new RuntimeException("Parameter password is not found in request..!!");
            }
            Optional<UserInfo> persitedUser = Optional.of(new UserInfo());
            Optional<UserInfo> savedUser = null;

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = user.getPassword();
            String encodedPassword = encoder.encode(rawPassword);

            user.setUsername(user.getUsername());
            user.setPassword(encodedPassword);
            user.setStatus("Active");
            user.setRoles(null);
             try {
             /*   if (user.getId() > 0) {
                    Optional<UserInfo> oldUser = Optional.ofNullable(agentRepository.findAgentProfileByAgentId(user.getId()));
                    oldUser.get().setCreatedBy(String.valueOf(oldUser.get().getUserId()));
                    if (!oldUser.isEmpty()) {
                        oldUser.get().setId(user.getId());
                        oldUser.get().setPassword(user.getPassword());
                        oldUser.get().setUsername(user.getUsername());
                        oldUser.get().setVerificationCode(user.getVerificationCode());
                        oldUser.get().setUpdatedAt(LocalDateTime.now());
                        oldUser.get().setDeviceType(user.getDeviceType());
                        oldUser.get().setUpdatedBy(String.valueOf(oldUser.get().getUserId()));
                        oldUser.get().setRoles(user.getRoles());
                        savedUser = Optional.of(agentRepository.save(oldUser.get()));
                        savedUser.get().setId(savedUser.get().getUserId());
                       // persitedUser = Optional.ofNullable(agentRepository.findAgentProfileByAgentId(savedUser.getId()));
                    } else {
                        throw new RuntimeException("Can't find record with identifier: " + persitedUser.get().getId());
                    }
                } else {*/
                    user.setCreatedAt(LocalDateTime.now());
                    savedUser = Optional.of(agentRepository.save(user));
               // }
               // persitedUser.get().setUId(String.valueOf(persitedUser.get().getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return savedUser;
        }
    }

    @Override
    public void deleteAgent(String userId) {
        UserInfo userInfo = agentRepository.findAgentProfileByAgentId(Long.valueOf(userId));
        if (userInfo != null) {
            agentRepository.delete(userInfo);
        }
    }

    @Override
    public UserInfo updateAgentInfo(UserInfo user) {
        Optional<UserInfo> persitedUser = Optional.of(new UserInfo());
        UserInfo savedUser = null;

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = user.getPassword();
        String encodedPassword = encoder.encode(rawPassword);

        user.setUsername(user.getUsername());
        user.setPassword(encodedPassword);
        user.setStatus("Active");
        UserInfo existingUser = agentRepository.findAgentProfileByAgentId((long) user.getUserId());
        if (existingUser != null) {
            existingUser.setCreatedBy(String.valueOf(existingUser.getUserId()));
            //existingUser.setId(user.getId());
            existingUser.setPassword(user.getPassword());
            existingUser.setUsername(user.getUsername());
            existingUser.setVerificationCode(user.getVerificationCode());
            existingUser.setUpdatedAt(LocalDateTime.now());
            existingUser.setDeviceType(user.getDeviceType());
            existingUser.setUpdatedBy(String.valueOf(existingUser.getUserId()));
            existingUser.setRoles(null);
            savedUser = agentRepository.save(existingUser);
        }
return savedUser;
    }

    public List<UserInfo> getSubAgentListFromUserService(String parentId, String jwtToken) {
        String url = "http://localhost:8083/api/v1/user/getUserByAgentId/{parentId}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken); // Add JWT token
        headers.setContentType(MediaType.APPLICATION_JSON); // Set Content-Type

        HttpEntity<Void> entity = new HttpEntity<>(headers); // Wrap headers in HttpEntity
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("parentId", parentId);
        ResponseEntity<List<UserInfo>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<UserInfo>>() {},
                uriVariables
        );
        return response.getBody();
    }

    public UserInfo getAgentFromUserService(String userId, String jwtToken) {
        String url = "http://localhost:8083/api/v1/user/getAgentProfileByAgentId/{userId}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken); // Add JWT token
        headers.setContentType(MediaType.APPLICATION_JSON); // Set Content-Type

        HttpEntity<Void> entity = new HttpEntity<>(headers); // Wrap headers in HttpEntity
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("userId", userId);
        ResponseEntity<UserInfo> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UserInfo.class,
                uriVariables
        );
        return response.getBody();
    }
}
