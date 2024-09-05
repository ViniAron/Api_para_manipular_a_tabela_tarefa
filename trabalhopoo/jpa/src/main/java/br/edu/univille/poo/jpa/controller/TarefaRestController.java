package br.edu.univille.poo.jpa.controller;

import br.edu.univille.poo.jpa.entidade.Tarefa;
import br.edu.univille.poo.jpa.servico.TarefaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController()
@RequestMapping("api/tarefa")
public class TarefaRestController {

    @Autowired
    private TarefaServico tarefaServico;

    @PostMapping
    public ResponseEntity<?> incluir(@RequestBody Tarefa tarefa) {
        try {
            // Validação: título deve ter pelo menos 5 caracteres
            if (tarefa.getTitulo().length() < 5) {
                return new ResponseEntity<>("O título deve conter pelo menos 5 caracteres.", HttpStatus.BAD_REQUEST);
            }
            // Validação: data prevista de finalização é obrigatória
            if (tarefa.getDataPrevistaFinalizacao() == null) {
                return new ResponseEntity<>("A data prevista de finalização é obrigatória.", HttpStatus.BAD_REQUEST);
            }
            tarefa = tarefaServico.incluir(tarefa);
            return new ResponseEntity<>(tarefa, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<Tarefa> obterTodas() {
        return tarefaServico.obterTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> obterPeloId(@PathVariable Long id) {
        var opt = tarefaServico.obterPeloId(id);
        return opt.map(tarefa -> new ResponseEntity<>(tarefa, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    public ResponseEntity<?> atualizar(@RequestBody Tarefa tarefa) {
        try {
            if (tarefa.isFinalizado()) {
                return new ResponseEntity<>("Tarefas finalizadas não podem ser modificadas.", HttpStatus.BAD_REQUEST);
            }
            tarefa = tarefaServico.atualizar(tarefa);
            return new ResponseEntity<>(tarefa, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/nao-finalizadas")
    public List<Tarefa> obterNaoFinalizadas() {
        return tarefaServico.obterNaoFinalizadas();
    }

    @GetMapping("/finalizadas")
    public List<Tarefa> obterFinalizadas() {
        return tarefaServico.obterFinalizadas();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            var tarefa = tarefaServico.obterPeloId(id);
            if (tarefa.isPresent() && tarefa.get().isFinalizado()) {
                return new ResponseEntity<>("Tarefas finalizadas não podem ser excluídas.", HttpStatus.BAD_REQUEST);
            }
            tarefaServico.excluir(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/atrasadas")
    public List<Tarefa> obterAtrasadas() {
        return tarefaServico.obterAtrasadas(LocalDate.now());
    }

    @GetMapping("/nao-finalizadas-entre")
    public List<Tarefa> obterNaoFinalizadasEntre(@RequestParam("inicio") LocalDate inicio, @RequestParam("fim") LocalDate fim) {
        return tarefaServico.obterNaoFinalizadasEntre(inicio, fim);
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<?> finalizarTarefa(@PathVariable Long id) {
        try {
            tarefaServico.finalizarTarefa(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}