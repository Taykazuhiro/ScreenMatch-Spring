package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=e91e2a6b";
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu(){
        System.out.println("Digite o nome da serie para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumoAPI.conexaoApi(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
        DadosSeries busca1 = conversor.obterDados(json, DadosSeries.class);
        System.out.println(busca1);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i<= busca1.totalTemporadas(); i++){
            json = consumoAPI.conexaoApi(ENDERECO + nomeSerie.replace(" ","+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(t -> t.episodio().forEach(e -> System.out.println(e.titulo())));
        List <DadosEpisodio> listaDeEpisodios = temporadas.stream()
                .flatMap(t -> t.episodio().stream())
                .collect(Collectors.toList());

        /*System.out.println("Top 5 episódios: ");
        listaDeEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Filtro: N/A ")) //Peek serve para debugar a stream
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed()) //ordena elementos da stream ou lista. Ordem natural: menor>maior
                .peek(e -> System.out.println("Ordenação "))
                .limit(5) //limita a quantidade de elementos que serão selecionados da stream ou lista
                .peek(e-> System.out.println("Limite: " + e))
                .map(e-> e.titulo().toUpperCase())
                .peek(e-> System.out.println("Mapeamento: "))
                .forEach(System.out::println);*/

        List<Episodio> episodios = temporadas.stream()
                //pega varias listas ou elementos e transforma em uma só
                // Nesse caso pega todos os episodios das listas de todas as temporadas e transforma em uma lista só
                .flatMap(t -> t.episodio().stream()
                        .map(d -> new Episodio(t.numero(), d)) // Transforma elementos da stream em outros elementos do mesmo tipo ou diferentes
                ).collect(Collectors.toList()); //Collect: permite coletar os elementos da stream em uma coleção ou em outro tipo de dado.

        episodios.forEach(System.out::println);

//        System.out.println("Digite um trecho do título do episódio: ");
//        var trechoTitulo = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream() // objeto contêiner que pode ou não conter um valor não nulo
//                .filter(e-> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase())) //transforma o título e o trecho buscado pelo usuário em maiusculo para comparar usando o contains.
//                .findFirst();//sempre retorna o mesmo resultado. Se quiser um resultado mais rapido, o find any pode ser util
//        if (episodioBuscado.isPresent()){//usa o metodo para verificar se a variável Optional tem algum objeto dentro ou não.
//            System.out.println("Episódio Encontrado!");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//            System.out.println("Episodio: " + episodioBuscado.get().getNumeroEpisodio());
//        } else {
//            System.out.println("Episódio não encontrado");
//        }


        /*System.out.println("A partir de qual ano você deseja ver os episódios?");
        var ano = leitura.nextInt();
        leitura.nextLine();

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate anoBusca = LocalDate.of(ano, 1, 1);
        episodios.stream()
                //Filter filtra algo a partir dos critérios apresentados na expressão lambda
                .filter(e-> e.getDataLancamento() != null && e.getDataLancamento().isAfter(anoBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episodio: " + e.getTitulo() +
                                " Data de lançamento: " + e.getDataLancamento().format(formatador)));*/

        // Nesse caso o tipo de variável Map vai armazenar um conjunto chave-valor dos tipos especificados dentro do <>
//        Map<Integer, Double> avaliacaoPorTemporada = episodios.stream()
//            .filter(e -> e.getAvaliacao() > 0.0)
//            .collect(Collectors.groupingBy(Episodio::getTemporada,Collectors.averagingDouble(Episodio::getAvaliacao)));
//        System.out.println(avaliacaoPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e-> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Nota do Melhor episódio: " + est.getMax() +
                " Temporada: " + episodios.stream().filter(e-> e.getAvaliacao() == est.getMax()).findFirst().get().getTemporada()+
                " Episódio: " + episodios.stream().filter(e-> e.getAvaliacao() == est.getMax()).findFirst().get().getNumeroEpisodio() +
                " Título: " + episodios.stream().filter(e-> e.getAvaliacao() == est.getMax()).findFirst().get().getTitulo());
        System.out.println("Nota do Pior episódio: " + est.getMin() +
                " Temporada: " + episodios.stream().filter(e-> e.getAvaliacao() == est.getMin()).findFirst().get().getTemporada()+
                " Episódio: " + episodios.stream().filter(e-> e.getAvaliacao() == est.getMin()).findFirst().get().getNumeroEpisodio() +
                " Título: " + episodios.stream().filter(e-> e.getAvaliacao() == est.getMin()).findFirst().get().getTitulo());
    }
}
