package com.scuop.userauthservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserauthserviceApplicationTests {

	@Test
	void contextLoads() {
		long x = 1000000000;
		long y = 1000000000;
		Long z = (long) 1000000000;
		System.out.println(x == y);
		System.out.println(z == x);
		System.out.println(x == z);
	}

}
