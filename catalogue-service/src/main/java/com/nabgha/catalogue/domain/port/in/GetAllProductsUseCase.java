package com.nabgha.catalogue.domain.port.in;

import com.nabgha.catalogue.domain.model.Product;

import java.util.List;

public interface GetAllProductsUseCase {

    List<Product> execute();
}
