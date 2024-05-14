package com.bosorio.rest.api.services;

import com.bosorio.rest.api.dto.ProductDTO;
import com.bosorio.rest.api.entities.Product;

import java.util.List;

public interface ProductService {

    ProductDTO create(ProductDTO productDTO);

    List<ProductDTO> getAll();

    ProductDTO getById(Long id);

    ProductDTO update(Long id, ProductDTO productDTO);

    ProductDTO updateAvailability(Long id);

    void delete(Long id);
}
