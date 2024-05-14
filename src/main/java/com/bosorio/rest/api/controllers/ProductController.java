package com.bosorio.rest.api.controllers;

import com.bosorio.rest.api.dto.ProductDTO;
import com.bosorio.rest.api.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Products", description = "API operations related to products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    @Transactional
    @Operation(summary = "Create a new product", description = "Returns a new record in the database")
    @ApiResponse(responseCode = "200",
            description = "Succesful response",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(mediaType = ""))
    public ResponseEntity<?> create(@RequestBody(required = false) ProductDTO productDTO) {
        try {
            if (productDTO == null) {
                throw new RuntimeException("Todos los campos son obligatorios");
            }
            ProductDTO newProductDTO = productService.create(productDTO);
            Map<String, ProductDTO> response = new HashMap<>();
            response.put("data", newProductDTO);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());

            return  ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/products")
    @Operation(summary = "Get a list of products", description = "Resturn a list of products")
    @ApiResponse(responseCode = "200",
                 description = "Succesful response",
                 content = @Content(mediaType = "application/json",
                 array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))))
    public ResponseEntity<?> getAll() {
        Map<String, List<ProductDTO>> response = new HashMap<>();
        List<ProductDTO> products =  productService.getAll();
        response.put("data", products);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get a product by id", description = "Return a product based on its unique ID")
    @ApiResponse(responseCode = "200",
            description = "Succesful response",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid ID", content = @Content(mediaType = ""))
    @ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content(mediaType = ""))
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ProductDTO product = productService.getById(id);

        if (product == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Producto No Encontrado");
            return ResponseEntity.status(404).body(response);
        }
        Map<String, ProductDTO> response = new HashMap<>();
        response.put("data", product);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Updates a product with user input", description = "Returns the updated product")
    @ApiResponse(responseCode = "200",
            description = "Succesful response",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid ID or Invalid input data", content = @Content(mediaType = ""))
    @ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content(mediaType = ""))
    @Transactional
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO updatedProductDTO = productService.update(id, productDTO);
            if (updatedProductDTO == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Producto No Encontrado");
                return ResponseEntity.status(404).body(response);
            }
            Map<String, ProductDTO> response = new HashMap<>();
            response.put("data", updatedProductDTO);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/products/{id}")
    @Operation(summary = "Actualizar disponibilidad del producto")
    @Transactional
    public ResponseEntity<?> updateAvailability(@PathVariable Long id) {
        try {
            ProductDTO updatedProductDTO = productService.updateAvailability(id);
            Map<String, ProductDTO> response = new HashMap<>();
            response.put("data", updatedProductDTO);

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Producto no pudo ser actualizado");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Eliminar un producto producto")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            Map<String, String> response = new HashMap<>();
            response.put("data", "Producto eliminado");

            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
