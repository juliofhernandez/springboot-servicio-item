package com.microservices.item.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservices.item.models.Item;
import com.microservices.item.models.ProductDTO;

@Service
public class ItemServiceImplRestTemplate implements ItemService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * Retrieves a list of all {@link Item} objects.
	 * 
	 * @return a {@link List} of {@link Item} objects, each containing a {@link ProductDTO} and a random quantity.
	 */
	@Override
	public List<Item> findAll() {
//		List<Product> products = Arrays.asList(restTemplate.getForObject("http://localhost:8001/products", Product[].class));
		List<ProductDTO> products = Arrays.asList(restTemplate.getForObject("http://service-products/products", ProductDTO[].class));
		return products.stream().map(product -> new Item(product, new Random().nextInt(10)+1)).collect(Collectors.toList());
	}

	/**
	 * Retrieves an {@link Item} by its ID.
	 * 
	 * @param id the ID of the {@link ProductDTO} to be retrieved.
	 * 
	 * @return an {@link Optional} containing the {@link Item} if the product was found, or an empty {@link Optional} if the product does not exist.
	 */
	@Override
	public Optional<Item> findById(Long id) {
		Map<String, String> pathVariables = new HashMap<String,String>();
		pathVariables.put("id", id.toString());
//		Product product = restTemplate.getForObject("http://localhost:8001/products/{id}", Product.class,pathVariables);
		ProductDTO productDTO = restTemplate.getForObject("http://service-products/products/{id}", ProductDTO.class, pathVariables);
		return Optional.of(new Item(productDTO, new Random().nextInt(10)+1));	
	}

	@Override
	public Optional<Item> save(Item item) {
		return Optional.empty();
	}

	@Override
	public void deleteById(Long id) {

	}

	@Override
	public Optional<Item> update(Long id, Item item) {
		return Optional.empty();
	}

}
