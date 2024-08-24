package com.microservices.springboot.app.item.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microservices.springboot.app.item.models.Item;
import com.microservices.springboot.app.item.models.Product;
import com.microservices.springboot.app.item.models.service.ItemService;

@RestController
public class ItemController {
	
	private final Logger logger = LoggerFactory.getLogger(ItemController.class);
	
	@Autowired
	private CircuitBreakerFactory circuitBreakerFactory;
	
	@Autowired
//	@Qualifier("itemServiceImplFeign")
	@Qualifier("itemServiceImplRestTemplate")
	private ItemService itemService;
	
	@GetMapping("/items")
	public List<Item> findAll(@RequestParam(name = "nombre") String nombre, @RequestHeader(name = "token-request") String tokenRequest){
		System.out.println("Nombre: "+nombre);
		System.out.println("tokenRequest: "+tokenRequest);
		return itemService.findAll();
	}
	
	@GetMapping("/items/{id}/quantity/{quantity}")
	public Item findById(@PathVariable Long id, @PathVariable Integer quantity) {
//		return itemService.findById(id, quantity);
		return circuitBreakerFactory.create("items").run(() -> itemService.findById(id, quantity), e -> metodoAlternativo(id, quantity, e));
	}
	
	public Item metodoAlternativo(Long id, Integer quantity, Throwable throwable) {
		logger.info(throwable.getMessage());
		Item item = new Item();
		Product product = new Product();
		product.setId(id);
		product.setName("Camara Sony [Metodo Alternativo]");
		product.setPrice(500.00);
		item.setProduct(product);
		item.setQuantity(quantity);
		return item;
	}
}
