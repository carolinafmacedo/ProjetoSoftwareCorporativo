package com.ifpe.edu.br.workflowmanagement.service.DTO;

/**
 * Data Transfer Object (DTO) para receber os dados
 * do formulário de cadastro de um novo usuário.
 */
public class CadastroUsuarioDTO {
    
    private String nome;
    private String email;
    private String senha;
    private Long papelId; // ID do papel que o usuário terá

    // Getters e Setters
    // Métodos para obter (get) e definir (set) os valores dos atributos.

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Long getPapelId() {
        return papelId;
    }

    public void setPapelId(Long papelId) {
        this.papelId = papelId;
    }
}