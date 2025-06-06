package com.taild.simpleshopapp.services.products;

import com.taild.simpleshopapp.dtos.products.ProductResponse;
import com.taild.simpleshopapp.models.Product;
import com.taild.simpleshopapp.dtos.ProductDTO;
import com.taild.simpleshopapp.models.ProductImage;
import com.taild.simpleshopapp.dtos.ProductImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    Page<ProductResponse> getAllProducts(String keyword,
                                         Long categoryId, PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);
    ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception;

    List<Product> findProductsByIds(List<Long> productIds);
}
