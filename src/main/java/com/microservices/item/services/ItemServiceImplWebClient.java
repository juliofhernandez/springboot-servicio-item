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

    private final WebClient.Builder webClient;
    private final Random random = new Random();

    /**
     * Constructor para inyectar el {@link WebClient.Builder} que se utilizará para realizar solicitudes HTTP a otros microservicios.
     *
     * @param webClient {@link WebClient.Builder} configurado con la base URL del servicio al que se desea hacer la llamada.
     */
    public ItemServiceImplWebClient(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    /**
     * Obtiene una lista de {@link Item} desde el microservicio de productos. Realiza una solicitud GET para obtener todos los productos y, por cada uno, lo mapea a un {@link Item}.
     *
     * @return Una lista de {@link Item} que contiene todos los productos encontrados, donde cada {@link Item} se crea a partir de los datos del {@link ProductDTO}.
     * Si no se encuentran productos, la lista estará vacía.
     *
     * @throws WebClientResponseException Si ocurre un error en la comunicación con el microservicio (por ejemplo, si el servicio responde con un error).
     *
     * @see WebClient
     * @see ProductDTO
     * @see Item
     */
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

    /**
     * Obtiene un {@link Item} a partir del identificador del producto. Realiza una solicitud GET al microservicio
     * para obtener los detalles del producto y, si se encuentra, lo mapea a un {@link Item}.
     *
     * @param id El identificador del producto a buscar. Este valor se utiliza para obtener el producto correspondiente desde el microservicio.
     * @return Un {@link Optional} que contiene el {@link Item} correspondiente si el producto es encontrado, o un {@link Optional#empty()} si el producto no se encuentra o ocurre un error durante la solicitud.
     *
     * @throws WebClientResponseException Si ocurre un error en la comunicación con el microservicio (por ejemplo, si el producto no es encontrado o el servicio responde con un error), retrieve().
     *
     * @see WebClient
     * @see ProductDTO
     * @see Item
     */
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

    @Override
    public Optional<Item>  save(Item item) {
        return Optional.ofNullable(
          webClient.build()
                  .post()
                  .accept(MediaType.APPLICATION_JSON)
                  .bodyValue(item.getProductDTO())
                  .retrieve()
                  .bodyToMono(ProductDTO.class)
                  .map(productDTO -> new Item(productDTO, random.nextInt(10)+1))
                  .block()
        );
    }

    @Override
    public void deleteById(Long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        webClient.build()
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
                webClient.build()
                        .put()
                        .uri("/{id}", params)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(item.getProductDTO())
                        .retrieve()
                        .bodyToMono(ProductDTO.class)
                        .map(productDTO -> new Item(productDTO, random.nextInt(10)+1))
                        .block()
        );
    }
}
