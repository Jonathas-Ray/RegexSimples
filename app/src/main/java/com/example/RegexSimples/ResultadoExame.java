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
        StringBuilder sb = new StringBuilder();
        sb.append(nome).append(": ").append(valorAtual).append(" ").append(unidadeAtual);

        if (valorRefMin != null && valorRefMax != null && unidadeReferencia != null) {
            sb.append(" (Ref: ").append(valorRefMin).append(" - ").append(valorRefMax).append(" ").append(unidadeReferencia).append(")");
        }
        return sb.toString();
    }
}
