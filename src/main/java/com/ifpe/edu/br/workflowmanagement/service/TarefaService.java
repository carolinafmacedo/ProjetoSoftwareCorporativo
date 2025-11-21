package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.*;
import com.ifpe.edu.br.workflowmanagement.service.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final EtapaRepository etapaRepository;
    private final RegistroHorasService registroHorasService;
    private final ComentarioService comentarioService;

    @Autowired
    public TarefaService(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository,
                         ProjetoRepository projetoRepository, EtapaRepository etapaRepository,
                         RegistroHorasService registroHorasService, ComentarioService comentarioService) { 
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.projetoRepository = projetoRepository;
        this.etapaRepository = etapaRepository;
        this.registroHorasService = registroHorasService;
        this.comentarioService = comentarioService;
    }

    @Transactional
    public Tarefa criarTarefa(String titulo, String descricao, Long idProjeto, Long idResponsavel, Long idCriador) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        
        if (projeto.getFluxoTrabalho() == null) {
            throw new IllegalStateException("Projeto sem fluxo de trabalho associado.");
        }

        // Pega primeira etapa
        Etapa primeiraEtapa = etapaRepository.findFirstByFluxoTrabalhoIdOrderByOrdemAsc(projeto.getFluxoTrabalho().getId())
                .orElseThrow(() -> new IllegalStateException("Fluxo sem etapas."));

        Usuario responsavel = null;
        if (idResponsavel != null) {
            responsavel = usuarioRepository.findById(idResponsavel)
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado."));
        }

        Tarefa novaTarefa = new Tarefa(titulo, descricao, projeto, responsavel, primeiraEtapa);
        return tarefaRepository.save(novaTarefa);
    }

    @Transactional
    public Tarefa moverTarefaParaEtapa(Long idTarefa, Long idNovaEtapa, Long idUsuarioExecutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa).orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        Etapa novaEtapa = etapaRepository.findById(idNovaEtapa).orElseThrow(() -> new RuntimeException("Etapa não encontrada."));
        
        // Validação de fluxo
        if (!tarefa.getProjeto().getFluxoTrabalho().getId().equals(novaEtapa.getFluxoTrabalho().getId())) {
            throw new IllegalStateException("A etapa não pertence ao fluxo deste projeto.");
        }

        tarefa.setEtapaAtual(novaEtapa);
        
        // Lógica simples de conclusão
        if (novaEtapa.getNome().toLowerCase().contains("concluído") || novaEtapa.getNome().toLowerCase().contains("done")) {
            tarefa.setDataConclusao(LocalDateTime.now());
        }

        return tarefaRepository.save(tarefa);
    }

    // Métodos delegados corrigidos para BigDecimal
    @Transactional
    public RegistroHoras registrarHoras(Long idTarefa, BigDecimal horas, LocalDate data, Long idUsuario) {
        return registroHorasService.registrarHoras(idTarefa, horas, data, idUsuario);
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarHorasGastas(Long idTarefa) {
        return registroHorasService.consultarTotalHorasPorTarefa(idTarefa);
    }

    @Transactional
    public Comentario adicionarComentario(Long idTarefa, String texto, Long idAutor) {
        return comentarioService.criarComentario(idTarefa, texto, idAutor);
    }
}