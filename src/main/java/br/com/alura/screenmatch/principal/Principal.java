package br.com.alura.screenmatch.principal;


import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=e91e2a6b";
    private ConverteDados conversor = new ConverteDados();
    private SerieRepository repositorio;
    private List<Serie> listaSeries = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    Optional<Serie> serieBuscada;

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar por trecho do nome da série
                    5 - Buscar série por ator
                    6 - Top 5 Séries
                    7 - Buscar Série por Categoria
                    8 - Buscar por quantidade de temporadas
                    9 - Busca por trecho do nome do episódio
                    10 - Top 5 Episódios por Serie
                    11 - Buscar episódios por Data
                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarPorTrecho();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarTemporadaAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosPorData();
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
        // adiciona as séries buscadas em uma lista de séries
        listaSeries = repositorio.findAll();
        listaSeries.stream()
                .sorted(Comparator.comparing(Serie::getCategoria))
                .forEach(System.out::println);

    }


    private void buscarSerieWeb() {
        DadosSeries dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
    }

    private DadosSeries getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumoAPI.conexaoApi(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome:");
        var nomeSerie = leitura.nextLine();

        //vai fazer uma stream com todas as séries buscadas que estão nessa lista
//        Optional<Serie>serie = listaSeries.stream()
//                // antes de fazer a stream vai verificar se a série buscada pelo usuário consta na lista. Se sim, vai retornar a primeira
//                .filter(s-> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
//                .findFirst();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        // se tiver um retorno vai pegar a série encontrada na stream e fazer um loop para buscar na API e adicionar todos os episódios em uma lista de temporadas.
        if (serie.isPresent()) {
            //Usado para atribuir o serie.get() para uma variável do tipo Série e não precisar usar a variavel do tipo Optional como referência
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoAPI.conexaoApi(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodiosParaBanco = temporadas.stream()
                    // stream da temporada para juntar todos os episódios em um stream só
                    .flatMap(t -> t.episodio().stream()
                            // depois mapear para da episódio da stream tempora, criar um novo objeto da classe Episódio
                            // em que t.numero() é o numero da temporada e o "e" é o número do episódio que vai chamar o record DadosEpisódio
                            .map(e -> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList());

            // setando a lista de episódios dessa série que foi encontrada com os episódios depois do stream.
            serieEncontrada.setEpisodios(episodiosParaBanco);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void buscarPorTrecho() {
        System.out.println("Digite um trecho do título da série:");
        var nomeSerie = leitura.nextLine();
        serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Série Encontrada: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator para Busca");
        var nomeDoAtor = leitura.nextLine();
        System.out.println("Séries acima de qual avaliação de 0 a 10?");
        var avaliacaoBuscada = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeDoAtor, avaliacaoBuscada);
        System.out.println("Series em que " + nomeDoAtor + " trabalhou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " (avaliação: " + s.getAvaliacao() + ")"));
    }

    private void buscarTop5Series() {
        List<Serie> seriesEncontradas = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " (avaliação: " + s.getAvaliacao() + ")"));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Qual o gênero de série deseja buscar? ");
        var serieBuscada = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(serieBuscada);

        List<Serie> seriesEncontradas = repositorio.findByCategoria(categoria);
        seriesEncontradas.forEach(System.out::println);
    }

    private void buscarTemporadaAvaliacao() {
        System.out.println("Deseja buscar séries com até quantas temporadas?");
        var serieTemporada = leitura.nextInt();
        System.out.println("As séries devem ter avaliação superior à qual nota de 0 a 10?");
        var serieAvaliacao = leitura.nextDouble();
        List<Serie> seriesBuscadas = repositorio.seriesPorTemporadaEAvaliacao(serieTemporada, serieAvaliacao);
        System.out.println("Aqui estão as Top 3 séries com até " + serieTemporada + " temporadas e avaliação a partir de: " + serieAvaliacao + " : ");
        seriesBuscadas.forEach(s -> System.out.println("Série: " + s.getTitulo() + " Temporadas: " + s.getTotalTemporadas() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite um trecho do título do episódio:");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodioPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio: %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarPorTrecho();
        if (serieBuscada.isPresent()) {
            Serie serieBuscadaExiste = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5EpisodiosSerieBuscada(serieBuscadaExiste);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio: %s - %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));
        }
    }

    private void buscarEpisodiosPorData() {
        buscarPorTrecho();
        if (serieBuscada.isPresent()) {
            Serie serieBuscadaExiste = serieBuscada.get();
            System.out.println("Digite o ano limite de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serieBuscadaExiste, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }
}
