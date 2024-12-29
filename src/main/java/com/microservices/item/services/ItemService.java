package com.microservices.item.services;

import java.util.List;
import java.util.Optional;

import com.microservices.item.models.Item;
import reactor.core.publisher.Mono;

public interface ItemService {
	public List<Item> findAll();
	public Optional<Item> findById(Long id);
	public Optional<Item>  save(Item item);
	public void deleteById(Long id);
	public Optional<Item>  update(Long id, Item item);
}
