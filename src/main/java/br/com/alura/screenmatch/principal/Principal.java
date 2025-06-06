package br.com.alura.screenmatch.principal;


import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
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
    private SerieRepository repositorio;
    private List<Serie> listaSeries = new ArrayList<>();
    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0){
            var menu = """
            1 - Buscar séries
            2 - Buscar episódios
            3 - Listar séries buscadas
            4 - Buscar por trecho do nome da série
            
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
                case 4:
                    buscarPorTrecho();
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
        if (serie.isPresent()){
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
                    .flatMap(t-> t.episodio().stream()
                            // depois mapear para da episódio da stream tempora, criar um novo objeto da classe Episódio
                            // em que t.numero() é o numero da temporada e o "e" é o número do episódio que vai chamar o record DadosEpisódio
                            .map(e-> new Episodio(t.numero(),e)))
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
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()){
            System.out.println("Série Encontrada: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }

}
