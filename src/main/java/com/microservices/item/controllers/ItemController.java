package com.microservices.item.controllers;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.microservices.item.models.Item;
import com.microservices.item.models.ProductDTO;
import com.microservices.item.services.ItemService;

@RestController
public class ItemController {
	
	private final Logger logger = LoggerFactory.getLogger(ItemController.class);	
	private final CircuitBreakerFactory circuitBreakerFactory;	
	private final ItemService itemService;
	private final Random random = new Random();
	
	/**
	 * This constructor initiates {@Link ItemController} with the specific implementation
	 * @param circuitBreakerFactory		the {@link CircuitBreakerFactory} used to create and configure circuit breakers for handling service failures.
	 * @param itemService				the implementation of {@link ItemService} injected 
	 */
	public ItemController(CircuitBreakerFactory circuitBreakerFactory, @Qualifier("itemServiceImplWebClient") ItemService itemService) {
		this.circuitBreakerFactory = circuitBreakerFactory;
		this.itemService = itemService;
	}
	
	/**
	 * Retrieves a list of items
	 * @return 			a list of {@link Item} available
	 */
	@GetMapping("/items")
	public List<Item> findAll(){
		return itemService.findAll();
	}
	
	/**
	 * Retrieves an {@link Item] by its ID
	 * @param id		the ID of the {@link Item} to be retrieved
	 * @return 			the {@Link ResponseEntity} containing the {@link Item} corresponding to the specified ID
	 */
	@GetMapping("/items/{id}")
	public ResponseEntity<Item> findById(@PathVariable Long id) {
		return circuitBreakerFactory.create("items").run(() -> ResponseEntity.ok(itemService.findById(id).get()), e -> metodoAlternativo(id, e));
	}
	
	/**
	 * Alternative method that handles fallback logic when an error occurs during the original request
	 * @param id		the ID of the {@link ProductDTO} to be sent in the returned {@link Item}
	 * @param throwable	the {@link Throwable} that triggered this fallback method, its message is logged
	 * @return			an {ResponseEntity Item} containing the {@link Item} containing the default {@link ProductDTO} with the fixed data
	 */
	public ResponseEntity<Item> metodoAlternativo(Long id, Throwable throwable) {
		logger.info(throwable.getMessage());
		Item item = new Item();
		ProductDTO product = new ProductDTO();
		product.setId(id);
		product.setName("Camara Sony [Metodo Alternativo]");
		product.setPrice(500.00);
		item.setProductDTO(product);
		item.setQuantity(random.nextInt(10)+1);
		return ResponseEntity.ok(item);
	}
}
