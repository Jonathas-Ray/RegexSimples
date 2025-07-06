package com.example.RegexSimples;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Extract {
    private List<String> examTypes = new ArrayList<>(List.of(
            "Neutrófilo", "Linfócito", "Monócito", "Eosinófilo", "Basófilo",
            "Hemácias", "Hemoglobina", "Hematócrito", "VCM", "HCM", "CHCM", "RDW",
            "Plaquetas", "MPV", "Leucograma"
    ));

    public List<ResultadoExame> extrairExames(String[] words) {
        List<ResultadoExame> results = new ArrayList<>();

        String patternTerms = examTypes.stream()
                // O Stream é chamado para estabelecer uma sequência de atos (aqui 2.map e 1.collect)
                .map(Pattern::quote)
                // Acima especifica que haverão termos que evem ser considerados de maneira literal e não seus respectivos significados para o sistema Regex
                .map(s -> "(?<![\\w-])" + s + "(?=(?:\\s*[:\\-–—\\.]+\\s*)?(?![a-zA-Z]))")
                // Acima, "vê ao redor da palavra" para garantir que não haja nada antes da palavra ou depois evitando falsos positivos
                .collect(Collectors.joining("|"));
                // Reune tudo via |

        Pattern examPattern = Pattern.compile(patternTerms, Pattern.CASE_INSENSITIVE);
        // Compila os nomes dos exames adicionando "case insenstive" para evitar falsos negativos por causa de maiúsculos/minúsculos
        Pattern resultPattern = Pattern.compile("(\\d+[.,]?\\d*)\\s*(%|fL|g/dL|pg|/µL|10\\^6/µL)?");
        // Na linha acima o porquê do quote ser necessário
        Pattern referencePattern = Pattern.compile("(\\d+[.,]?\\d*)\\s*(?:a|à|até|–|-)\\s*(\\d+[.,]?\\d*)\\s*(%|fL|g/dL|pg|/µL|10\\^6/µL)");

        for (int i = 0; i < words.length; i++) {
            Matcher examMatcher = examPattern.matcher(words[i].replace(":", ""));
            if (examMatcher.find()) {
                ResultadoExame result = new ResultadoExame();
                result.nome = words[i].replace(":", "");

                StringBuilder context = new StringBuilder();
                int j = i + 1;
                for (; j < words.length; j++) {
                    Matcher nextExam = examPattern.matcher(words[j].replace(":", ""));
                    if (nextExam.find()) {
                        break;
                    }
                    context.append(words[j]).append(" ");
                } // Até esse ponto ele só encontra os dois próximos exames para especificar o trecho onde procurar os resultados

                String contextStr = context.toString();

                Matcher resultMatcher = resultPattern.matcher(contextStr);
                if (resultMatcher.find()) {
                    result.valorAtual = resultMatcher.group(1);
                    result.unidadeAtual = resultMatcher.group(2) != null ? resultMatcher.group(2) : "";
                }

                Matcher referenceMatcher = referencePattern.matcher(contextStr);
                if (referenceMatcher.find()) {
                    result.valorRefMin = referenceMatcher.group(1);
                    result.valorRefMax = referenceMatcher.group(2);
                    result.unidadeReferencia = referenceMatcher.group(3);
                } // OS DOIS "IF" ACIMA NÃO SÃO CAPAZES DE LIDAR COM EXAMES COM MAIS DE UM RESULTADO
                 //  ALÉM DE QUEBRAR CASO NÃO SE IDENTIFIQUE UM EXAME E ELE ACABE DENTRO DO CONTEXTSTR
                //   PREFERÍVEL CONTAR UNIDADES DE MEDIDA DISTINTAS COMO REDUNDÂNCIA

                results.add(result);
                i = j - 1;
            }
        }

        return results;
    }
}
