package ru.feniks.franchise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FranchiseLeadRequest(
    @NotBlank @Size(max = 200) String fullName,
    @NotBlank @Size(max = 50) String phone,
    @NotBlank @Email @Size(max = 200) String email,
    @NotBlank @Size(max = 200) String city) {}
