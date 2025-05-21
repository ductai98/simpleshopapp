package com.taild.simpleshopapp.controllers;


import com.github.javafaker.Faker;
import com.taild.simpleshopapp.dtos.ProductDTO;
import com.taild.simpleshopapp.dtos.ListProductResponse;
import com.taild.simpleshopapp.dtos.ProductImageDTO;
import com.taild.simpleshopapp.dtos.Response;
import com.taild.simpleshopapp.dtos.products.ProductResponse;
import com.taild.simpleshopapp.models.Product;
import com.taild.simpleshopapp.models.ProductImage;
import com.taild.simpleshopapp.services.products.IProductService;
import com.taild.simpleshopapp.utils.FileUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final IProductService productService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> createProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result
    ) throws Exception {

        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(String.join("; ", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
        Product newProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(
                Response.builder()
                        .message("Create new product successfully")
                        .status(HttpStatus.CREATED)
                        .data(newProduct)
                        .build());
    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) throws Exception {
        Product existingProduct = productService.getProductById(productId);
        files = files == null ? new ArrayList<>() : files;
        if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message("Maximum number of images is 5")
                            .build()
            );
        }
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files) {
            if(file.getSize() == 0) {
                continue;
            }
            // Kiểm tra kích thước file và định dạng
            if(file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(Response.builder()
                                .message("File is too large")
                                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .build());
            }
            String contentType = file.getContentType();
            if(contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(Response.builder()
                                .message("Uploaded file must be an image")
                                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .build());
            }

            String filename = FileUtils.storeFile(file);
            ProductImage productImage = productService.createProductImage(
                    existingProduct.getId(),
                    ProductImageDTO.builder()
                            .imageUrl(filename)
                            .build()
            );
            productImages.add(productImage);
        }

        return ResponseEntity.ok().body(Response.builder()
                .message("Upload image successfully")
                .status(HttpStatus.CREATED)
                .data(productImages)
                .build());
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                logger.info("{} not found", imageName);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving image: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<Response> getProducts(
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

        Page<ProductResponse> productPage = productService
                .getAllProducts(keyword, categoryId, pageRequest);
        // Lấy tổng số trang
        totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        // Bổ sung totalPages vào các đối tượng ProductResponse
        for (ProductResponse product : products) {
            product.setTotalPages(totalPages);
        }

        ListProductResponse productListResponse = ListProductResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok().body(Response.builder()
                .message("Get products successfully")
                .status(HttpStatus.OK)
                .data(productListResponse)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getProductById(
            @PathVariable("id") Long productId
    ) throws Exception {
        Product existingProduct = productService.getProductById(productId);
        return ResponseEntity.ok(Response.builder()
                .data(ProductResponse.fromProduct(existingProduct))
                .message("Get detail product successfully")
                .status(HttpStatus.OK)
                .build());

    }

    @GetMapping("/ids")
    public ResponseEntity<Response> getProductsByIds(@RequestParam("ids") List<Long> ids) {
        //eg: 1,3,5,7

        List<Product> products = productService.findProductsByIds(ids);
        return ResponseEntity.ok(Response.builder()
                .data(products.stream().map(ProductResponse::fromProduct).toList())
                .message("Get products successfully")
                .status(HttpStatus.OK)
                .build()
        );
    }


    @PostMapping("/generateFakeProducts")
    public ResponseEntity<Response> generateFakeProducts(
            @RequestParam(defaultValue = "1000") int numberOfProducts
    ) throws Exception {
        Faker faker = new Faker();

        for (int i = 0; i < numberOfProducts; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .quantity(faker.number().numberBetween(1, 40))
                    .categoryId((long)faker.number().numberBetween(7, 9))
                    .build();
            productService.createProduct(productDTO);
        }

        return ResponseEntity.ok(Response.builder()
                .message("Insert fake products succcessfully")
                .data(null)
                .status(HttpStatus.OK)
                .build());
    }
}
