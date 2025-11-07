package com.ifpe.edu.br.workflowmanagement.service.repositories;

import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    // Encontra todas as tarefas de um projeto específico
    List<Tarefa> findByProjetoId(Long projetoId);

    // Encontra todas as tarefas atribuídas a um usuário específico
    List<Tarefa> findByResponsavelId(Long responsavelId);

private final ComentarioService comentarioService;

@Autowired
public TarefaService(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository,
                     ProjetoRepository projetoRepository, EtapaRepository etapaRepository,
                     ComentarioRepository comentarioRepository, RegistroHorasRepository registroHorasRepository,
                     ComentarioService comentarioService) { // Adicionar aqui
    this.tarefaRepository = tarefaRepository;
    this.usuarioRepository = usuarioRepository;
    this.projetoRepository = projetoRepository;
    this.etapaRepository = etapaRepository;
    this.comentarioRepository = comentarioRepository; // Pode ser removido se não for mais usado diretamente
    this.registroHorasRepository = registroHorasRepository;
    this.comentarioService = comentarioService; // Adicionar aqui
}