package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.*;
import com.ifpe.edu.br.workflowmanagement.service.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final EtapaRepository etapaRepository;
    private final ComentarioRepository comentarioRepository;
    private final RegistroHorasRepository registroHorasRepository;
    private final ComentarioService comentarioService;
    private final RegistroHorasService registroHorasService;
    

    @Autowired
    public TarefaService(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository,
                         ProjetoRepository projetoRepository, EtapaRepository etapaRepository,
                         ComentarioRepository comentarioRepository, RegistroHorasRepository registroHorasRepository,
                         ComentarioService comentarioService) { 
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.projetoRepository = projetoRepository;
        this.etapaRepository = etapaRepository;
        this.comentarioRepository = comentarioRepository;
        this.registroHorasRepository = registroHorasRepository;
        this.comentarioService = comentarioService;
    }

    /**
     * CU 9: Cria uma nova tarefa em um projeto.
     * A tarefa é automaticamente colocada na primeira etapa do fluxo de trabalho do projeto.
     */
    @Transactional
    public Tarefa criarTarefa(String titulo, String descricao, Long idProjeto, Long idResponsavel, Long idCriador) {
        Usuario criador = usuarioRepository.findById(idCriador)
                .orElseThrow(() -> new RuntimeException("Usuário criador não encontrado."));
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        
        if (projeto.getFluxoTrabalho() == null) {
            throw new IllegalStateException("O projeto precisa ter um fluxo de trabalho associado para criar tarefas.");
        }

        // Verifica permissão do criador
        if (!temPermissao(criador, "CRIAR_TAREFA")) {
            throw new SecurityException("Usuário não tem permissão para criar tarefas.");
        }

        // Encontra a primeira etapa do fluxo de trabalho do projeto
        Etapa primeiraEtapa = etapaRepository.findFirstByFluxoTrabalhoIdOrderByOrdemAsc(projeto.getFluxoTrabalho().getId())
                .orElseThrow(() -> new IllegalStateException("O fluxo de trabalho não possui uma etapa inicial."));

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo(titulo);
        novaTarefa.setDescricao(descricao);
        novaTarefa.setProjeto(projeto);
        novaTarefa.setEtapaAtual(primeiraEtapa);

        if (idResponsavel != null) {
            Usuario responsavel = usuarioRepository.findById(idResponsavel)
                .orElseThrow(() -> new RuntimeException("Usuário responsável não encontrado."));
            novaTarefa.setResponsavel(responsavel);
        }

        return tarefaRepository.save(novaTarefa);
    }

    /**
     * CU 12: Move uma tarefa para uma nova etapa do fluxo de trabalho.
     */
    @Transactional
    public Tarefa moverTarefaParaEtapa(Long idTarefa, Long idNovaEtapa, Long idUsuarioExecutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        Etapa novaEtapa = etapaRepository.findById(idNovaEtapa)
                .orElseThrow(() -> new RuntimeException("Nova etapa não encontrada."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        
        // Validação: A nova etapa pertence ao mesmo fluxo de trabalho da tarefa?
        if (!tarefa.getProjeto().getFluxoTrabalho().getId().equals(novaEtapa.getFluxoTrabalho().getId())) {
            throw new IllegalStateException("A etapa de destino não pertence ao fluxo de trabalho do projeto.");
        }

        // Verificação de permissão (ex: responsável, gerente do projeto ou admin)
        if (!isResponsavelOuGerente(executor, tarefa)) {
             throw new SecurityException("Usuário não tem permissão para mover esta tarefa.");
        }

        tarefa.setEtapaAtual(novaEtapa);
        
        // Opcional: Se a etapa for a última ("Done", "Concluído"), registrar a data de conclusão
        // Esta lógica depende de como você identifica a última etapa.
        if ("concluído".equalsIgnoreCase(novaEtapa.getNome())) {
            tarefa.setDataConclusao(LocalDateTime.now());
        }

        return tarefaRepository.save(tarefa);
    }

    /**
     * CU 13: Define ou altera o responsável por uma tarefa.
     */

    @Transactional
    public Tarefa definirResponsavel(Long idTarefa, Long idNovoResponsavel, Long idUsuarioExecutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
            .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        Usuario novoResponsavel = usuarioRepository.findById(idNovoResponsavel)
            .orElseThrow(() -> new RuntimeException("Novo usuário responsável não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        
        // Apenas o gerente do projeto ou um admin pode alterar o responsável
        if (!isGerenteDoProjeto(executor, tarefa.getProjeto()) && !temPermissao(executor, "DEFINIR_QUALQUER_RESPONSAVEL")) {
             throw new SecurityException("Usuário não tem permissão para alterar o responsável desta tarefa.");
        }

        tarefa.setResponsavel(novoResponsavel);
        return tarefaRepository.save(tarefa);
    }

    

    /**
     * CU 20: Adiciona um comentário a uma tarefa.
     */
    @Transactional
    public Comentario adicionarComentario(Long idTarefa, String texto, Long idAutor) {

    // A lógica de busca e validação agora está centralizada no ComentarioService.
        return comentarioService.criarComentario(idTarefa, texto, idAutor);

    }

    /**
     * CU 14: Registra horas gastas em uma tarefa, delegando para o RegistroHorasService.
     */
    @Transactional
    public RegistroHoras registrarHoras(Long idTarefa, float horas, LocalDate data, Long idUsuario) {
        return registroHorasService.registrarHoras(idTarefa, horas, data, idUsuario);
    }

    /**
     * CU 15: Consulta o total de horas gastas em uma tarefa, delegando para o RegistroHorasService.
     */
    @Transactional(readOnly = true)
    public Float consultarHorasGastas(Long idTarefa) {
        return registroHorasService.consultarTotalHorasPorTarefa(idTarefa);
    }

    
    
    // --- MÉTODOS AUXILIARES E DE VERIFICAÇÃO ---

    private boolean temPermissao(Usuario usuario, String permissao) {
        return usuario.getPapel() != null && usuario.getPapel().getPermissoes().contains(permissao);
    }
    
    private boolean isGerenteDoProjeto(Usuario usuario, Projeto projeto) {
        return projeto.getGerente().getId().equals(usuario.getId());
    }

    private boolean isResponsavelOuGerente(Usuario usuario, Tarefa tarefa) {
        boolean isResponsavel = tarefa.getResponsavel() != null && tarefa.getResponsavel().getId().equals(usuario.getId());
        boolean isGerente = isGerenteDoProjeto(usuario, tarefa.getProjeto());
        return isResponsavel || isGerente || temPermissao(usuario, "MOVER_QUALQUER_TAREFA");
    }
}