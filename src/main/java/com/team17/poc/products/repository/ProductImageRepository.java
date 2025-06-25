package com.team17.poc.products.repository;

import com.team17.poc.products.entity.Product;
import com.team17.poc.products.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductOrderByImageOrderAsc(Product product);

    ProductImage findFirstByProductOrderByImageOrderAsc(Product product);

}
