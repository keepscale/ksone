package org.crossfit.app.web.rest.admin;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/admin/github")
public class GitHubResource {

	private final Logger log = LoggerFactory.getLogger(GitHubResource.class);

    @Value("${github.name}")
	private String repositoryName;

    /**
     * GET  /admin/github/tags -> get tags
     * @throws IOException 
     */
    @RequestMapping(value = "/tags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedIterable<GHRelease>> listTags() throws IOException {
        log.debug("REST request to list github tags");
        GitHub github = GitHub.connectAnonymously();
        GHRepository repository = github.getRepository(repositoryName);
        return ResponseEntity.ok(repository.listReleases());
    }	
    

}
