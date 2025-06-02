package br.com.alura.screenmatch.principal;


import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=e91e2a6b";
    private ConverteDados conversor = new ConverteDados();
    private List<DadosSeries> dadosSeries = new ArrayList<>();

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0){
    var menu = """
            1 - Buscar séries
            2 - Buscar episódios
            3 - Listar séries buscadas
            
            0 - Sair                                 
            """;

        System.out.println(menu);
        opcao =leitura.nextInt();
        leitura.nextLine();

        switch(opcao)

    {
        case 1:
            buscarSerieWeb();
            break;
        case 2:
            buscarEpisodioPorSerie();
            break;
        case 3:
            listarSeriesBuscadas();
            break;
        case 0:
            System.out.println("Saindo...");
            break;
        default:
            System.out.println("Opção inválida");
    }
}
    }

    private void listarSeriesBuscadas() {
        List<Serie> series = new ArrayList<>();
        series = dadosSeries.stream()
                        .map(d -> new Serie(d)).collect(Collectors.toList());
        series.stream()
                .sorted(Comparator.comparing(Serie::getCategoria))
                .forEach(System.out::println);

    }


    private void buscarSerieWeb() {
        DadosSeries dados = getDadosSerie();
        dadosSeries.add(dados);
    }

    private DadosSeries getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumoAPI.conexaoApi(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        DadosSeries dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumoAPI.conexaoApi(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }
}
