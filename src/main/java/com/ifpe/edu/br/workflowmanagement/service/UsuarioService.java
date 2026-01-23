package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.Papel;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.PapelRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository; // Necessário para buscar o papel
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, 
                          PapelRepository papelRepository, 
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(String nome, String email, String senha, String cargo, Long papelId) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }

        Papel papel = papelRepository.findById(papelId)
                .orElseThrow(() -> new RuntimeException("Papel (Role) não encontrado."));

        String senhaCriptografada = passwordEncoder.encode(senha);

        Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada, cargo, papel);
        return usuarioRepository.save(novoUsuario);
    }

    public Optional<Usuario> login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    // Demais métodos mantidos...
    public Optional<Usuario> buscarPorId(Long id) { return usuarioRepository.findById(id); }
    public List<Usuario> listarTodos() { return usuarioRepository.findAll(); }
    
    @Transactional
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) throw new RuntimeException("Usuário não encontrado.");
        usuarioRepository.deleteById(id);
    }
}