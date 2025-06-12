package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceSerie {
    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasSeriesBanco() {
        return converteDadosSerie(repositorio.findAll());
    }

    public List<SerieDTO> top5Series() {
        return converteDadosSerie(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterTodosLançamentos() {
        return converteDadosSerie(repositorio.lancamentosMaisRecentes());
    }

    private List<SerieDTO> converteDadosSerie(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getCategoria(), s.getImagem(), s.getSinopse(), s.getAtores()))
                .collect(Collectors.toList());
    }

    private List<EpisodioDTO> converteDadosEpisodio(List<Episodio> episodios) {
        return episodios.stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(),s.getAvaliacao(),s.getCategoria(),s.getImagem(), s.getSinopse(), s.getAtores());
        }
        return null;
    }

    public List<EpisodioDTO> obterTodosEpisodios(Long id) {
        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return converteDadosEpisodio(s.getEpisodios());
        } else{
            return null;
        }
    }

    public List<EpisodioDTO> obterEpisodioPorTemporada(Long id, Long numero) {
        return converteDadosEpisodio(repositorio.obterEpisodioPorNumero(id,numero));
    }
}

