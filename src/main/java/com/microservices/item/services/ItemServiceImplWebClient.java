package com.microservices.item.services;

import com.microservices.item.models.Item;
import com.microservices.item.models.ProductDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class ItemServiceImplWebClient implements ItemService {

    private final WebClient webClient;
    private final Random random = new Random();

    public ItemServiceImplWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Item> findAll() {
        return webClient
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .map(productDTO -> new Item(productDTO, random.nextInt(10) + 1))
                .collectList()
                .block();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        return Optional.ofNullable(
                webClient
                        .get()
                        .uri("/{id}", params)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(ProductDTO.class)
                        .map(productDTO -> new Item(productDTO, random.nextInt(10) + 1))
                        .block()
        );
    }

    @Override
    public Optional<Item> save(Item item) {
        return Optional.ofNullable(
                webClient
                        .post()
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(item.getProductDTO())
                        .retrieve()
                        .bodyToMono(ProductDTO.class)
                        .map(productDTO -> new Item(productDTO, random.nextInt(10) + 1))
                        .block()
        );
    }

    @Override
    public void deleteById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        webClient
                .delete()
                .uri("/{id}", params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public Optional<Item> update(Long id, Item item) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        return Optional.ofNullable(
                webClient
                        .put()
                        .uri("/{id}", params)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(item.getProductDTO())
                        .retrieve()
                        .bodyToMono(ProductDTO.class)
                        .map(productDTO -> new Item(productDTO, random.nextInt(10) + 1))
                        .block()
        );
    }
}
