package org.crossfit.app.config;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.repository.CrossFitBoxRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class CrossFitBoxSerivceTestConfiguration {
    
    @Inject
    private CrossFitBoxRepository boxRepository;
    
    @Bean
    @Primary
    public CrossFitBoxSerivce crossFitBoxSerivce() {
    	return new CrossFitBoxSerivce(){
			@Override
			public CrossFitBox findCurrentCrossFitBox() {
				return boxRepository.getOne(1L);
			}
    	};
    	
    }
}