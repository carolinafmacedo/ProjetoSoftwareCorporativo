package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import java.time.LocalDate; // Usar LocalDate para representar apenas a data
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_horas")
public class RegistroHoras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // "possui"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa; // "registra"

    @Column(nullable = false)
    private float horas; // Tipo float para horas pode ser ajustado para BigDecimal se precisar de maior precisão monetária

    @Column(nullable = false)
    private LocalDate dataRegistro; // Usar LocalDate para a data de registro

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    // Construtor padrão (necessário para JPA)
    public RegistroHoras() {
    }

    // Construtor com campos
    public RegistroHoras(Usuario usuario, Tarefa tarefa, float horas, LocalDate dataRegistro) {
        this.usuario = usuario;
        this.tarefa = tarefa;
        this.horas = horas;
        this.dataRegistro = dataRegistro;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public float getHoras() {
        return horas;
    }

    public void setHoras(float horas) {
        this.horas = horas;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}