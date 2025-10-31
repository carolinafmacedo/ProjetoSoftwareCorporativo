package com.ifpe.edu.br.workflowmanagement.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para buscar um usuário pelo email (usado no login e no cadastro)
    Optional<Usuario> findByEmail(String email);
    
    // O JpaRepository já fornece métodos como: save(), findById(), findAll(), deleteById(), etc.
}