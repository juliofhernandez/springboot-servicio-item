package com.microservices.item.controllers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
	public List<Item> findAll(@RequestParam(name="paramRequestItems", required=false) String paramRequestItems, @RequestHeader(name = "X-Header-Request-Items", required = false) String headerRequestItems) {
		logger.info("Filtros Gateway Factory de fábrica:");
		logger.info("paramRequestItems: " + paramRequestItems);
		logger.info("X-Header-Request-Items: " + headerRequestItems);
		return itemService.findAll();
	}

	/**
	 * Retrieves an Item by its ID asynchronously.
	 * This method utilizes the @CircuitBreaker annotation to handle faults and the @TimeLimiter annotation to enforce a time limit on execution.
	 * - @CircuitBreaker uses the configuration specified in the application.yml file.
	 *   If an error occurs during the retrieval process, the fallback method metodoAlternativo is executed.
	 * - @TimeLimiter enforces a timeout as defined in the application.yml file.
	 *   If the execution exceeds the configured limit, a TimeoutException is thrown.
	 * @param id the ID of the Item to be retrieved
	 * @return a CompletableFuture containing a ResponseEntity with the retrieved Item, or an appropriate fallback response if a fault or timeout occurs
	 */
	@GetMapping("/items/{id}")
	@CircuitBreaker(name = "items",fallbackMethod = "metodoAlternativo")
	@TimeLimiter(name = "items")
	public CompletableFuture<?> findById(@PathVariable Long id) {
		return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(itemService.findById(id).get()));
	}

	/**
	 * Alternative method that handles fallback logic when an error occurs during the original request.
	 * This method is triggered by the @CircuitBreaker fallback mechanism.
	 *
	 * @param id the ID of the ProductDTO to be included in the returned Item
	 * @param throwable the Throwable that triggered this fallback method; its message is logged for debugging purposes
	 * @return a CompletableFuture containing a ResponseEntity with the default Item, which includes a ProductDTO with predefined data
	 */
	public CompletableFuture<?> metodoAlternativo(Long id, Throwable throwable) {
		logger.info(throwable.getMessage());
		Item item = new Item();
		ProductDTO product = new ProductDTO();
		product.setId(id);
		product.setName("Camara Sony [Metodo Alternativo]");
		product.setPrice(500.00);
		item.setProductDTO(product);
		item.setQuantity(random.nextInt(10)+1);
		return CompletableFuture.supplyAsync(()->ResponseEntity.ok(item));
	}
}
