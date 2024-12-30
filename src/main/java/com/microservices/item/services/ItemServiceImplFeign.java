package com.microservices.item.services;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.microservices.item.clients.ProductFeignClient;
import com.microservices.item.models.Item;
import com.microservices.item.models.ProductDTO;

@Service
//@Primary
public class ItemServiceImplFeign implements ItemService {

    private final ProductFeignClient productFeignClient;
    private Random random = new Random();

    public ItemServiceImplFeign(ProductFeignClient productFeignClient) {
        this.productFeignClient = productFeignClient;
    }

    /**
     * Retrieves a list of {@link Item} objects by fetching all {@link ProductDTO} objects from the external REST client and mapping them to {@link Item} instances.
     * @return a List of {@link Item} objects, each containing a {@link ProductDTO} and a random quantity.
     */
    @Override
    public List<Item> findAll() {
        return productFeignClient.findAll().stream().map(product -> new Item(product, new Random().nextInt(10) + 1)).collect(Collectors.toList());
    }

    /**
     * Retrieves an {@link Item} by its ID and quantity.
     * @param id       the ID of the {@link ProductDTO} to be retrieved.
     * @return an {@link Item} containing the retrieved {@link ProductDTO} and the specified quantity.
     */
    @Override
    public Optional<Item> findById(Long id) {
        ProductDTO productDTO = productFeignClient.findById(id);
        return Optional.of(new Item(productDTO, random.nextInt(10) + 1));
    }

    @Override
    public Optional<Item> save(Item item) {
        ProductDTO productDTO = productFeignClient.save(item.getProductDTO());
        return Optional.of(new Item(productDTO, item.getQuantity()));
    }

    @Override
    public void deleteById(Long id) {
        productFeignClient.delete(id);
    }

    @Override
    public Optional<Item> update(Long id, Item item) {
        ProductDTO productDTO = productFeignClient.update(id, item.getProductDTO());
        return Optional.of(new Item(productDTO, item.getQuantity()));
    }
}
