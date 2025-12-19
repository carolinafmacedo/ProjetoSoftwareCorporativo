package com.ifpe.edu.br.workflowmanagement.service.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CadastroUsuarioDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotNull(message = "O ID do papel é obrigatório")
    private Long papelId;

    // Construtor padrão (necessário para o Jackson/JSON transformar o JSON em Objeto)
    public CadastroUsuarioDTO() {}

    public CadastroUsuarioDTO(String nome, String email, String senha, Long papelId) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.papelId = papelId;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Long getPapelId() { return papelId; }
    public void setPapelId(Long papelId) { this.papelId = papelId; }
}