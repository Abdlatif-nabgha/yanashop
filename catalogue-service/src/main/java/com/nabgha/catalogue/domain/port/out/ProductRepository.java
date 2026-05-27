package com.nabgha.catalogue.domain.port.out;


import com.nabgha.catalogue.domain.model.Product;
import com.nabgha.catalogue.domain.model.ProductId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(ProductId id);

    List<Product> findAll();

    void deleteById(ProductId id);
}
