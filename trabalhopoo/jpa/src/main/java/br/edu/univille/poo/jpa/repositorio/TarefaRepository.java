package br.edu.univille.poo.jpa.repositorio;

import br.edu.univille.poo.jpa.entidade.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findAllByFinalizadoFalse();

    List<Tarefa> findAllByFinalizadoTrue();

    List<Tarefa> findAllByFinalizadoFalseAndDataPrevistaFinalizacaoBefore(LocalDate dataAtual);

    List<Tarefa> findAllByFinalizadoFalseAndDataPrevistaFinalizacaoBetween(LocalDate inicio, LocalDate fim);
}