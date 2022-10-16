package com.microservices.springboot.app.item.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.springboot.app.item.models.Item;
import com.microservices.springboot.app.item.models.service.ItemService;

@RestController
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@GetMapping("/items")
	public List<Item> findAll(){
		return itemService.findAll();
	}
	
	@GetMapping("/items/{id}/quantity/{quantity}")
	public Item findById(@PathVariable Long id, @PathVariable Integer quantity) {
		return itemService.findById(id, quantity);
	}
	
}
