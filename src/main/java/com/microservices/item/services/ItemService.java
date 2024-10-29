package com.microservices.item.services;

import java.util.List;
import java.util.Optional;

import com.microservices.item.models.Item;

public interface ItemService {
	public List<Item> findAll();
	public Optional<Item> findById(Long id);

}
