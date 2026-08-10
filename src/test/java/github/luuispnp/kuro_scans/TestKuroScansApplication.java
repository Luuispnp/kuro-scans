package github.luuispnp.kuro_scans;

import org.springframework.boot.SpringApplication;

public class TestKuroScansApplication {

	public static void main(String[] args) {
		SpringApplication.from(KuroScansApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
