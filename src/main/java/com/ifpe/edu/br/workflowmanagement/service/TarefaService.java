package com.ifpe.edu.br.workflowmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifpe.edu.br.workflowmanagement.service.entities.Comentario;
import com.ifpe.edu.br.workflowmanagement.service.entities.Etapa;
import com.ifpe.edu.br.workflowmanagement.service.entities.Projeto;
import com.ifpe.edu.br.workflowmanagement.service.entities.RegistroHoras;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.EtapaRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.ProjetoRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.TarefaRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;

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

    @Transactional(readOnly = true)
    public Optional<Tarefa> buscarPorId(Long id) {
        return tarefaRepository.findById(id);
    }

    @Transactional
    public Tarefa criarTarefa(String titulo, String descricao, Long idProjeto, Long idResponsavel, Long idCriador) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        
        if (projeto.getFluxoTrabalho() == null) {
            throw new IllegalStateException("Projeto sem fluxo de trabalho associado.");
        }

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
        
        if (!tarefa.getProjeto().getFluxoTrabalho().getId().equals(novaEtapa.getFluxoTrabalho().getId())) {
            throw new IllegalStateException("A etapa não pertence ao fluxo deste projeto.");
        }

        // --- NOVO: Captura o nome da etapa antiga para o histórico ---
        String nomeEtapaAntiga = tarefa.getEtapaAtual().getNome();

        tarefa.setEtapaAtual(novaEtapa);
        
        if (novaEtapa.getNome().toLowerCase().contains("concluído") || novaEtapa.getNome().toLowerCase().contains("done")) {
            tarefa.setDataConclusao(LocalDateTime.now());
        }

        // --- NOVO: Registra o histórico ---
        comentarioService.criarComentario(idTarefa, "Moveu a tarefa de '" + nomeEtapaAntiga + "' para '" + novaEtapa.getNome() + "'", idUsuarioExecutor);

        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public Tarefa editarTarefa(Long idTarefa, String novoTitulo, String novaDescricao, Long idUsuarioExecutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        
        // Validação de permissão (Opcional: checar se é gerente ou criador)
        
        if (novoTitulo != null && !novoTitulo.isBlank()) {
            tarefa.setTitulo(novoTitulo);
        }
        if (novaDescricao != null) {
            tarefa.setDescricao(novaDescricao);
        }
        
        return tarefaRepository.save(tarefa);
    }

    @Transactional
    public Tarefa definirResponsavel(Long idTarefa, Long idNovoResponsavel, Long idUsuarioExecutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        
        Usuario novoResponsavel = null;
        if (idNovoResponsavel != null) {
            novoResponsavel = usuarioRepository.findById(idNovoResponsavel)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        }

        tarefa.setResponsavel(novoResponsavel);
        
        // Histórico automático
        String nomeResp = (novoResponsavel != null) ? novoResponsavel.getNome() : "Ninguém";
        comentarioService.criarComentario(idTarefa, "Responsável alterado para: " + nomeResp, idUsuarioExecutor);
        
        return tarefaRepository.save(tarefa);
    }

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

    @Transactional
    public void excluirTarefa(Long idTarefa, Long idUsuarioExecutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado."));

        boolean isGerente = tarefa.getProjeto().getGerente().getId().equals(idUsuarioExecutor);
        boolean isResponsavel = tarefa.getResponsavel() != null && tarefa.getResponsavel().getId().equals(idUsuarioExecutor);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(executor.getPapel().getNome());

        if (!isGerente && !isResponsavel && !isAdmin) {
            throw new SecurityException("Você não tem permissão para excluir esta tarefa.");
        }

        tarefaRepository.delete(tarefa);
    }
}