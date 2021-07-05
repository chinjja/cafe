package com.chinjja.issue.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;

public interface CafeMemberRepository extends CrudRepository<CafeMember, CafeMember.Id> {
	List<CafeMember> findByIdCafeAndLevelGreaterThan(Cafe cafe, int level);
	List<CafeMember> findByIdCafeAndLevelLessThan(Cafe cafe, int level);
	List<CafeMember> findByIdCafeAndLevel(Cafe cafe, int level);
}
