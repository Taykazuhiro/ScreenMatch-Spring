package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;

/* @JsonAlias e @JsonProperty são notações da dependencia Jackson para serialização e deserialização
JsonAlias é usado apenas apenas na desearilização (leitura). Posso colocar um array de nomes para busca.
JsonProperty é uma para serialização e desearilização usando a mesma chave que ele buscou.
* */

public record DadosSeries(@JsonAlias("Title") String titulo,
                          @JsonAlias("totalSeasons") Integer totalTemporadas,
                          @JsonAlias("imdbRating") String avaliacao) {
}
