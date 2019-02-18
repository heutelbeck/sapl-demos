package org.demo;

import org.demo.security.VaadinSessionSecurityContextHolderStrategy;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DemoApplicationTest {

	@BeforeClass
	public static void setUp() {
		VaadinSessionSecurityContextHolderStrategy.initForUnitTest();
	}

	@Test
	public void contextLoads() {
	}

}
