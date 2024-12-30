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
@RequestMapping("/items")
public class ItemController {
	
	private final Logger logger = LoggerFactory.getLogger(ItemController.class);	
	private final CircuitBreakerFactory circuitBreakerFactory;	
	private final ItemService itemService;
	private final Random random = new Random();

	@Value("${config.text.msg}")
	private String configTextMsg;

	@Autowired
	private Environment environment;
	
	/**
	 * This constructor initiates {@Link ItemController} with the specific implementation
	 * @param circuitBreakerFactory		the {@link CircuitBreakerFactory} used to create and configure circuit breakers for handling service failures.
	 * @param itemService				the implementation of {@link ItemService} injected 
	 */
	public ItemController(CircuitBreakerFactory circuitBreakerFactory, @Qualifier("itemServiceImplFeign") ItemService itemService) {
		this.circuitBreakerFactory = circuitBreakerFactory;
		this.itemService = itemService;
	}
	
	/**
	 * Retrieves a list of items
	 * @return 			a list of {@link Item} available
	 */
	@GetMapping()
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
	@GetMapping("/{id}")
	@CircuitBreaker(name = "items",fallbackMethod = "metodoAlternativo")
	@TimeLimiter(name = "items")
	public CompletableFuture<ResponseEntity<Item>> findById(@PathVariable Long id) {
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
	public CompletableFuture<ResponseEntity<Item>> metodoAlternativo(Long id, Throwable throwable) {
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

	/**
	 * Fetches configuration properties for the Items service and returns them as a JSON response.
	 * This endpoint retrieves the value of a custom configuration property (`configTextMsg`) and the current server's port number.
	 * The configuration for the Items service is sourced from a configuration file managed by the configuration server.
	 *
	 * @param port the port number of the server, injected using the {@code @Value} annotation.
	 * @return a {@link ResponseEntity} containing a JSON response with configuration details:
	 *         - "configTextMsg": the value of the {@code configTextMsg} property.
	 *         - "port": the current server port.
	 */
	@GetMapping("/fetchConfig")
	public ResponseEntity<?> fetchConfig(@Value("${server.port}") String port) {
		Map<String,String> jsonResponse = new HashMap<>();
		jsonResponse.put("configTextMsg", configTextMsg);
		jsonResponse.put("server.port", port);

		if(environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("dev")) {
			jsonResponse.put("author.name", environment.getProperty("config.author.name"));
			jsonResponse.put("author.email", environment.getProperty("config.author.email"));
		}

		return ResponseEntity.ok(jsonResponse);
	}

	/**
	 * Crea un nuevo item en el sistema. -> No se crea un Item, ya que no hay un Database de items, se crea un producto.
	 * @param item El item que se desea guardar. El objeto debe ser proporcionado en el cuerpo de la solicitud.
	 * @return Un `ResponseEntity` que contiene el item guardado y un estado HTTP 201 si la creación fue exitosa.
	 */
	@PostMapping
	public ResponseEntity<Item> save(@RequestBody Item item) {
		return ResponseEntity.status(HttpStatus.CREATED).body(itemService.save(item).get());
	}

	/**
	 * Actualiza un item existente en el sistema. -> No se actualiza un Item, ya que no hay un Database de items, se actualiza un producto.
	 * @param id El identificador único del item que se desea actualizar.Este valor se pasa en la URL como un parámetro de ruta.
	 * @param item El objeto `Item` que contiene los nuevos datos que se desean actualizar en el item. El objeto debe ser proporcionado en el cuerpo de la solicitud.
	 * @return Un `ResponseEntity` que contiene el item actualizado y un estado HTTP 201 si la actualización fue exitosa.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Item> update(@PathVariable Long id,@RequestBody Item item) {
		return ResponseEntity.status(HttpStatus.CREATED).body(itemService.update(id,item).get());
	}

	/**
	 * Elimina un item existente del sistema. -> No se elimina un Item, ya que no hay un Database de items, se elimina un producto.
	 * @param id El identificador único del item que se desea eliminar. Este valor se pasa en la URL como un parámetro de ruta.
	 * @return Un `ResponseEntity` con un estado HTTP 204 si la eliminación fue exitosa.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		itemService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
