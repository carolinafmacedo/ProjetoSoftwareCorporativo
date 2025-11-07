package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.FluxoTrabalho;
import com.ifpe.edu.br.workflowmanagement.service.entities.Projeto;
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

    /**
     * CU 4: Cria um novo projeto.
     * Apenas usuários com a permissão "CRIAR_PROJETO" podem executar esta ação.
     */
    @Transactional
    public Projeto criarProjeto(String nome, String descricao, Long idUsuarioGerente) {
        Usuario gerente = usuarioRepository.findById(idUsuarioGerente)
                .orElseThrow(() -> new RuntimeException("Usuário gerente não encontrado."));

        // Verificação de Permissão
        if (!temPermissao(gerente, "CRIAR_PROJETO")) {
            throw new SecurityException("Usuário não tem permissão para criar projetos.");
        }

        Projeto novoProjeto = new Projeto();
        novoProjeto.setNome(nome);
        novoProjeto.setDescricao(descricao);
        novoProjeto.setGerente(gerente);

        return projetoRepository.save(novoProjeto);
    }

    /**
     * CU 5: Edita as informações de um projeto existente.
     * Apenas o gerente do projeto ou um administrador podem editar.
     */
    @Transactional
    public Projeto editarProjeto(Long idProjeto, String novoNome, String novaDescricao, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verificação de Permissão: ou é o gerente do projeto OU tem permissão de admin
        if (!projeto.getGerente().getId().equals(executor.getId()) && !temPermissao(executor, "EDITAR_QUALQUER_PROJETO")) {
            throw new SecurityException("Usuário não tem permissão para editar este projeto.");
        }

        projeto.setNome(novoNome);
        projeto.setDescricao(novaDescricao);
        
        return projetoRepository.save(projeto);
    }

    /**
     * CU 5: Exclui um projeto.
     * Apenas o gerente do projeto ou um administrador podem excluir.
     */
    @Transactional
    public void excluirProjeto(Long idProjeto, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verificação de Permissão: ou é o gerente do projeto OU tem permissão de admin
        if (!projeto.getGerente().getId().equals(executor.getId()) && !temPermissao(executor, "EXCLUIR_QUALQUER_PROJETO")) {
            throw new SecurityException("Usuário não tem permissão para excluir este projeto.");
        }

        projetoRepository.deleteById(idProjeto);
    }

    /**
     * CU 7: Associa um Fluxo de Trabalho a um Projeto.
     */
    @Transactional
    public Projeto associarFluxoTrabalho(Long idProjeto, Long idFluxo, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        FluxoTrabalho fluxo = fluxoTrabalhoRepository.findById(idFluxo)
                .orElseThrow(() -> new RuntimeException("Fluxo de Trabalho não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Verificação de Permissão: Apenas o gerente do projeto ou um admin pode associar.
        if (!projeto.getGerente().getId().equals(executor.getId()) && !temPermissao(executor, "ASSOCIAR_FLUXO_PROJETO")) {
             throw new SecurityException("Usuário não tem permissão para associar um fluxo a este projeto.");
        }
        
        projeto.setFluxoTrabalho(fluxo);
        return projetoRepository.save(projeto);
    }

    /**
     * CU 16: Gera um relatório simples sobre o projeto.
     * Em um sistema real, isso retornaria um objeto mais complexo (DTO) ou um arquivo.
     */
    @Transactional(readOnly = true) // Operação de leitura, não modifica o banco
    public String gerarRelatorio(Long idProjeto, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
            .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!projeto.getGerente().getId().equals(executor.getId()) && !temPermissao(executor, "GERAR_RELATORIOS")) {
             throw new SecurityException("Usuário não tem permissão para gerar relatórios deste projeto.");
        }

        long totalTarefas = projeto.getTarefas().size();
        // Exemplo: Contar tarefas concluídas (supondo que a última etapa do fluxo seja "Concluído")
        long tarefasConcluidas = projeto.getTarefas().stream()
            .filter(t -> t.getEtapaAtual() != null && t.getEtapaAtual().getNome().equalsIgnoreCase("Concluído"))
            .count();

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Relatório do Projeto: ").append(projeto.getNome()).append("\n");
        relatorio.append("============================================\n");
        relatorio.append("Gerente do Projeto: ").append(projeto.getGerente().getNome()).append("\n");
        relatorio.append("Total de Tarefas: ").append(totalTarefas).append("\n");
        relatorio.append("Tarefas Concluídas: ").append(tarefasConcluidas).append("\n");
        relatorio.append("Progresso: ").append(totalTarefas > 0 ? (100 * tarefasConcluidas / totalTarefas) : 0).append("%\n");

        return relatorio.toString();
    }
    
    /**
     * Busca um projeto pelo seu ID.
     */
    public Projeto buscarPorId(Long id) {
        return projetoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
    }

    /**
     * Lista todos os projetos existentes.
     */
    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    /**
     * Método auxiliar para verificar permissões de um usuário.
     */
    private boolean temPermissao(Usuario usuario, String permissao) {
        if (usuario.getPapel() == null || usuario.getPapel().getPermissoes() == null) {
            return false;
        }
        return usuario.getPapel().getPermissoes().contains(permissao);
    }
}