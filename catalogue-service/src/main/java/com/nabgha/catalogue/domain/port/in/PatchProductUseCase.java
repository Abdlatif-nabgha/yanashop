package com.nabgha.catalogue.domain.port.in;

import com.nabgha.catalogue.domain.model.Product;

public interface PatchProductUseCase {

    Product execute(PatchProductCommand command);
}
