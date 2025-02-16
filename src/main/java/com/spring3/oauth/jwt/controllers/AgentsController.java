package com.spring3.oauth.jwt.controllers;

import com.spring3.oauth.jwt.dtos.ApiResponse;
import com.spring3.oauth.jwt.dtos.SubAgentListResponse;
import com.spring3.oauth.jwt.exceptions.UserNotFoundException;
import com.spring3.oauth.jwt.models.UserInfo;
import com.spring3.oauth.jwt.services.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/agent")
public class AgentsController {

    @Autowired
    AgentService agentService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getSubAgentList/{userId}")
    public ResponseEntity<ApiResponse> getSubAgentList(@PathVariable String userId){
        agentService.getSubAgentList(userId);
        int statusCode = 0;
        ApiResponse response = null;
        try {
            statusCode = HttpStatus.OK.value();
            SubAgentListResponse userInfoList = agentService.getSubAgentList(userId);
            response = new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", userInfoList);
        } catch(Exception e) {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE", null);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getAgentProfile/{userId}")
    public  ResponseEntity<ApiResponse> getAgentProfile(@PathVariable String userId) {
        int statusCode = 0;
        ApiResponse response = null;
        try {
            statusCode = HttpStatus.OK.value();
            UserInfo agentProfile = agentService.getAgentProfile(userId);
            response = new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", agentProfile);
        } catch(Exception e) {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE", null);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
    }

    @PostMapping("/updateAgentProfile")
    public  ResponseEntity<ApiResponse> updateAgentProfile(@RequestBody UserInfo userInfo) {
        int statusCode = 0;
        ApiResponse response = null;
        try {
            statusCode = HttpStatus.OK.value();
            UserInfo agentProfile = agentService.updateAgentInfo(userInfo);
            response = new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", agentProfile);
        } catch(Exception e) {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE", null);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
    }
    @PostMapping(value = "/addAgent")
    public ResponseEntity<ApiResponse> saveUser(@RequestBody UserInfo userRequest) {
        ApiResponse apiResponse = null;
        int statusCode = 0;
        Optional<UserInfo> userResponse = null;
        try {
            userResponse = agentService.saveAgent(userRequest);
            statusCode = HttpStatus.OK.value();
            apiResponse = new ApiResponse(statusCode, "Success", userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            apiResponse = new ApiResponse(statusCode, "Failed:"+e, userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deleteAgent/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") String userId){
        int statusCode = 0;
        ApiResponse response = null;
        Boolean isDeleted = agentService.deleteAgent(String.valueOf((userId)));
        if (!isDeleted) {
            statusCode = HttpStatus.NOT_FOUND.value();
            response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "FAILED", null);
            throw new UserNotFoundException("User not found.");
        } else {
            statusCode = HttpStatus.OK.value();
            response = new ApiResponse<>(HttpStatus.OK.value(), "Success", null);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
