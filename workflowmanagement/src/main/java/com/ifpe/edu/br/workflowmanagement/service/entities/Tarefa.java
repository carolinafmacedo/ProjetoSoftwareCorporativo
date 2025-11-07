package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao; // Adicionado para refletir o diagrama (dataConclusao)

    // Relacionamento com Projeto (muitas tarefas pertencem a um projeto)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto; // "contém" no lado do Projeto

    // Relacionamento com Usuário (uma tarefa é atribuída a um usuário)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id") // Pode ser nulo se a tarefa ainda não tiver um responsável
    private Usuario responsavel; // Relacionamento no diagrama, "possui"

    // Relacionamento com Etapa (uma tarefa está em uma etapa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_atual_id") // Pode ser nulo, ou etapa inicial padrão
    private Etapa etapaAtual; // "está em" no lado da Etapa

    // Relacionamento com Comentário (uma tarefa tem muitos comentários)
    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>(); // "registra" / "escreve" (usuário escreve em tarefa)

    // Relacionamento com RegistroHoras (uma tarefa tem muitos registros de horas)
    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroHoras> registrosHoras = new ArrayList<>(); // "registra" (usuário registra horas para tarefa)

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    // Construtor padrão (necessário para JPA)
    public Tarefa() {
    }

    // Construtor com campos
    public Tarefa(String titulo, String descricao, Projeto projeto, Usuario responsavel, Etapa etapaAtual) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.projeto = projeto;
        this.responsavel = responsavel;
        this.etapaAtual = etapaAtual;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
    }

    public Etapa getEtapaAtual() {
        return etapaAtual;
    }

    public void setEtapaAtual(Etapa etapaAtual) {
        this.etapaAtual = etapaAtual;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public List<RegistroHoras> getRegistrosHoras() {
        return registrosHoras;
    }

    public void setRegistrosHoras(List<RegistroHoras> registrosHoras) {
        this.registrosHoras = registrosHoras;
    }

    // Métodos auxiliares para gerenciar a lista de comentários
    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setTarefa(this);
    }

    public void removeComentario(Comentario comentario) {
        comentarios.remove(comentario);
        comentario.setTarefa(null);
    }

    // Métodos auxiliares para gerenciar a lista de registros de horas
    public void addRegistroHoras(RegistroHoras registro) {
        registrosHoras.add(registro);
        registro.setTarefa(this);
    }

    public void removeRegistroHoras(RegistroHoras registro) {
        registrosHoras.remove(registro);
        registro.setTarefa(null);
    }
}