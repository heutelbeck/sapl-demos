package io.sapl.ethereum.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringBootApplication
@Theme(value = "sapldemoethereum", variant = Lumo.DARK)
public class EthereumDemoApplication implements AppShellConfigurator {
	private static final long serialVersionUID = -6789027976268546698L;

	public static void main(String[] args) {
		SpringApplication.run(EthereumDemoApplication.class, args);
	}

	@Bean
	Web3j web3j() {
		return Web3j.build(new HttpService("http://localhost:7545"));
	}

}
