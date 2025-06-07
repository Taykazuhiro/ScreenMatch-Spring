package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Ação"),
    ROMANCE("Romance)", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Criminal"),
    TERROR("Horror", "Terror");

    private String categoriaOMBD;
    private String categoriaPortugues;

    Categoria (String categoriaOMDB, String categoriaPortugues){
        this.categoriaOMBD = categoriaOMDB;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromString(String text){
        for (Categoria categoria : Categoria.values()){
            if (categoria.categoriaOMBD.equalsIgnoreCase(text)){
                return categoria;
            }
        }
    throw new IllegalArgumentException("Nenhuma categoria encontrada para a sua busca");
    }

    public static Categoria fromPortugues(String text){
        for (Categoria categoria : Categoria.values()){
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a sua busca");
    }
}
