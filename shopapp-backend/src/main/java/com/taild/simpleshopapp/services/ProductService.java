package com.taild.simpleshopapp.services;

import com.taild.simpleshopapp.dtos.ProductDTO;
import com.taild.simpleshopapp.dtos.ProductImageDTO;
import com.taild.simpleshopapp.models.Product;
import com.taild.simpleshopapp.models.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService  {

    @Override
    public Product createProduct(ProductDTO productDTO) throws Exception {
        return null;
    }

    @Override
    public Product getProductById(long id) throws Exception {
        return null;
    }

    @Override
    public Page<ProductDTO> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        return null;
    }

    @Override
    public void deleteProduct(long id) {

    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        return null;
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return List.of();
    }

    @Override
    public Product likeProduct(Long userId, Long productId) throws Exception {
        return null;
    }

    @Override
    public Product unlikeProduct(Long userId, Long productId) throws Exception {
        return null;
    }

    @Override
    public List<ProductDTO> findFavoriteProductsByUserId(Long userId) throws Exception {
        return List.of();
    }

    @Override
    public void generateFakeLikes() throws Exception {

    }
}
