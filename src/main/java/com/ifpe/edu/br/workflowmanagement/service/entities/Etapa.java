package com.ifpe.edu.br.workflowmanagement.service.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "etapas")
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da etapa é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private int ordem;

    @JsonIgnore
    @NotNull(message = "A etapa deve pertencer a um fluxo de trabalho")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluxo_trabalho_id", nullable = false)
    private FluxoTrabalho fluxoTrabalho;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    protected Etapa() {}

    public Etapa(String nome, int ordem, FluxoTrabalho fluxoTrabalho) {
        this.nome = nome;
        this.ordem = ordem;
        this.fluxoTrabalho = fluxoTrabalho;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }

    public FluxoTrabalho getFluxoTrabalho() { return fluxoTrabalho; }
    public void setFluxoTrabalho(FluxoTrabalho fluxoTrabalho) { this.fluxoTrabalho = fluxoTrabalho; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}