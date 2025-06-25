package com.example.RegexSimples;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Extract {
    private List<String> tiposDeExame = new ArrayList<>(List.of("Neutrófilo:", "Linfócito:", "Monócito:", "Eosinófilo:", "Basófilo:",
            "Hemácias:", "Hemoglobina:", "Hematócrito:", "VCM:", "HCM:", "CHCM:", "RDW:")); // Ajustável

    public List<ResultadoExame> extrairExames(String[] palavras) { //Posso "exibir" no TextView
        List<ResultadoExame> resultados = new ArrayList<>();

        String termosPattern = tiposDeExame.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        Pattern examePattern = Pattern.compile(termosPattern, Pattern.CASE_INSENSITIVE);
        Pattern resultadoPattern = Pattern.compile("(\\d+[.,]?\\d*)\\s*(%|fL|g/dL|pg|/µL|10\\^6/µL)?");
        Pattern referenciaPattern = Pattern.compile("(\\d+[.,]?\\d*)\\s*[a-–]\\s*(\\d+[.,]?\\d*)\\s*(%|fL|g/dL|pg|/µL|10\\^6/µL)");

        for (int i = 0; i < palavras.length; i++) { //De um em um encontra o nome do exame
            Matcher mExame = examePattern.matcher(palavras[i]);
            if (mExame.matches()) {
                ResultadoExame res = new ResultadoExame();
                res.nome = palavras[i];

                StringBuilder contexto = new StringBuilder();
                int j = i + 1; //Necessário para pularmos o índice mais tarde
                for (; j < palavras.length; j++) { //Itera mais um pouco até achar o proximo exame
                    Matcher proximoExame = examePattern.matcher(palavras[j]);
                    if (proximoExame.matches()) {
                        break; // chegou no próximo exame, para de coletar
                    }
                    contexto.append(palavras[j]).append(" "); //forma um retalho String para o fazer o Match
                }
                String contextoStr = contexto.toString();

                Matcher mRes = resultadoPattern.matcher(contextoStr); //Matcher para bloco dos resultados
                if (mRes.find()) {
                    res.valorAtual = mRes.group(1);
                    res.unidadeAtual = mRes.group(2) != null ? mRes.group(2) : "";
                }

                Matcher mRef = referenciaPattern.matcher(contextoStr); //Matcher para bloco das referências
                if (mRef.find()) {
                    res.valorRefMin = mRef.group(1);
                    res.valorRefMax = mRef.group(2);
                    res.unidadeReferencia = mRef.group(3);
                }

                resultados.add(res);
                i = j - 1; // pula direto pro índice anterior ao próximo exame
            }
        }
        return resultados;
    }
}
