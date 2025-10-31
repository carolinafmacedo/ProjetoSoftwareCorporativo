package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.*;
import com.ifpe.edu.br.workflowmanagement.service.DTO.CadastroUsuarioDTO;
import com.ifpe.edu.br.workflowmanagement.service.DTO.UsuarioResponseDTO;
import com.ifpe.edu.br.workflowmanagement.service.entities.Papel;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.PapelRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Para criptografar a senha
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PapelRepository papelRepository; // Supondo que exista um repositório para Papel

    @Autowired
    private PasswordEncoder passwordEncoder; // Bean de segurança para senhas

    /**
     * Lógica para cadastrar um novo usuário.
     */
    public UsuarioResponseDTO cadastrar(CadastroUsuarioDTO dto) {
        // 1. Valida se o e-mail já existe
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado no sistema.");
        }

        // 2. Busca o Papel pelo ID
        Papel papel = papelRepository.findById(dto.getPapelId())
            .orElseThrow(() -> new RuntimeException("Papel não encontrado."));

        // 3. Cria a nova entidade Usuario
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        // 4. Criptografa a senha antes de salvar
        novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoUsuario.setPapel(papel);

        // 5. Salva o usuário no banco de dados
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        
        // 6. Retorna um DTO com os dados públicos do usuário
        return new UsuarioResponseDTO(
            usuarioSalvo.getId(),
            usuarioSalvo.getNome(),
            usuarioSalvo.getEmail(),
            usuarioSalvo.getPapel().getNome()
        );
    }

    // Outros métodos como login, recuperarSenha, etc. seriam implementados aqui.
}