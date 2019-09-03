package org.crossfit.app.service.box;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.crossfit.app.config.CacheConfiguration;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.exception.CrossFitBoxConfiguration;
import org.crossfit.app.repository.CrossFitBoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrossFitBoxResolverService {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxResolverService.class);

    private static final Set<String> KNOW_HOSTS = new HashSet<String>(Arrays.asList("(.*).rhcloud.com", "(.*).localhost", "127.0.0.1"));
    
	@Autowired
	private CrossFitBoxRepository crossFitBoxRepository;
	
	
	@Cacheable(CacheConfiguration.BOX_CACHE_NAME)
	public CrossFitBox findCrossFitBoxByServername(String servername){

		log.trace("Recherche d'une box associé à {}", servername);
		
		List<CrossFitBox> boxs = crossFitBoxRepository.findAll();
		
		Optional<CrossFitBox> box = boxs.stream().filter(b->{
			try{
				return Pattern.matches(b.getWebsitepattern(), servername);
			}
			catch (Exception e) {
				log.error("Erreur sur le pattern {} de la box {}: {}", b.getWebsitepattern(), b.getId(), e.getMessage());
				return false;
			}
		}).findFirst();
		
		//On a pas trouve la box avec le servername, alors qu'il y a des box ?
		if (!box.isPresent() && boxs.size() > 0 ){
			log.warn("Aucune box n'est recensée à l'adresse "+ servername);
			if (boxs.size() == 1){
				log.trace("Une seule box dans la base, on l'utilise");
				box = Optional.ofNullable(boxs.get(0));
			}
		}

		log.trace("Current CorssFitBox: {}", box.isPresent() ? box.get().getName() + " ("+box.get().getWebsitepattern()+")" : "null");
		
		
		if (!box.isPresent()){

			boolean matchKnowHost = KNOW_HOSTS.stream().anyMatch(knowHost->{return Pattern.matches(knowHost, servername);});

			if (matchKnowHost){
				log.warn("Parmis {}, aucune box n'a ete trouve, par contre le serveur {} fait parti des serveurs de confiance {}, donc on laisse passer.", boxs.stream().map(CrossFitBox::getWebsitepattern).collect(Collectors.toList()), servername, KNOW_HOSTS);
			}
			else{
				throw new CrossFitBoxConfiguration("Aucune box n'est recensée à l'adresse "+ servername);
			}
		}
		
		
		
		return box.orElseGet(null);
	}
}
