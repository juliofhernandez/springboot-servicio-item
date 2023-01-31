package com.microservices.springboot.app.item.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.microservices.springboot.app.item.models.Item;
import com.microservices.springboot.app.item.models.Product;
import com.microservices.springboot.app.item.models.service.ItemService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class ItemController {
	
	@Autowired
//	@Qualifier("itemServiceImplFeign")
	@Qualifier("itemServiceImplRestTemplate")
	private ItemService itemService;
	
	@GetMapping("/items")
	public List<Item> findAll(){
		return itemService.findAll();
	}
	
	@HystrixCommand(fallbackMethod = "metodoAlternativo")
	@GetMapping("/items/{id}/quantity/{quantity}")
	public Item findById(@PathVariable Long id, @PathVariable Integer quantity) {
		return itemService.findById(id, quantity);
	}
	
	public Item metodoAlternativo(Long id, Integer quantity) {
		Item item = new Item();
		Product product = new Product();
		product.setId(id);
		product.setName("Camara Sony");
		product.setPrice(500.00);
		item.setProduct(product);
		item.setQuantity(quantity);
		return item;
	}
}
