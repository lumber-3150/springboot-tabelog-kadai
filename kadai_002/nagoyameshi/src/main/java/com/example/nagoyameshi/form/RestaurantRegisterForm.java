package com.example.nagoyameshi.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantRegisterForm {
	@NotBlank(message = "店舗名を入力してください。")
    private String name;
        
    private MultipartFile imageFile;
    
    @NotBlank(message = "説明を入力してください。")
    private String description;   
    
    @NotNull(message = "最低料金を入力してください。")
    @Min(value = 1, message = "最低料金は1円以上に設定してください。")
    private Integer lowest_price;  
    
    @NotNull(message = "最高料金を入力してください。")
    @Min(value = 1, message = "最高料金は1円以上に設定してください。")
    private Integer highest_price; 
    
    @NotNull(message = "定員を入力してください。")
    @Min(value = 1, message = "定員は1人以上に設定してください。")
    private Integer seating_capacity;     
    
    @NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    private List<Integer> categoryIds;
    
//    @NotBlank(message = "電話番号を入力してください。")
//    private String phoneNumber;
}
