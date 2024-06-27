/*
 * author: Vitalik Scherbatyuk
 * version: 1
 * developing social network for portfolio
 * 01.01.2024
 */

package scherbatyuk.network;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "scherbatyuk.network")
public class NetworkOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkOnlineApplication.class, args);
	}

}
