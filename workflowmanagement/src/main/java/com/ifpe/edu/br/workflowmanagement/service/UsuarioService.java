package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.Papel;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
// Importe uma implementação de PasswordEncoder. BCrypt é o mais comum.
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Injetado para criptografar senhas

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * CU 1: Cadastrar um novo usuário no sistema.
     * Valida se o e-mail já existe e criptografa a senha antes de salvar.
     */
    @Transactional
    public Usuario cadastrar(String nome, String email, String senha, String cargo, Papel papel) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }

        // Criptografa a senha antes de salvar no banco
        String senhaCriptografada = passwordEncoder.encode(senha);

        Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada, cargo, papel);
        return usuarioRepository.save(novoUsuario);
    }

    /**
     * CU 2: Autenticar um usuário (Login).
     * Busca o usuário pelo e-mail e compara a senha fornecida com a senha criptografada no banco.
     */
    public Optional<Usuario> login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Compara a senha em texto plano com a senha criptografada
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                return Optional.of(usuario); // Senha correta
            }
        }
        
        return Optional.empty(); // Usuário não encontrado ou senha incorreta
    }

    /**
     * CU 22: Inicia o processo de recuperação de senha.
     * Em uma aplicação real, este método geraria um token, o salvaria no banco de dados
     * associado ao usuário e enviaria um e-mail com um link para redefinição.
     */
    public void solicitarRecuperacaoSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Lógica para gerar token e enviar e-mail (a ser implementada)
        System.out.println("Enviando link de recuperação de senha para: " + usuario.getEmail());
    }

    /**
     * CU 17: Buscar um usuário pelo ID.
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    /**
     * CU 17: Listar todos os usuários.
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * CU 17: Excluir um usuário.
     */
    @Transactional
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado para exclusão.");
        }
        usuarioRepository.deleteById(id);
    }
}