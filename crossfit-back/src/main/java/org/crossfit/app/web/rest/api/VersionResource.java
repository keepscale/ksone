package org.crossfit.app.web.rest.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jcabi.manifests.Manifests;


@RestController
@RequestMapping("/api")
public class VersionResource {

    private static final String VERSION = Manifests.read("Implementation-Version");

	@RequestMapping(value = "/version",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String isAuthenticated(HttpServletRequest request) {
        return VERSION;
    }


}
