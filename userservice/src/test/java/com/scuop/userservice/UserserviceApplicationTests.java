package com.scuop.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserserviceApplicationTests {

	@Test
	void contextLoads() {
		Long x = (long) 1000000000;
		Long y = (long) 1000000000;
		System.out.println(x == y);
		System.out.println(x.equals(y));
	}

}
