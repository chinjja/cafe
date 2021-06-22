package com.chinjja.issue.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Cafe;

public interface CafeRepository extends PagingAndSortingRepository<Cafe, String> {
	
}
