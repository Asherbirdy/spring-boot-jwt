package com.app.security.service.impl;

import com.app.security.dao.MemberDao;
import com.app.security.model.Member;
import com.app.security.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public Map<String, Object> showMemberInfo(String memberId) {
        Member member = memberDao.getMemberById(memberId);

        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "NON_EXISTENT_MEMBER");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("memberId", member.getMemberId());
        result.put("name", member.getName());
        result.put("email", member.getEmail());
        result.put("role", member.getRole());
        result.put("createdAt", member.getCreatedAt());
        result.put("updatedAt", member.getUpdatedAt());

        return result;
    }
}
