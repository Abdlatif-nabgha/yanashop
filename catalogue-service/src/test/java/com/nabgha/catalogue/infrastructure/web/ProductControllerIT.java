package com.nabgha.catalogue.infrastructure.web;

import com.nabgha.catalogue.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductController.
 *
 * @SpringBootTest   — loads the full Spring context (real beans, real DB via Testcontainers)
 * @AutoConfigureMockMvc — creates a MockMvc instance that drives the controllers
 *
 * The PostgreSQL container is inherited from AbstractIntegrationTest.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // -----------------------------------------------------------------------
    // POST /api/v1/products
    // -----------------------------------------------------------------------

    @Test
    void should_create_product_and_return_201_created() throws Exception {
        String requestJson = """
                {
                    "name": "MacBook Pro M4",
                    "description": "Ordinateur portable Apple",
                    "price": 2499.99,
                    "currency": "EUR",
                    "initialQuantity": 50,
                    "category": "ELECTRONICS"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                // Response body is ApiResponse<ProductResponseDto> → fields are under $.data
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("MacBook Pro M4"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.stockQuantity").value(50));
    }

    @Test
    void should_return_400_when_name_is_missing() throws Exception {
        String requestJson = """
                {
                    "description": "Sans nom",
                    "price": 99.99,
                    "currency": "EUR",
                    "initialQuantity": 5,
                    "category": "BOOKS"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_400_when_price_is_zero() throws Exception {
        String requestJson = """
                {
                    "name": "Produit gratuit",
                    "description": "Prix invalide",
                    "price": 0,
                    "currency": "EUR",
                    "initialQuantity": 1,
                    "category": "OTHER"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/products/{id}
    // -----------------------------------------------------------------------

    @Test
    void should_return_404_when_product_does_not_exist() throws Exception {
        mockMvc.perform(get("/api/v1/products/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
