package com.taild.simpleshopapp.dtos.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTO {
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    // Password may not be needed for social login but required for traditional login
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @JsonProperty("role_id")
    private Long roleId;

    // Facebook Account Id, not mandatory, can be blank
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    // Google Account Id, not mandatory, can be blank
    @JsonProperty("google_account_id")
    private String googleAccountId;

    //For Google, Facebook login
    // Full name, not mandatory, can be blank
    @JsonProperty("fullname")
    private String fullname;

    // Profile image URL, not mandatory, can be blank
    @JsonProperty("profile_image")
    private String profileImage;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
    // Kiểm tra facebookAccountId có hợp lệ không
    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    // Kiểm tra googleAccountId có hợp lệ không
    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
