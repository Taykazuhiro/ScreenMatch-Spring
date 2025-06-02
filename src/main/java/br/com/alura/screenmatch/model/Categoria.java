package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action"),
    ROMANCE("Romance)"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime"),
    TERROR("Horror");

    private String categoriaOMBD;

    Categoria (String categoriaOMDB){
        this.categoriaOMBD = categoriaOMDB;
    }

    public static Categoria fromString(String text){
        for (Categoria categoria : Categoria.values()){
            if (categoria.categoriaOMBD.equalsIgnoreCase(text)){
                return categoria;
            }
        }
    throw new IllegalArgumentException("Nenhuma categoria encontrada para a sua busca");
    }
}
