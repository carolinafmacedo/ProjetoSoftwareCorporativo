package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.RegistroHoras;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.RegistroHorasRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.TarefaRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistroHorasService {

    private final RegistroHorasRepository registroHorasRepository;
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public RegistroHorasService(RegistroHorasRepository registroHorasRepository,
                                TarefaRepository tarefaRepository,
                                UsuarioRepository usuarioRepository) {
        this.registroHorasRepository = registroHorasRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * CU 14: Cria um novo registro de horas em uma tarefa.
     */
    @Transactional
    public RegistroHoras registrarHoras(Long idTarefa, float horas, LocalDate data, Long idUsuario) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
            .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Regra de Negócio: O usuário que registra deve ser o responsável pela tarefa.
        if (tarefa.getResponsavel() == null || !tarefa.getResponsavel().getId().equals(usuario.getId())) {
             throw new SecurityException("Apenas o responsável atual pela tarefa pode registrar horas.");
        }

        RegistroHoras novoRegistro = new RegistroHoras(usuario, tarefa, horas, data);
        return registroHorasRepository.save(novoRegistro);
    }

    /**
     * Permite que um usuário edite um registro de horas que ele mesmo criou.
     */
    @Transactional
    public RegistroHoras editarRegistro(Long idRegistro, float novasHoras, LocalDate novaData, Long idUsuarioExecutor) {
        RegistroHoras registro = registroHorasRepository.findById(idRegistro)
            .orElseThrow(() -> new RuntimeException("Registro de horas não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Regra de Negócio: Apenas o autor do registro pode editá-lo.
        if (!registro.getUsuario().getId().equals(executor.getId())) {
            throw new SecurityException("Usuário não tem permissão para editar este registro de horas.");
        }

        registro.setHoras(novasHoras);
        registro.setDataRegistro(novaData);
        return registroHorasRepository.save(registro);
    }

    /**
     * Permite que um usuário exclua um registro de horas que ele mesmo criou.
     */
    @Transactional
    public void excluirRegistro(Long idRegistro, Long idUsuarioExecutor) {
        RegistroHoras registro = registroHorasRepository.findById(idRegistro)
            .orElseThrow(() -> new RuntimeException("Registro de horas não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Regra de Negócio: Apenas o autor ou o gerente do projeto podem excluir.
        boolean isAutor = registro.getUsuario().getId().equals(executor.getId());
        boolean isGerenteDoProjeto = registro.getTarefa().getProjeto().getGerente().getId().equals(executor.getId());

        if (!isAutor && !isGerenteDoProjeto) {
             throw new SecurityException("Usuário não tem permissão para excluir este registro.");
        }

        registroHorasRepository.deleteById(idRegistro);
    }

    /**
     * CU 15: Consulta o total de horas gastas em uma tarefa específica.
     */
    @Transactional(readOnly = true)
    public float consultarTotalHorasPorTarefa(Long idTarefa) {
        Float total = registroHorasRepository.sumHorasByTarefaId(idTarefa);
        return total != null ? total : 0.0f;
    }

    /**
     * CU 15 / 16: Consulta o total de horas gastas em um projeto inteiro.
     */
    @Transactional(readOnly = true)
    public float consultarTotalHorasPorProjeto(Long idProjeto) {
        Float total = registroHorasRepository.sumHorasByProjetoId(idProjeto);
        return total != null ? total : 0.0f;
    }

    /**
     * CU 15 / 16: Busca todos os registros de um usuário em um determinado período.
     */
    @Transactional(readOnly = true)
    public List<RegistroHoras> buscarRegistrosPorUsuarioEPeriodo(Long idUsuario, LocalDate inicio, LocalDate fim) {
        return registroHorasRepository.findByUsuarioIdAndDataRegistroBetween(idUsuario, inicio, fim);
    }
}