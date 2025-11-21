package com.ifpe.edu.br.workflowmanagement.controller;

import com.ifpe.edu.br.workflowmanagement.service.DTO.CriarTarefaDTO;
import com.ifpe.edu.br.workflowmanagement.service.entities.Comentario;
import com.ifpe.edu.br.workflowmanagement.service.entities.RegistroHoras;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import com.ifpe.edu.br.workflowmanagement.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    @Autowired
    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    // 1. Criar Tarefa
    @PostMapping
    public ResponseEntity<?> criarTarefa(@RequestBody @Valid CriarTarefaDTO dto) {
        try {
            Tarefa tarefa = tarefaService.criarTarefa(
                    dto.getTitulo(),
                    dto.getDescricao(),
                    dto.getProjetoId(),
                    dto.getResponsavelId(),
                    dto.getCriadorId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Mover Tarefa (Kanban Drag & Drop)
    @PatchMapping("/{idTarefa}/mover/{idNovaEtapa}")
    public ResponseEntity<?> moverTarefa(
            @PathVariable Long idTarefa,
            @PathVariable Long idNovaEtapa,
            @RequestParam Long idExecutor) {
        try {
            Tarefa tarefa = tarefaService.moverTarefaParaEtapa(idTarefa, idNovaEtapa, idExecutor);
            return ResponseEntity.ok(tarefa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Registrar Horas
    @PostMapping("/{idTarefa}/horas")
    public ResponseEntity<?> registrarHoras(
            @PathVariable Long idTarefa,
            @RequestBody Map<String, Object> payload) {
        try {
            // Converte os dados do JSON
            BigDecimal horas = new BigDecimal(payload.get("horas").toString());
            LocalDate data = LocalDate.parse((String) payload.get("data")); // Formato YYYY-MM-DD
            Long idUsuario = Long.valueOf(payload.get("usuarioId").toString());

            RegistroHoras registro = tarefaService.registrarHoras(idTarefa, horas, data, idUsuario);
            return ResponseEntity.ok(registro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 4. Adicionar Comentário
    @PostMapping("/{idTarefa}/comentarios")
    public ResponseEntity<?> comentar(
            @PathVariable Long idTarefa,
            @RequestBody Map<String, Object> payload) {
        try {
            String texto = (String) payload.get("texto");
            Long autorId = Long.valueOf(payload.get("autorId").toString());
            
            Comentario comentario = tarefaService.adicionarComentario(idTarefa, texto, autorId);
            return ResponseEntity.ok(comentario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. Excluir Tarefa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirTarefa(
            @PathVariable Long id,
            @RequestParam Long idExecutor) {
        try {
            tarefaService.excluirTarefa(id, idExecutor);
            return ResponseEntity.noContent().build(); // Retorna 204 (Sucesso sem conteúdo)
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

