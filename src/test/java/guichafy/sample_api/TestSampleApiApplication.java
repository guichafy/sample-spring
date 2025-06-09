package guichafy.sample_api;

import org.springframework.boot.SpringApplication;

public class TestSampleApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SampleApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
