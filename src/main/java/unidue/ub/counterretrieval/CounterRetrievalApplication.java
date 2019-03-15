package unidue.ub.counterretrieval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@SpringBootApplication
@EnableEurekaClient
public class CounterRetrievalApplication  extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(CounterRetrievalApplication.class, args);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable().csrf().disable();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/ebookcounter/**").permitAll().and()
			.authorizeRequests()
				.anyRequest().hasIpAddress("127.0.0.1").anyRequest().permitAll().and()
				.authorizeRequests()
				.anyRequest().authenticated().anyRequest().permitAll();
	}
}
