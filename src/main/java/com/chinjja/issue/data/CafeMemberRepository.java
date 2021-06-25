package com.chinjja.issue.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.CafeMemberId;

public interface CafeMemberRepository extends CrudRepository<CafeMember, CafeMemberId> {
	List<CafeMember> findByIdCafeAndApproved(Cafe cafe, boolean approved);
}
