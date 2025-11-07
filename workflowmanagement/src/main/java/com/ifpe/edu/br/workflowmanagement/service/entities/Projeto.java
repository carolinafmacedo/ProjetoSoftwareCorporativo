package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList; // Import para inicializar a lista

@Entity
@Table(name = "projetos")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    // Relacionamento com Usuário (gerente/responsável pelo projeto)
    // Assume-se que um projeto tem um criador/gerente principal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id", nullable = false)
    private Usuario gerente; // "cria/gerencia" e "recebe" (responsável)

    // Relacionamento com FluxoTrabalho (um projeto utiliza um fluxo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluxo_trabalho_id") // Pode ser nulo se o fluxo for opcional ou definido depois
    private FluxoTrabalho fluxoTrabalho; // "utiliza"

    // Relacionamento com Tarefa (um projeto contém muitas tarefas)
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefa> tarefas = new ArrayList<>(); // "contém"

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    // Construtor padrão (necessário para JPA)
    public Projeto() {
    }

    // Construtor com campos
    public Projeto(String nome, String descricao, Usuario gerente, FluxoTrabalho fluxoTrabalho) {
        this.nome = nome;
        this.descricao = descricao;
        this.gerente = gerente;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Usuario getGerente() {
        return gerente;
    }

    public void setGerente(Usuario gerente) {
        this.gerente = gerente;
    }

    public FluxoTrabalho getFluxoTrabalho() {
        return fluxoTrabalho;
    }

    public void setFluxoTrabalho(FluxoTrabalho fluxoTrabalho) {
        this.fluxoTrabalho = fluxoTrabalho;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    // Métodos auxiliares para gerenciar a lista de tarefas
    public void addTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
        tarefa.setProjeto(this);
    }

    public void removeTarefa(Tarefa tarefa) {
        tarefas.remove(tarefa);
        tarefa.setProjeto(null);
    }
}