package org.crossfit.app;

import static reactor.bus.selector.Selectors.$;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.crossfit.app.event.BookingReceiver;
import org.crossfit.app.event.CheckingCardReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import reactor.bus.EventBus;

@SpringBootApplication
@EnableAutoConfiguration
public class Application implements CommandLineRunner{

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Inject
    private Environment env;

	@Autowired
	private BookingReceiver bookingReceiver;

	@Autowired
	private CheckingCardReceiver checkingCardReceiver;
	
	@Autowired
	private EventBus eventBus;
    
    @Bean
    reactor.Environment env() {
        return reactor.Environment.initializeIfEmpty()
                          .assignErrorJournal();
    }
    
    @Bean
    EventBus createEventBus(reactor.Environment env) {
	    return EventBus.create(env, reactor.Environment.THREAD_POOL);
    }
    
    

    @Override
	public void run(String... arg0) throws Exception {
		eventBus.on($("booking"), bookingReceiver);
		eventBus.on($("checkingcard"), checkingCardReceiver);
	}

	@PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            log.warn("No Spring profile configured, running with default configuration");
        } else {
            log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        }
    }

    /**
     * Main method, used to run the application.
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(Application.class);
        Environment env = app.run(args).getEnvironment();
        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
            "Local: \t\thttp://127.0.0.1:{}\n\t" +
            "External: \thttp://{}:{}\n----------------------------------------------------------",
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"));

    }

}
