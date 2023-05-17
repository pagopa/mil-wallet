package it.pagopa.swclient.mil.wallet.it.resource;

import java.util.Iterator;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.google.common.collect.ImmutableMap;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
public class Initializer implements QuarkusTestResourceLifecycleManager,DevServicesContext.ContextAware {
    
	private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private static final String WIREMOCK_NETWORK_ALIAS = "wiremock-it";

    private GenericContainer<?> wiremockContainer;

    private DevServicesContext devServicesContext;
    
	@Override
	public void setIntegrationTestContext(DevServicesContext devServicesContext){
		  this.devServicesContext = devServicesContext;
	}
	
	
	@Override
	public Map<String, String> start() {
		
		 // create a "fake" network using the same id as the one that will be used by Quarkus
        // using the network is the only way to make the withNetworkAliases work
        logger.info("devServicesContext.containerNetworkId() -> " + devServicesContext.containerNetworkId());
        Network testNetwork = new Network() {
            @Override
            public String getId() {
                return devServicesContext.containerNetworkId().get();
            }

            @Override
            public void close() {

            }

            @Override
            public Statement apply(Statement statement, Description description) {
                return null;
            }
        };

        wiremockContainer = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:latest"))
                .withNetwork(testNetwork)
                .withNetworkAliases(WIREMOCK_NETWORK_ALIAS)
                //.withNetworkMode(devServicesContext.containerNetworkId().get())
                .waitingFor(Wait.forListeningPort());

        wiremockContainer.withLogConsumer(new Slf4jLogConsumer(logger));

        wiremockContainer.withFileSystemBind("./src/test/resources/wiremockIT", "/home/wiremock");
        wiremockContainer.setCommand("--verbose");
        wiremockContainer.start();

        // Pass the configuration to the application under test
		
		Map<String, String> map = ImmutableMap.of(
	                "quarkus.log.level", "DEBUG",
	                "wallet.app-log-level", "DEBUG",
	                "ocp-apim-subscription","bbc27c4c66a64effa786cf7e5dad3a6f",
	                "quarkus.log.category.\"it.pagopa.swclient.mil.wallet\".level", "DEBUG",
	                "rest-client-session-api-url","http://" + WIREMOCK_NETWORK_ALIAS + ":" + 8080,
	                "rest-client-pmwallet-api-url","http://" + WIREMOCK_NETWORK_ALIAS + ":" + 8080
	        );
		
        		

		 
		 
		Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();
		 
		while (itr.hasNext()) {
		    System.out.println(itr.next());
		}
		return map;
	}

	@Override
	public void stop() {
		if (null != wiremockContainer) {
			wiremockContainer.stop();
		}
		
	}
	
}
