package com.bosorio.rest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(name = "Product")
public class ProductDTO {

    @Schema(type = "integer", description = "The product ID", example = "1")
    private Long id;

    @Schema(type = "string", description = "The product name", example = "Monitor Curvo de 49 Pulgadas")
    private String name;

    @Schema(type = "number", description = "The product price", example = "300")
    private BigDecimal price;

    @Schema(type = "boolean", description = "The product availability", example = "true")
    private Boolean availability;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
