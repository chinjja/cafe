package com.chinjja.issue.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Likable;

public interface LikableRepository extends PagingAndSortingRepository<Likable, Long> {

}
