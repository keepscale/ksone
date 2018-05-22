package org.crossfit.app.web.rest.admin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.VersionJar;
import org.crossfit.app.repository.VersionJarRepository;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/admin/version")
public class VersionJarResource {

	
	@Inject
	private VersionJarRepository versionJarRepository;

	private final Logger log = LoggerFactory.getLogger(VersionJarResource.class);

    /**
     * GET  /admin/version -> get all version
     * @throws IOException 
     */
    @RequestMapping(value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VersionJar>> listVersion() throws IOException {
        log.debug("REST request to list versions");
       
		return ResponseEntity.ok(versionJarRepository.findAll());
    }

    /**
     * POST  /admin/version/activate -> active the version sended
     * @throws IOException 
     */
    @RequestMapping(value = "/activate",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> activateVersion(@RequestBody VersionJar version) throws IOException {
    	
        log.debug("REST request to activate version {}", version);

        
        Optional<VersionJar> actualVersion = versionJarRepository.findOneActif();
        
        actualVersion.ifPresent(v->{
            v.setActif(false);
    		versionJarRepository.save(v);
        });
        
		version.setActif(true);
		versionJarRepository.save(version);
    	
		return ResponseEntity.ok().build();
    }	
    
	/**
	 * POST /admin/version -> Create a new version.
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VersionJar> create(@Valid @RequestBody VersionJar versionJar) throws URISyntaxException {
		log.debug("REST request to save VersionJar : {}", versionJar);
		
		VersionJar result = versionJarRepository.save(versionJar);
		return ResponseEntity.created(new URI("/admin/version/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("versionJar", result.getId().toString())).body(result);
	}
	

	/**
	 * DELETE /admin/version/:id -> delete the "id" versionJar.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete VersionJar : {}", id);
		versionJarRepository.deleteById(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("versionJar", id.toString())).build();
	}
    

}
