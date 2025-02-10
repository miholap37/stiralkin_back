package com.mailsender.mailSender.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    private String age;
    private String problem;
    private String brand;
    private String service;
    private String discountName;
}
