package com.microservices.item.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservices.item.models.Item;
import com.microservices.item.models.ProductDTO;
import com.microservices.item.services.ItemService;
import reactor.core.publisher.Mono;

@RefreshScope
@RestController
public class ItemController {
	
	private final Logger logger = LoggerFactory.getLogger(ItemController.class);	
	private final CircuitBreakerFactory circuitBreakerFactory;	
	private final ItemService itemService;
	private final Random random = new Random();
	@Value("${config.text.msg}")
	private String configTextMsg;
	private final Environment environment;

	public ItemController(CircuitBreakerFactory circuitBreakerFactory, @Qualifier("itemServiceImplWebClient") ItemService itemService, Environment environment) {
		this.circuitBreakerFactory = circuitBreakerFactory;
		this.itemService = itemService;
		this.environment = environment;
	}

//	@GetMapping()
//	public List<Item> findAll(@RequestParam(name="paramRequestItems", required=false) String paramRequestItems, @RequestHeader(name = "X-Header-Request-Items", required = false) String headerRequestItems) {
//		logger.info("ItemController findAll GET request");
//		logger.info("Filtros Gateway Factory de fábrica:");
//		logger.info("paramRequestItems: " + paramRequestItems);
//		logger.info("X-Header-Request-Items: " + headerRequestItems);
//		return itemService.findAll();
//	}

	@GetMapping()
	public List<Item> findAll() {
		logger.info("ItemController findAll GET request");
		return itemService.findAll();
	}

	@GetMapping("/{id}")
	@CircuitBreaker(name = "items",fallbackMethod = "metodoAlternativo")
	@TimeLimiter(name = "items")
	public CompletableFuture<ResponseEntity<Item>> findById(@PathVariable Long id) {
		logger.info("ItemController findById GET request with id: " + id);
		return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(itemService.findById(id).get()));
	}

	public CompletableFuture<ResponseEntity<Item>> metodoAlternativo(Long id, Throwable throwable) {
		logger.info("ItemController metodoAlternativo GET request with id: " + id);
		logger.info("CircuitBreaker fallback triggered: " + throwable.getMessage());
		Item item = new Item();
		ProductDTO product = new ProductDTO();
		product.setId(id);
		product.setName("Camara Sony [Metodo Alternativo]");
		product.setPrice(500.00);
		item.setProductDTO(product);
		item.setQuantity(random.nextInt(10)+1);
		return CompletableFuture.supplyAsync(()->ResponseEntity.ok(item));
	}

	@GetMapping("/fetchConfig")
	public ResponseEntity<?> fetchConfig(@Value("${server.port}") String port) {
		logger.info("ItemController fetchConfig GET request");
		Map<String,String> jsonResponse = new HashMap<>();
		jsonResponse.put("configTextMsg", configTextMsg);
		jsonResponse.put("server.port", port);
		logger.info(port);
		logger.info(configTextMsg);

		if(environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("dev")) {
			jsonResponse.put("author.name", environment.getProperty("config.author.name"));
			jsonResponse.put("author.email", environment.getProperty("config.author.email"));
		}

		return ResponseEntity.ok(jsonResponse);
	}

	@PostMapping
	public ResponseEntity<Item> save(@RequestBody Item item) {
		logger.info("ItemController save POST request: {}", item);
		return ResponseEntity.status(HttpStatus.CREATED).body(itemService.save(item).get());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Item> update(@PathVariable Long id,@RequestBody Item item) {
		logger.info("ItemController update PUT request: {}", item);
		return ResponseEntity.status(HttpStatus.CREATED).body(itemService.update(id,item).get());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		logger.info("ItemController delete DELETE request: {}", id);
		itemService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
