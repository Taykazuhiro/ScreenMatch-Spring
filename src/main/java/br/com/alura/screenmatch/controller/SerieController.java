package br.com.alura.screenmatch.controller;
import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.service.ServiceSerie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/series")
public class SerieController {
@Autowired
private ServiceSerie servico;

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return servico.obterTodasSeriesBanco();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5Series(){
        return servico.top5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return servico.obterTodosLançamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id){
        return servico.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterEpisodios(@PathVariable Long id){
        return servico.obterTodosEpisodios(id);
    }
    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDTO> obterEpisodiosPorNumero(@PathVariable Long id, @PathVariable Long numero){
        return servico.obterEpisodioPorTemporada(id, numero);
    }

}
