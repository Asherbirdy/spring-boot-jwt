package com.app.security.dao;

import com.app.security.model.Member;

public interface MemberDao {

    Member getMemberByEmail(String email);

    Member getMemberById(String memberId);

    String createMember(Member member);

    void updateRole(String memberId, String role);
}
