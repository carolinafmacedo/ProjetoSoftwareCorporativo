package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.FluxoTrabalho;
import com.ifpe.edu.br.workflowmanagement.service.entities.Projeto;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.FluxoTrabalhoRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.ProjetoRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FluxoTrabalhoRepository fluxoTrabalhoRepository;

    @Autowired
    public ProjetoService(ProjetoRepository projetoRepository, 
                          UsuarioRepository usuarioRepository, 
                          FluxoTrabalhoRepository fluxoTrabalhoRepository) {
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fluxoTrabalhoRepository = fluxoTrabalhoRepository;
    }

    // --- Métodos de Escrita (Criação e Edição) ---

    @Transactional
    public Projeto criarProjeto(String nome, String descricao, Long idUsuarioGerente) {
        Usuario gerente = usuarioRepository.findById(idUsuarioGerente)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado."));

        // Permissão: Apenas Gerentes ou Admins
        if (!isGerenteOuAdmin(gerente)) {
            throw new SecurityException("Apenas Gerentes ou Admins podem criar projetos.");
        }

        Projeto novoProjeto = new Projeto(nome, descricao, gerente, null);
        return projetoRepository.save(novoProjeto);
    }

    @Transactional
    public Projeto associarFluxoTrabalho(Long idProjeto, Long idFluxo, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado."));
        FluxoTrabalho fluxo = fluxoTrabalhoRepository.findById(idFluxo)
                .orElseThrow(() -> new RuntimeException("Fluxo não encontrado."));

        // Permissão: Dono do projeto ou Admin
        if (!projeto.getGerente().getId().equals(executor.getId()) && !isAdmin(executor)) {
             throw new SecurityException("Sem permissão para associar fluxo.");
        }
        
        projeto.setFluxoTrabalho(fluxo);
        return projetoRepository.save(projeto);
    }

    // --- Métodos de Leitura (GET) que estavam faltando ---

    @Transactional(readOnly = true)
    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Projeto buscarPorId(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado com ID: " + id));
    }

    // --- Relatórios ---

    @Transactional(readOnly = true)
    public String gerarRelatorio(Long idProjeto, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
            .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Permissão: Quem pode ver o relatório? (Gerente, Admin ou membros da equipe)
        // Aqui simplifiquei para Gerente ou Admin
        if (!projeto.getGerente().getId().equals(executor.getId()) && !isAdmin(executor)) {
             throw new SecurityException("Usuário não tem permissão para gerar relatórios deste projeto.");
        }

        List<Tarefa> tarefas = projeto.getTarefas();
        long totalTarefas = tarefas.size();
        
        // Lógica simples: considera concluída se a etapa contiver "Concluído" ou "Done" no nome
        long tarefasConcluidas = tarefas.stream()
            .filter(t -> t.getEtapaAtual() != null && 
                   (t.getEtapaAtual().getNome().toLowerCase().contains("concluído") || 
                    t.getEtapaAtual().getNome().toLowerCase().contains("done")))
            .count();

        // Montagem do texto
        StringBuilder sb = new StringBuilder();
        sb.append("=== Relatório do Projeto: ").append(projeto.getNome()).append(" ===\n");
        sb.append("Gerente: ").append(projeto.getGerente().getNome()).append("\n");
        sb.append("Total de Tarefas: ").append(totalTarefas).append("\n");
        sb.append("Tarefas Concluídas: ").append(tarefasConcluidas).append("\n");
        
        if (totalTarefas > 0) {
            double progresso = (double) tarefasConcluidas / totalTarefas * 100;
            sb.append(String.format("Progresso: %.2f%%\n", progresso));
        } else {
            sb.append("Progresso: 0.00% (Sem tarefas)\n");
        }

        return sb.toString();
    }

    // --- Métodos Auxiliares de Permissão ---

    private boolean isAdmin(Usuario u) {
        return u.getPapel() != null && "ADMIN".equalsIgnoreCase(u.getPapel().getNome());
    }

    private boolean isGerenteOuAdmin(Usuario u) {
        if (u.getPapel() == null) return false;
        String papel = u.getPapel().getNome().toUpperCase();
        return "ADMIN".equals(papel) || "GERENTE".equals(papel) || papel.contains("GERENTE");
    }
}