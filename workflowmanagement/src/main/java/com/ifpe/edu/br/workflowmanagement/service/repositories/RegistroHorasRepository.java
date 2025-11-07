package com.ifpe.edu.br.workflowmanagement.service.repositories;

import com.ifpe.edu.br.workflowmanagement.service.entities.RegistroHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroHorasRepository extends JpaRepository<RegistroHoras, Long> {

    /**
     * Busca todos os registros de um usuário dentro de um intervalo de datas.
     * @param usuarioId O ID do usuário.
     * @param dataInicio A data inicial do período.
     * @param dataFim A data final do período.
     * @return Uma lista de registros de horas.
     */
    List<RegistroHoras> findByUsuarioIdAndDataRegistroBetween(Long usuarioId, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca todos os registros associados a uma tarefa específica.
     */
    List<RegistroHoras> findByTarefaId(Long tarefaId);

    /**
     * Utiliza uma consulta JPQL para buscar todos os registros de horas de um projeto inteiro,
     * navegando através do relacionamento Tarefa -> Projeto.
     */
    @Query("SELECT r FROM RegistroHoras r WHERE r.tarefa.projeto.id = :projetoId")
    List<RegistroHoras> findByProjetoId(Long projetoId);

    /**
     * Soma todas as horas registradas para uma tarefa específica.
     * Retorna Float para poder ser nulo se não houver registros.
     */
    @Query("SELECT SUM(r.horas) FROM RegistroHoras r WHERE r.tarefa.id = :tarefaId")
    Float sumHorasByTarefaId(Long tarefaId);

    /**
     * Soma todas as horas registradas para um projeto inteiro.
     */
    @Query("SELECT SUM(r.horas) FROM RegistroHoras r WHERE r.tarefa.projeto.id = :projetoId")
    Float sumHorasByProjetoId(Long projetoId);
}