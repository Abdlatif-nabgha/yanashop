package com.nabgha.catalogue.application.service;

import com.nabgha.catalogue.domain.exception.ProductNotFoundException;
import com.nabgha.catalogue.domain.model.Money;
import com.nabgha.catalogue.domain.model.Product;
import com.nabgha.catalogue.domain.model.ProductId;
import com.nabgha.catalogue.domain.port.in.*;
import com.nabgha.catalogue.domain.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductApplicationService
        implements CreateProductUseCase, GetProductUseCase,
                   GetAllProductsUseCase, UpdateProductUseCase,
                   PatchProductUseCase, DeleteProductUseCase {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product execute(CreateProductCommand command) {
        Money price = Money.of(command.price(), command.currency());
        Product product = Product.create(
                command.name(),
                command.description(),
                price,
                command.initialQuantity(),
                command.category()
        );
        productRepository.save(product);
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> execute(ProductId id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> execute() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public Product execute(UpdateProductCommand command) {
        Product product = productRepository.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));

        product.rename(command.name());
        product.updateDescription(command.description());
        product.changePriceTo(Money.of(command.price(), command.currency()));
        product.recategorize(command.category());

        productRepository.save(product);
        return product;
    }

    @Override
    @Transactional
    public Product execute(PatchProductCommand command) {
        Product product = productRepository.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));

        if (command.name() != null)        product.rename(command.name());
        if (command.description() != null) product.updateDescription(command.description());
        if (command.category() != null)    product.recategorize(command.category());

        // price ou currency changé → on prend la valeur existante si l'un est absent
        if (command.price() != null || command.currency() != null) {
            BigDecimal newAmount   = command.price() != null
                    ? command.price()
                    : product.price().amount();
            String newCurrency     = command.currency() != null
                    ? command.currency()
                    : product.price().currency().getCurrencyCode();
            product.changePriceTo(Money.of(newAmount, newCurrency));
        }

        // stockQuantity : on calcule le delta et on appelle les méthodes domaine
        if (command.stockQuantity() != null) {
            int current = product.stockQuantity();
            int target  = command.stockQuantity();
            if (target > current)       product.increaseStock(target - current);
            else if (target < current)  product.decreaseStock(current - target);
        }

        productRepository.save(product);
        return product;
    }

    @Override
    @Transactional
    public void delete(ProductId id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.deleteById(id);
    }
}
