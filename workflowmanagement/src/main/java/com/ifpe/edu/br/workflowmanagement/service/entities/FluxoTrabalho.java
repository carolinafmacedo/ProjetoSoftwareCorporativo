package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "fluxos_trabalho")
public class FluxoTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    // Relacionamento com Etapa (um fluxo de trabalho possui muitas etapas)
    @OneToMany(mappedBy = "fluxoTrabalho", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC") // Garante que as etapas são recuperadas na ordem correta
    private List<Etapa> etapas = new ArrayList<>(); // "possui"

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    // Construtor padrão (necessário para JPA)
    public FluxoTrabalho() {
    }

    // Construtor com campos
    public FluxoTrabalho(String nome) {
        this.nome = nome;
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

    public List<Etapa> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<Etapa> etapas) {
        this.etapas = etapas;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    // Métodos auxiliares para gerenciar a lista de etapas
    public void addEtapa(Etapa etapa) {
        etapas.add(etapa);
        etapa.setFluxoTrabalho(this);
    }

    public void removeEtapa(Etapa etapa) {
        etapas.remove(etapa);
        etapa.setFluxoTrabalho(null);
    }
}