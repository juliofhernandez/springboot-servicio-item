package com.microservices.item.models;

public class Item {
	private ProductDTO productDTO;
	private Integer quantity;

	public Item() {
	}

	public Item(ProductDTO productDTO, Integer quantity) {
		this.productDTO = productDTO;
		this.quantity = quantity;
	}

	public ProductDTO getProductDTO() {
		return productDTO;
	}

	public void setProductDTO(ProductDTO product) {
		this.productDTO = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getTotal() {
		return productDTO.getPrice() * quantity.doubleValue();
	}

}
