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

import java.math.BigDecimal; // Importante!
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

    @Transactional
    public RegistroHoras registrarHoras(Long idTarefa, BigDecimal horas, LocalDate data, Long idUsuario) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
            .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verifica se quem está registrando é o responsável
        if (tarefa.getResponsavel() == null || !tarefa.getResponsavel().getId().equals(usuario.getId())) {
             throw new SecurityException("Apenas o responsável atual pode registrar horas.");
        }

        RegistroHoras novoRegistro = new RegistroHoras(usuario, tarefa, horas, data);
        return registroHorasRepository.save(novoRegistro);
    }

    @Transactional
    public RegistroHoras editarRegistro(Long idRegistro, BigDecimal novasHoras, LocalDate novaData, Long idUsuarioExecutor) {
        RegistroHoras registro = registroHorasRepository.findById(idRegistro)
            .orElseThrow(() -> new RuntimeException("Registro não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!registro.getUsuario().getId().equals(executor.getId())) {
            throw new SecurityException("Sem permissão para editar este registro.");
        }

        registro.setHoras(novasHoras);
        registro.setDataRegistro(novaData);
        return registroHorasRepository.save(registro);
    }

    @Transactional
    public void excluirRegistro(Long idRegistro, Long idUsuarioExecutor) {
        RegistroHoras registro = registroHorasRepository.findById(idRegistro)
            .orElseThrow(() -> new RuntimeException("Registro não encontrado."));
        
        boolean isAutor = registro.getUsuario().getId().equals(idUsuarioExecutor);
        // Verifica hierarquia (se é autor ou gerente)
        boolean isGerente = registro.getTarefa().getProjeto().getGerente().getId().equals(idUsuarioExecutor);

        if (!isAutor && !isGerente) {
             throw new SecurityException("Sem permissão para excluir.");
        }

        registroHorasRepository.deleteById(idRegistro);
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarTotalHorasPorTarefa(Long idTarefa) {
        BigDecimal total = registroHorasRepository.sumHorasByTarefaId(idTarefa);
        return total != null ? total : BigDecimal.ZERO; // Evita NullPointerException
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarTotalHorasPorProjeto(Long idProjeto) {
        BigDecimal total = registroHorasRepository.sumHorasByProjetoId(idProjeto);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    // buscarRegistrosPorUsuarioEPeriodo mantido igual...
}