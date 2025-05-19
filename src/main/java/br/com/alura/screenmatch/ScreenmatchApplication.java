package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.com.alura.screenmatch.service.ConsumoAPI;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoAPI = new ConsumoAPI();
		var json = consumoAPI.conexaoApi("https://www.omdbapi.com/?t=gilmore+girls&apikey=e91e2a6b");
		System.out.println(json);

		ConverteDados conversor = new ConverteDados();
		DadosSeries dadosGilmoreGirls = conversor.obterDados(json, DadosSeries.class);
		System.out.println(dadosGilmoreGirls);

		json = consumoAPI.conexaoApi("https://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=e91e2a6b");
		DadosEpisodio episodiosGilmoreGirls = conversor.obterDados(json, DadosEpisodio.class);
		System.out.println(episodiosGilmoreGirls);

	}
}
