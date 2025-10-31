package com.ifpe.edu.br.workflowmanagement.service.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ifpe.edu.br.workflowmanagement.service.entities.Papel;


@Repository
public interface PapelRepository extends JpaRepository<Papel, Long> {
    
    // O JpaRepository já fornece métodos como findById(), findAll(), save(), etc.
    // Se precisar de buscas customizadas para Papel, você pode adicioná-las aqui.
    // Ex: Optional<Papel> findByNome(String nome);
}