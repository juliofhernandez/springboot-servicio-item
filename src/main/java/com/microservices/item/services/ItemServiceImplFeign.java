package com.microservices.item.services;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.microservices.item.clients.ProductRestClient;
import com.microservices.item.models.Item;
import com.microservices.item.models.ProductDTO;

@Service
//@Primary
public class ItemServiceImplFeign implements ItemService {

    final private ProductRestClient productRESTClient;

    public ItemServiceImplFeign(ProductRestClient productRESTClient) {
        this.productRESTClient = productRESTClient;
    }

    /**
     * Retrieves a list of {@link Item} objects by fetching all {@link ProductDTO} objects from the external REST client and mapping them to {@link Item} instances.
     *
     * @return a List of {@link Item} objects, each containing a {@link ProductDTO} and a random quantity.
     */
    @Override
    public List<Item> findAll() {
        return productRESTClient.findAll().stream().map(product -> new Item(product, new Random().nextInt(10) + 1)).collect(Collectors.toList());
    }

    /**
     * Retrieves an {@link Item} by its ID and quantity.
     *
     * @param id       the ID of the {@link ProductDTO} to be retrieved.
     * @param quantity the quantity of the {@link Item} to be created.
     * @return an {@link Item} containing the retrieved {@link ProductDTO} and the specified quantity.
     */
    @Override
    public Optional<Item> findById(Long id) {
        ProductDTO productDTO = productRESTClient.findById(id);
        return Optional.of(new Item(productDTO, new Random().nextInt(10) + 1));
    }

}
