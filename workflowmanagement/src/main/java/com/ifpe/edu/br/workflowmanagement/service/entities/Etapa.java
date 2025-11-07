package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "etapas")
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private int ordem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluxo_trabalho_id", nullable = false)
    private FluxoTrabalho fluxoTrabalho; // "está em"

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    // Construtor padrão (necessário para JPA)
    public Etapa() {
    }

    // Construtor com campos
    public Etapa(String nome, int ordem, FluxoTrabalho fluxoTrabalho) {
        this.nome = nome;
        this.ordem = ordem;
        this.fluxoTrabalho = fluxoTrabalho;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public FluxoTrabalho getFluxoTrabalho() {
        return fluxoTrabalho;
    }

    public void setFluxoTrabalho(FluxoTrabalho fluxoTrabalho) {
        this.fluxoTrabalho = fluxoTrabalho;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}