package com.nabgha.catalogue.infrastructure.persistence;

import com.nabgha.catalogue.domain.model.Product;
import com.nabgha.catalogue.domain.model.ProductId;
import com.nabgha.catalogue.domain.port.out.ProductRepository;
import com.nabgha.catalogue.infrastructure.persistence.mapper.ProductDbMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostgresProductRepository implements ProductRepository {
    private final SpringDataProductRepository springDataProductRepository;


    @Override
    public void save(Product product) {
        var entity = ProductDbMapper.toEntity(product);
        springDataProductRepository.save(entity);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return springDataProductRepository.findById(id.value())
                .map(ProductDbMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return springDataProductRepository.findAll()
                .stream()
                .map(ProductDbMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(ProductId id) {
        springDataProductRepository.deleteById(id.value());
    }
}
