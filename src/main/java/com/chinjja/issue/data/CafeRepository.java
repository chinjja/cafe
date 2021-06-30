package com.chinjja.issue.data;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.User;

public interface CafeRepository extends PagingAndSortingRepository<Cafe, String> {
	List<Cafe> findAllByOwner(User owner);
	List<Cafe> findAllByPrivacy(boolean privacy);
	long countByPrivacy(boolean privacy);
}
