package com.chinjja.issue.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mes")
public class MesRestController {
	@PostMapping
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void mes(@RequestBody Map<String, Object> json) {
	}
}
