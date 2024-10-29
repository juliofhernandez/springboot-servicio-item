package com.microservices.item.services;

import com.microservices.item.models.Item;
import com.microservices.item.models.ProductDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class ItemServiceImplWebClient implements ItemService {

    private final WebClient.Builder webClient;
    private final Random random = new Random();

    public ItemServiceImplWebClient(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Item> findAll() {
        return webClient.build()
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
                webClient.build()
                        .get()
                        .uri("/{id}", params)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(ProductDTO.class)
                        .map(productDTO -> new Item(productDTO, random.nextInt(10) + 1))
                        .block()
        );
    }
}
