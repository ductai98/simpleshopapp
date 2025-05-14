package com.taild.simpleshopapp.controllers;


import com.taild.simpleshopapp.dtos.ProductDTO;
import com.taild.simpleshopapp.dtos.ProductListDTO;
import com.taild.simpleshopapp.dtos.ResponseDTO;
import com.taild.simpleshopapp.services.IProductService;
import com.taild.simpleshopapp.utils.FileUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final IProductService productService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> createProduct(
            @RequestPart("files") List<MultipartFile> files,
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result
    ) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        ResponseDTO.builder()
                                .message(String.join("; ", errorMessages))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }

            // Validate product image size
            List<String> fileNames = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file != null && file.getSize() > 10 * 1024 * 1024) { // 10MB
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body(ResponseDTO.builder()
                                        .message("Product image size must be less than 10MB")
                                        .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                        .build());
                    }

                    String contentType = file.getContentType();
                    if(contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .body(ResponseDTO.builder()
                                        .message("Product image must be in image format (JPEG, PNG, GIF,...)")
                                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                        .build());
                    }

                    String filename = FileUtils.storeFile(file);
                    fileNames.add(filename);
                }
            }

            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .message("Create new product successfully")
                            .status(HttpStatus.CREATED)
                            .data(fileNames)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

    }


    @GetMapping("")
    public ResponseEntity<ResponseDTO> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d",
                keyword, categoryId, page, limit));

        Page<ProductDTO> productPage = productService
                .getAllProducts(keyword, categoryId, pageRequest);
        // Lấy tổng số trang
        totalPages = productPage.getTotalPages();
        List<ProductDTO> products = productPage.getContent();
        // Bổ sung totalPages vào các đối tượng ProductResponse
        for (ProductDTO product : products) {
            product.setTotalPages(totalPages);
        }

        ProductListDTO productListResponse = ProductListDTO
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Get products successfully")
                .status(HttpStatus.OK)
                .data(productListResponse)
                .build());
    }
}
