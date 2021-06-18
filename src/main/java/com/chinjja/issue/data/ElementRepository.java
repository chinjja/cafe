package com.chinjja.issue.data;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.chinjja.issue.domain.Element;

public interface ElementRepository extends PagingAndSortingRepository<Element, Long> {

}
