package com.ifpe.edu.br.workflowmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ifpe.edu.br.workflowmanagement.service.UsuarioService;
import com.ifpe.edu.br.workflowmanagement.service.DTO.CadastroUsuarioDTO;
import com.ifpe.edu.br.workflowmanagement.service.DTO.LoginDTO;
import com.ifpe.edu.br.workflowmanagement.service.DTO.UsuarioResponseDTO;

@RestController
@RequestMapping("/api/usuarios") // Endpoint base para funcionalidades de usuário
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para o Caso de Uso 1: Cadastro de Usuário
     */
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrarUsuario(@RequestBody CadastroUsuarioDTO cadastroDTO) {
        UsuarioResponseDTO usuarioCriado = usuarioService.cadastrar(cadastroDTO);
        // Retorna HTTP 201 Created com os dados do usuário no corpo da resposta
        return ResponseEntity.status(201).body(usuarioCriado);
    }

    /**
     * Endpoint para o Caso de Uso 2: Login do Usuário
     * (A implementação completa requer Spring Security para gerar tokens JWT, etc.)
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        // A lógica de autenticação seria chamada aqui
        // Se sucesso, retorna um token de acesso
        return ResponseEntity.ok("Login bem-sucedido! (Token JWT seria gerado aqui)");
    }

    /**
     * Endpoint para o Caso de Uso 22: Recuperação de Senha
     */
    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@RequestParam String email) {
        // A lógica para iniciar a recuperação de senha seria chamada aqui
        return ResponseEntity.ok("Se um usuário com o e-mail informado existir, um link de recuperação será enviado.");
    }
}