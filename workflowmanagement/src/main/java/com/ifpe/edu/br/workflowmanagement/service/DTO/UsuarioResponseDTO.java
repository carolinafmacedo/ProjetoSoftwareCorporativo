package com.ifpe.edu.br.workflowmanagement.service.DTO;

public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String papel; // Nome do papel, não o objeto inteiro

    // Construtor, Getters e Setters
    // É uma boa prática ter um construtor que recebe um objeto Usuario
    public UsuarioResponseDTO(Long id, String nome, String email, String papel) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.papel = papel;
    }
}