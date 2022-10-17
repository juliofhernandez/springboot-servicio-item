package com.microservices.springboot.app.item.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.springboot.app.item.models.Product;

@FeignClient(name = "service-products",url = "localhost:8001")
public interface ProductRestClient {
	
	@GetMapping("/products")
	public List<Product> findAll();
	
	@GetMapping("/products/{id}")
	public Product findById(@PathVariable Long id);
	
}
