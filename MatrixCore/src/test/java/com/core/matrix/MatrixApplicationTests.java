package com.core.matrix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class MatrixApplicationTests {

	@Test
	void contextLoads() {
            
            System.out.println("password-> " + new BCryptPasswordEncoder().encode("123456"));
            
            
            
            
	}

}
