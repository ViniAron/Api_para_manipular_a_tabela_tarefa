package br.edu.univille.poo.jpa.servico;

import br.edu.univille.poo.jpa.entidade.Tarefa;
import br.edu.univille.poo.jpa.repositorio.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TarefaServico {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> obterTodas() {
        return tarefaRepository.findAll();
    }

    public Optional<Tarefa> obterPeloId(Long id) {
        return tarefaRepository.findById(id);
    }

    public Tarefa incluir(Tarefa tarefa) {
        tarefa.setId(0); // Garante que é uma nova tarefa
        if (tarefa.getTitulo() == null || tarefa.getTitulo().length() < 5) {
            throw new RuntimeException("O título da tarefa deve conter pelo menos 5 caracteres.");
        }
        if (tarefa.getDataPrevistaFinalizacao() == null) {
            throw new RuntimeException("A data prevista de finalização é obrigatória.");
        }
        return tarefaRepository.save(tarefa);
    }

    public Tarefa atualizar(Tarefa tarefa) {
        var antiga = tarefaRepository.findById(tarefa.getId()).orElse(null);
        if (antiga == null) {
            throw new RuntimeException("Tarefa não encontrada.");
        }
        if (antiga.isFinalizado()) {
            throw new RuntimeException("Tarefas finalizadas não podem ser modificadas.");
        }

        antiga.setTitulo(tarefa.getTitulo());
        antiga.setDescricaoLonga(tarefa.getDescricaoLonga());
        antiga.setDataPrevistaFinalizacao(tarefa.getDataPrevistaFinalizacao());

        return tarefaRepository.save(antiga);
    }

    public void excluir(Long id) {
        var tarefa = tarefaRepository.findById(id).orElse(null);
        if (tarefa == null) {
            throw new RuntimeException("Tarefa não encontrada.");
        }
        if (tarefa.isFinalizado()) {
            throw new RuntimeException("Tarefas finalizadas não podem ser excluídas.");
        }
        tarefaRepository.delete(tarefa);
    }

    public void finalizarTarefa(Long id) {
        var tarefa = tarefaRepository.findById(id).orElse(null);
        if (tarefa == null) {
            throw new RuntimeException("Tarefa não encontrada.");
        }
        if (tarefa.isFinalizado()) {
            throw new RuntimeException("Tarefa já está finalizada.");
        }

        tarefa.setFinalizado(true);
        tarefa.setDataFinalizacao(LocalDate.now());

        tarefaRepository.save(tarefa);
    }

    public List<Tarefa> obterNaoFinalizadas() {
        return tarefaRepository.findAllByFinalizadoFalse();
    }

    public List<Tarefa> obterFinalizadas() {
        return tarefaRepository.findAllByFinalizadoTrue();
    }

    public List<Tarefa> obterAtrasadas(LocalDate dataAtual) {
        return tarefaRepository.findAllByFinalizadoFalseAndDataPrevistaFinalizacaoBefore(dataAtual);
    }

    public List<Tarefa> obterNaoFinalizadasEntre(LocalDate inicio, LocalDate fim) {
        return tarefaRepository.findAllByFinalizadoFalseAndDataPrevistaFinalizacaoBetween(inicio, fim);
    }
}