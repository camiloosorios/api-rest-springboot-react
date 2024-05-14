package com.bosorio.rest.api.services;

import com.bosorio.rest.api.dto.ProductDTO;
import com.bosorio.rest.api.entities.Product;
import com.bosorio.rest.api.respositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDTO create(ProductDTO productDTO) {
        if (productDTO.getName().isBlank()){
            throw new RuntimeException("El nombre no puede ir vacio");
        }
        if (productDTO.getPrice() == null) {
            throw new RuntimeException("El precio no puede ir vacio");
        }
        if (productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Precio no valido");
        }

        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .build();

        Product newProduct = productRepository.save(product);

        return ProductDTO.builder()
                .id(newProduct.getId())
                .name(newProduct.getName())
                .price(newProduct.getPrice())
                .availability(newProduct.getAvailability())
                .createdAt(newProduct.getCreatedAt())
                .updatedAt(newProduct.getUpdatedAt())
                .build();
    }

    @Override
    public List<ProductDTO> getAll() {
        List<ProductDTO> products = new ArrayList<>();
        productRepository.findAll().forEach(product -> {
            products.add(ProductDTO.builder()
                   .id(product.getId())
                   .name(product.getName())
                   .price(product.getPrice())
                   .availability(product.getAvailability())
                   .createdAt(product.getCreatedAt())
                   .updatedAt(product.getUpdatedAt())
                   .build());
        });
        return products;
    }

    @Override
    public ProductDTO getById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return ProductDTO.builder()
                   .id(product.getId())
                   .name(product.getName())
                   .price(product.getPrice())
                   .availability(product.getAvailability())
                   .createdAt(product.getCreatedAt())
                   .updatedAt(product.getUpdatedAt())
                   .build();
        }
        return null;
    }

    @Override
    public ProductDTO update(Long id, ProductDTO productDTO) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product existingProduct = productOptional.get();
            Field[] fields = productDTO.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(productDTO);
                    if (value != null){
                        if (field.getName().equals("price")) {
                            BigDecimal priceValue;
                            if (value instanceof BigDecimal) {
                                priceValue = (BigDecimal) value;
                                if (priceValue.compareTo(BigDecimal.ZERO) <= 0) {
                                    throw new RuntimeException("Precio no válido");
                                }
                            }
                        }
                        Field existingProductField = existingProduct.getClass().getDeclaredField(field.getName());
                        existingProductField.setAccessible(true);
                        existingProductField.set(existingProduct, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            existingProduct.setUpdatedAt(LocalDateTime.now());
            Product updatedProduct = productRepository.save(existingProduct);
            return ProductDTO.builder()
                    .id(updatedProduct.getId())
                    .name(updatedProduct.getName())
                    .price(updatedProduct.getPrice())
                    .availability(updatedProduct.getAvailability())
                    .createdAt(updatedProduct.getCreatedAt())
                    .updatedAt(updatedProduct.getUpdatedAt())
                    .build();
        }
        return null;
    }

    @Override
    public ProductDTO updateAvailability(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product existingProduct = productOptional.get();
            existingProduct.setAvailability(!existingProduct.getAvailability());

            return ProductDTO.builder()
                    .id(existingProduct.getId())
                    .name(existingProduct.getName())
                    .price(existingProduct.getPrice())
                    .availability(existingProduct.getAvailability())
                    .createdAt(existingProduct.getCreatedAt())
                    .updatedAt(existingProduct.getUpdatedAt())
                    .build();
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            productRepository.delete(productOptional.get());
        } else {
            throw new RuntimeException("Producto no encontrado");
        }
    }
}
