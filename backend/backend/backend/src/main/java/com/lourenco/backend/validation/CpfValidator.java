package com.lourenco.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// CORREÇÃO: Adicionado <ValidCPF, String> aqui
public class CpfValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public void initialize(ValidCPF constraintAnnotation) {
        // Método de inicialização
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // 1. Se for null ou vazio, consideramos válido (deixe o @NotBlank validar isso)
        if (cpf == null || cpf.isEmpty()) {
            return true;
        }

        // 2. Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // 3. Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // 4. Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // 5. Calcula os dígitos verificadores
        try {
            // Cálculo do 1º dígito
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) {
                primeiroDigito = 0;
            }

            // Verifica o 1º dígito
            if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
                return false;
            }

            // Cálculo do 2º dígito
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) {
                segundoDigito = 0;
            }

            // Verifica o 2º dígito e retorna
            return Character.getNumericValue(cpf.charAt(10)) == segundoDigito;

        } catch (Exception e) {
            return false;
        }
    }
}