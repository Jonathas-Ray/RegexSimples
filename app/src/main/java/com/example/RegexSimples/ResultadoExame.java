package com.example.RegexSimples;

public class ResultadoExame {
    String nome;
    String valorAtual;
    String unidadeAtual;
    String valorRefMin;
    String valorRefMax;
    String unidadeReferencia;

    @Override
    public String toString() {
        return nome + ": " + valorAtual + " " + unidadeAtual +
                " | Ref: " + valorRefMin + " a " + valorRefMax + " " + unidadeReferencia;
    }
}
