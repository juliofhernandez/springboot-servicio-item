package com.microservices.springboot.app.item.models.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservices.springboot.app.item.models.Item;
import com.microservices.springboot.app.item.models.Product;

@Service
public class ItemServiceImplRestTemplate implements ItemService {
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<Item> findAll() {
//		List<Product> products = Arrays.asList(restTemplate.getForObject("http://localhost:8001/products", Product[].class));
		List<Product> products = Arrays.asList(restTemplate.getForObject("http://service-products/products", Product[].class));
		return products.stream().map(p -> new Item(p, 1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer quantity) {
		Map<String, String> pathVariables = new HashMap<String,String>();
		pathVariables.put("id", id.toString());
//		Product product = restTemplate.getForObject("http://localhost:8001/products/{id}", Product.class,pathVariables);
		Product product = restTemplate.getForObject("http://service-products/products/{id}", Product.class,pathVariables);
		return new Item(product, quantity);
	}

}
