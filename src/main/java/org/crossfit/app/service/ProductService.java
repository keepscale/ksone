package org.crossfit.app.service;

import java.util.List;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Product;
import org.crossfit.app.exception.NameAlreadyUseException;
import org.crossfit.app.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Inject
    private CrossFitBoxSerivce boxService;

	@Inject
	private ProductRepository productRepository;


	public Product doSave(Product product) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		Product productInDataBase = productRepository.findOneByNameAndBox(product.getName(), box);
		
		if (productInDataBase != null && product.getId() == null){
			throw new NameAlreadyUseException(product.getName());
		}

		product.setBox(box);

		return productRepository.save(product);
	}


	public List<Product> findAll(CrossFitBox box) {
		return productRepository.findAll(box);
	}


	public Product findOne(Long id, CrossFitBox box) {
		return productRepository.findOneByIdAndBox(id, box);
	}


	public void delete(Product product, CrossFitBox box) {
		productRepository.delete(product.getId(), box);
	}
}
