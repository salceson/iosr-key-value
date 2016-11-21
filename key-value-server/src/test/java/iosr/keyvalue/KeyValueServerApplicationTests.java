package iosr.keyvalue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "server=localhost:9000")
public class KeyValueServerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
