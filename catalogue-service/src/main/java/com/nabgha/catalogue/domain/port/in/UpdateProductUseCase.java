package com.nabgha.catalogue.domain.port.in;

import com.nabgha.catalogue.domain.model.Product;

public interface UpdateProductUseCase {

    Product execute(UpdateProductCommand command);
}
