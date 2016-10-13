package org.crossfit.app.web.rest.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.web.rest.admin.dto.Release;
import org.crossfit.app.web.rest.errors.CustomParameterizedException;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/admin/github")
public class GitHubResource {

	private static final String CROSSFIT_APP_VERSION_ENV_NAME = "CROSSFIT_APP_VERSION";

	private final Logger log = LoggerFactory.getLogger(GitHubResource.class);

    @Value("${github.name}")
	private String repositoryName;

    @Value("${github.download.directory}")
	private String githubDownloadDirectory;
    
    @Value("${github.users.vars.path:''}")
	private String usersVarsPath;

    /**
     * GET  /admin/github/tags -> get tags
     * @throws IOException 
     */
    @RequestMapping(value = "/release",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Release>> listReleases() throws IOException {
        log.debug("REST request to list github releases");
        List<Release> listReleases = doListRelease();
		return ResponseEntity.ok(listReleases);
    }

	private List<Release> doListRelease() throws IOException {
		GitHub github = GitHub.connectAnonymously();
        GHRepository repository = github.getRepository(repositoryName);
        List<Release> listReleases = repository.listReleases().asList().stream().map(release->{
        	try {
        		Optional<GHAsset> asset = release.getAssets().stream().findFirst();
				return new Release(release.getTagName(), asset.map(GHAsset::getName).orElse("null"), asset.map(GHAsset::getBrowserDownloadUrl).orElse("null"));
			} catch (IOException e) {
				return null;
			}
        }).collect(Collectors.toList());
		return listReleases;
	}	

    @RequestMapping(value = "/release/{tagname}/deploy",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deployRelease(@PathVariable("tagname")String tagName) throws IOException {
    	
    	Release release = doListRelease().stream()
    			.filter(r->{return r.getTagName().equals(tagName);})
    			.findFirst().orElseThrow(()->{return new CustomParameterizedException("Tag inconnu");});
    	
		log.debug("REST request to deploy release {}", release);

        Path jarPath = Paths.get(githubDownloadDirectory).resolve(release.getJarName());
        log.info("Suppression de {}", jarPath);
        Files.deleteIfExists(jarPath);
        Files.createDirectories(jarPath.getParent());
    	
        
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(
                new ByteArrayHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        log.info("Telechargement de {} ...", release.getDownloadUrl());
        ResponseEntity<byte[]> response = restTemplate.exchange(
                release.getDownloadUrl(),
                HttpMethod.GET, entity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
        	log.info("Telechargement OK vers {}", jarPath);
			Files.write(jarPath, response.getBody());
			
			if (StringUtils.isNotEmpty(usersVarsPath)){
				log.info("Mise à jour de la version à utiliser {}", release.getTagName());
				List<String> lines = Arrays.asList(release.getTagName());
				Path appVersionFilePath = Paths.get(usersVarsPath, CROSSFIT_APP_VERSION_ENV_NAME);
				Files.deleteIfExists(appVersionFilePath);
		        Files.createDirectories(appVersionFilePath.getParent());
				Files.write(appVersionFilePath, lines);
			}
			return ResponseEntity.ok("L'application peut être redémarrer");
        }
        else{
        	log.warn("Echec du telechargement de {}: {}", release.getDownloadUrl(), response.getStatusCode());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eche du telechargement");
        }
        
    }	
    
    

}
