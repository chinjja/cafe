package com.chinjja.issue.data;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.CafeMemberId;

public interface CafeMemberRepository extends CrudRepository<CafeMember, CafeMemberId> {
}
