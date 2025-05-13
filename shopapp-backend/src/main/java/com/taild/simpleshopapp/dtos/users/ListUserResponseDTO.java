package com.taild.simpleshopapp.dtos.users;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListUserResponseDTO {
    private List<UserResponseDTO> users;
    private int totalPages;
}
