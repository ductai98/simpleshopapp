package com.taild.simpleshopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 100000000, message = "Price must be less than or equal to 100,000,000")
    private Float price;

    @NotBlank(message = "Quantity is required")
    private int quantity;

    private String thumbnail;

    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    private int totalPages;

    private MultipartFile productImageFile;

}
