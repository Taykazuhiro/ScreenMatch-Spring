package br.com.alura.screenmatch.controller;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.service.ServiceSerie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
