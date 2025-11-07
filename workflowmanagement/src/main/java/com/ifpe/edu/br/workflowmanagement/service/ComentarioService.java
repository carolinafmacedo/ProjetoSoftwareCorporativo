package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.Comentario;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.ComentarioRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.TarefaRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ComentarioService(ComentarioRepository comentarioRepository,
                             TarefaRepository tarefaRepository,
                             UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Cria um novo comentário em uma tarefa.
     * @param idTarefa O ID da tarefa a ser comentada.
     * @param texto O conteúdo do comentário.
     * @param idAutor O ID do usuário que está criando o comentário.
     * @return O comentário salvo.
     */
    @Transactional
    public Comentario criarComentario(Long idTarefa, String texto, Long idAutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
            .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        Usuario autor = usuarioRepository.findById(idAutor)
            .orElseThrow(() -> new RuntimeException("Usuário autor não encontrado."));

        // Regra de Negócio: aqui você poderia verificar se o autor faz parte da equipe do projeto, etc.
        // Por enquanto, vamos assumir que se ele pode acessar a tarefa, ele pode comentar.

        Comentario novoComentario = new Comentario(autor, tarefa, texto);
        return comentarioRepository.save(novoComentario);
    }

    /**
     * Edita o texto de um comentário existente.
     * @param idComentario O ID do comentário a ser editado.
     * @param novoTexto O novo conteúdo do comentário.
     * @param idUsuarioExecutor O ID do usuário que está tentando editar.
     * @return O comentário atualizado.
     */
    @Transactional
    public Comentario editarComentario(Long idComentario, String novoTexto, Long idUsuarioExecutor) {
        Comentario comentario = comentarioRepository.findById(idComentario)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verificação de Permissão: Apenas o autor original do comentário pode editá-lo.
        if (!comentario.getAutor().getId().equals(executor.getId())) {
            // Em um sistema mais complexo, um admin ou gerente de projeto também poderia editar.
            // if (!temPermissao(executor, "EDITAR_QUALQUER_COMENTARIO")) ...
            throw new SecurityException("Usuário não tem permissão para editar este comentário.");
        }

        comentario.setText(novoTexto);
        return comentarioRepository.save(comentario);
    }

    /**
     * Exclui um comentário.
     * @param idComentario O ID do comentário a ser excluído.
     * @param idUsuarioExecutor O ID do usuário que está tentando excluir.
     */
    @Transactional
    public void excluirComentario(Long idComentario, Long idUsuarioExecutor) {
        Comentario comentario = comentarioRepository.findById(idComentario)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verificação de Permissão: Apenas o autor ou o gerente do projeto podem excluir.
        boolean isAutor = comentario.getAutor().getId().equals(executor.getId());
        boolean isGerenteDoProjeto = comentario.getTarefa().getProjeto().getGerente().getId().equals(executor.getId());
        
        if (!isAutor && !isGerenteDoProjeto) {
             throw new SecurityException("Usuário não tem permissão para excluir este comentário.");
        }

        comentarioRepository.deleteById(idComentario);
    }

    /**
     * Busca todos os comentários de uma tarefa específica, em ordem de criação.
     * @param idTarefa O ID da tarefa.
     * @return Uma lista de comentários.
     */
    @Transactional(readOnly = true)
    public List<Comentario> buscarComentariosPorTarefa(Long idTarefa) {
        if (!tarefaRepository.existsById(idTarefa)) {
            throw new RuntimeException("Tarefa não encontrada.");
        }
        return comentarioRepository.findByTarefaIdOrderByDataCriacaoAsc(idTarefa);
    }
}