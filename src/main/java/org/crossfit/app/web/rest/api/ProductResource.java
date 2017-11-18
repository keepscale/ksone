package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Product;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.ProductService;
import org.crossfit.app.web.rest.errors.CustomParameterizedException;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Product.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

	private final Logger log = LoggerFactory.getLogger(ProductResource.class);

	@Inject
    private CrossFitBoxSerivce boxService;
	
    @Inject
    private ProductService productService;
	/**
	 * POST /products -> Create a new product.
	 */
	@RequestMapping(value = "/products", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> create(@Valid @RequestBody Product product)
			throws URISyntaxException {
		log.debug("REST request to save Product : {}", product);
		if (product.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new product cannot already have an ID")
					.body(null);
		}
		Product result = productService.doSave(product);
		return ResponseEntity.created(new URI("/api/products/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("product", result.getId().toString()))
				.body(result);
	}

	/**
	 * PUT /products -> Updates an existing product.
	 */
	@RequestMapping(value = "/products", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> update(@Valid @RequestBody Product product)
			throws URISyntaxException {
		log.debug("REST request to update Product : {}", product);
		if (product.getId() == null) {
			return create(product);
		}
		Product result = productService.doSave(product);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("product", product.getId().toString()))
				.body(result);
	}

	/**
	 * GET /products -> get all the products.
	 */
	@RequestMapping(value = "/products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> getAll() {
		log.debug("REST request to get all products");
		return productService.findAll(boxService.findCurrentCrossFitBox());
	}

	/**
	 * GET /products/:id -> get the "id" product.
	 */
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Product> get(@PathVariable Long id) {
		log.debug("REST request to get Product : {}", id);
		return Optional.ofNullable(doGet(id))
				.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	protected Product doGet(Long id) {
		return productService.findOne(id, boxService.findCurrentCrossFitBox());
	}

	/**
	 * DELETE /products/:id -> delete the "id" product.
	 */
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Product : {}", id);

		
		Product product = doGet(id);
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		if (product.getBox().equals(box)){
			productService.delete(product, box);
		}
		else{
			ResponseEntity.status(HttpStatus.FORBIDDEN);
		}
		
				
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("product", id.toString()))
				.build();
	}
}
