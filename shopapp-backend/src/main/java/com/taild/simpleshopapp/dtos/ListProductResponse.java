package com.taild.simpleshopapp.dtos;

import com.taild.simpleshopapp.dtos.products.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListProductResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
