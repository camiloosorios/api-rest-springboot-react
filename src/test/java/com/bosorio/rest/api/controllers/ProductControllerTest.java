package com.bosorio.rest.api.controllers;

import com.bosorio.rest.api.dto.ProductDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    private final static String BASE_URL = "/api/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Tests for Listing Products")
    class  getListProducts {
        @Test
        public void testGetAllProductsReturnsStatusCode200AndJsonContentType() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            assertEquals(200, mockMvcResult.getResponse().getStatus());
            assertEquals("application/json", mockMvcResult.getResponse().getContentType());
            assertTrue(jsonNode.has("data"));
        }
    }

    @Nested
    @DisplayName("Tests for Creating Product")
    class createProductTest {

        @Test
        public void testCreateNewProduct() throws Exception {
            ProductDTO productDTO = ProductDTO.builder()
                    .name("Tablet")
                    .price(BigDecimal.valueOf(200))
                    .build();
            String jsonProductDTO = objectMapper.writeValueAsString(productDTO);
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonProductDTO))
                    .andReturn();


            String jsonResponse = mockMvcResult.getResponse().getContentAsString();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            assertEquals(200, mockMvcResult.getResponse().getStatus());
            assertEquals("application/json", mockMvcResult.getResponse().getContentType());
            assertTrue(jsonNode.has("data"), "La respuesta no contiene datos");

        }

        @Test
        public void testCreatingProductWithoutBody() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertEquals(400, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("error"), "La respuesta no contiene errores");
        }

        @Test
        public void testPriceIsNumberAndGreaterThanZero() throws Exception {
            ProductDTO productDTO = ProductDTO.builder()
                    .name("Tablet")
                    .price(BigDecimal.valueOf(0))
                    .build();
            String jsonProductDTO = objectMapper.writeValueAsString(productDTO);
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonProductDTO))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertTrue(jsonResponse.contains("error"), "La respuesta no contiene errores");
            assertTrue(jsonResponse.contains("Precio no valido"), "La respuesta no contiene el error correspondiente");
        }
    }

    @Nested
    @DisplayName("Tests for getting product by id")
    class getProductById {
        @Test
        public void testFindProductByIdThatDoesntExist() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/3")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertEquals(404, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("error"), "La respuesta no contiene errores");
            assertTrue(jsonResponse.contains("Producto No Encontrado"), "La respuesta no contiene el error correspondiente");
        }

        @Test
        public void testFindProductById() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();
            assertEquals(200, mockMvcResult.getResponse().getStatus());
            assertEquals("application/json", mockMvcResult.getResponse().getContentType());
            assertTrue(jsonResponse.contains("data"), "La respuesta no contiene datos");
            assertTrue(jsonResponse.contains("\"name\":\"Tablet Actualizada\""), "La respuesta no contiene el mensaje esperado");
        }

        @Test
        public void testIdIsNotANumber() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/text")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertEquals(400, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("error"), "La respuesta no contiene errores");
            assertTrue(jsonResponse.contains("Id no valido"), "La respuesta no contiene el mensaje esperado");

        }
    }

    @Nested
    @DisplayName("Tests for updating a product")
    class updateProduct {
        @Test
        public void testUpdateProductWithoutNumericId() throws Exception {
            ProductDTO productDTO = ProductDTO.builder().build();
            String jsonProductDTO = objectMapper.writeValueAsString(productDTO);
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/text")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonProductDTO))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertEquals(400, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("Id no valido"), "La respuesta no contiene el mensaje esperado");
        }

        @Test
        public void testUpdateProductWithoutBody() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                            .content("")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();
            JsonNode errorsNode = objectMapper.readTree(jsonResponse).get("errors");

            assertEquals(400, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("errors"), "La respuesta no contiene errores");
            assertEquals(3, errorsNode.size());
        }

        @Test
        public void testUpdateProduct() throws Exception {
            ProductDTO productDTO = ProductDTO.builder()
                    .name("Tablet Actualizada")
                    .price(BigDecimal.valueOf(250))
                    .build();
            String jsonProductDTO = objectMapper.writeValueAsString(productDTO);
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/2")
                            .content(jsonProductDTO)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();
            assertEquals(200, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("data"), "La respuesta no contiene data");
            assertTrue(jsonResponse.contains("\"name\":\"Tablet Actualizada\",\"price\":250"), "La respuesta no contiene la respuesta esperada");
        }

        @Test
        public void testUpdatedNonExistingProduct() throws Exception {
            ProductDTO productDTO = ProductDTO.builder()
                    .name("Tablet Actualizada")
                    .build();
            String jsonProductDTO = objectMapper.writeValueAsString(productDTO);
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/2000")
                            .content(jsonProductDTO)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertEquals(404, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("error"), "La respuesta no contiene error");
            assertTrue(jsonResponse.contains("Producto No Encontrado"), "La respuesta no contiene la respuesta esperada");
        }

        @Test
        public void testUpdateAvailability() throws Exception {
            ProductDTO productDTO = ProductDTO.builder()
                    .build();
            String jsonProductDTO = objectMapper.writeValueAsString(productDTO);
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + "/2")
                            .content(jsonProductDTO)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();
            assertEquals(200, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("data"), "La respuesta no contiene data");
            assertTrue(jsonResponse.contains("\"availability\":false"), "La respuesta no contiene la respuesta esperada");
        }
    }

    @Nested
    @DisplayName("Tests for deleting a product")
    class deleteProduct {
        @Test
        public void testDeleteNonExistingProduct() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/2000")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertEquals(400, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("error"), "La respuesta no contiene error");
            assertTrue(jsonResponse.contains("Producto no encontrado"), "La respuesta no contiene la respuesta esperada");
        }

        @Test
        public void testDeleteProduct() throws Exception {
            MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String jsonResponse = mockMvcResult.getResponse().getContentAsString();

            assertEquals(200, mockMvcResult.getResponse().getStatus());
            assertTrue(jsonResponse.contains("data"), "La respuesta no contiene data");
            assertTrue(jsonResponse.contains("Producto eliminado"), "La respuesta no contiene la respuesta esperada");
        }
    }

}