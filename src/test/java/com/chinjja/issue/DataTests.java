package com.chinjja.issue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = {
		"spring.jpa.properties.hibernate.show_sql=false",
		"spring.jpa.properties.hibernate.format_sql=true"
})
@ActiveProfiles("test")
class DataTests {
	@Test
	public void context() {
	}
}
