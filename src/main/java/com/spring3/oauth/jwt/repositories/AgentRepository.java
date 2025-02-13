package com.spring3.oauth.jwt.repositories;

import com.spring3.oauth.jwt.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<UserInfo, Long> {

    @Query(value = "SELECT * FROM USERS WHERE parent_id = :parentId", nativeQuery = true)
    public List<UserInfo> findSubAgentsByAgentId(@Param("parentId") String parentId);

    @Query(value = "SELECT * FROM USERS WHERE user_id = :userId", nativeQuery = true)
    public UserInfo findAgentProfileByAgentId(@Param("userId") Long parentId);
}
