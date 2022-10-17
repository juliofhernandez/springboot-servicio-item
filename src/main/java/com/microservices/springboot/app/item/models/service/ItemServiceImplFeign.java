package com.microservices.springboot.app.item.models.service;

import java.util.List;
import java.util.stream.Collectors; 
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.microservices.springboot.app.item.clients.ProductRestClient;
import com.microservices.springboot.app.item.models.Item;

@Service
//@Primary
public class ItemServiceImplFeign implements ItemService {

	@Autowired
	private ProductRestClient productRESTClient;
	
	@Override
	public List<Item> findAll() {
		return productRESTClient.findAll().stream().map(p -> new Item(p, 1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer quantity) {
		return new Item(productRESTClient.findById(id),quantity);
	}

}
