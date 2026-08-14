package com.example.nagoyameshi.form;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
	@NotNull(message = "予約日時を選択してください。")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservedDatetime;    
    
    @NotNull(message = "宿泊人数を入力してください。")
    @Min(value = 1, message = "宿泊人数は1人以上に設定してください。")
    private Integer numberOfPeople; 

    
}
