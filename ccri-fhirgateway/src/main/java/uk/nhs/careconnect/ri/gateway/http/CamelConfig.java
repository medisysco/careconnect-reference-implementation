package uk.nhs.careconnect.ri.gateway.http;



import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContextNameStrategy;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@Configuration
@ComponentScan
public class CamelConfig extends CamelConfiguration {


	@Override
	protected void setupCamelContext(CamelContext camelContext) throws Exception {

		camelContext.setNameStrategy(new DefaultCamelContextNameStrategy("Gateway"));

	}



}
