package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título da tarefa é obrigatório")
    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @NotNull(message = "A tarefa deve pertencer a um projeto")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_atual_id")
    private Etapa etapaAtual;

    // Listas inicializadas para evitar NullPointerException
    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroHoras> registrosHoras = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    protected Tarefa() {}

    public Tarefa(String titulo, String descricao, Projeto projeto, Usuario responsavel, Etapa etapaAtual) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.projeto = projeto;
        this.responsavel = responsavel;
        this.etapaAtual = etapaAtual;
    }

    // Métodos Auxiliares (Helpers)
    public void adicionarComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setTarefa(this);
    }

    public void removerComentario(Comentario comentario) {
        comentarios.remove(comentario);
        comentario.setTarefa(null);
    }

    public void adicionarRegistroHoras(RegistroHoras registro) {
        registrosHoras.add(registro);
        registro.setTarefa(this);
    }

    public void removerRegistroHoras(RegistroHoras registro) {
        registrosHoras.remove(registro);
        registro.setTarefa(null);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }

    public Etapa getEtapaAtual() { return etapaAtual; }
    public void setEtapaAtual(Etapa etapaAtual) { this.etapaAtual = etapaAtual; }

    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }

    public List<RegistroHoras> getRegistrosHoras() { return registrosHoras; }
    public void setRegistrosHoras(List<RegistroHoras> registrosHoras) { this.registrosHoras = registrosHoras; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}