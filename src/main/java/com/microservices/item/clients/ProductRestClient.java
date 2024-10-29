package com.microservices.item.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.item.models.ProductDTO;

/*
 * En el contexto del servicio Items, se define un cliente (Feign) que se comunica con el servicio Products.
 * Este cliente realiza solicitudes a los endpoints definidos en el servicio Products, los cuales devuelven
 * la información en formato JSON (REST). Esta información es mapeada a objetos ProductDTO, permitiendo su uso
 * en formato Java dentro del servicio Items.
 * 
 * En este caso, obtenemos la informacion no de un repository o dao ì, si no de este cliente que a su vez obtiene
 * la informacion de un servicio externo.
 */

@FeignClient(name = "service-products")
public interface ProductRestClient {
	
	@GetMapping("/products")
	public List<ProductDTO> findAll();
	
	@GetMapping("/products/{id}")
	public ProductDTO findById(@PathVariable Long id);
	
}
