package com.ifpe.edu.br.workflowmanagement.service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CriarTarefaDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "O ID do projeto é obrigatório")
    private Long projetoId;

    private Long responsavelId; // Opcional na criação

    @NotNull(message = "O ID do criador é obrigatório")
    private Long criadorId;

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }
    public Long getCriadorId() { return criadorId; }
    public void setCriadorId(Long criadorId) { this.criadorId = criadorId; }
}