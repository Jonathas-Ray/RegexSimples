package com.example.RegexSimples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ocrInterpreter {

    private static final int Y_TOLERANCIA = 10; // Tolerância para considerar palavras na mesma linha

    public static void ocrFromUri(Context context, Uri imageUri, TextView texto, ArrayList<String> exame) {
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            }

            InputImage image = InputImage.fromBitmap(bitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(result -> {
                        exame.clear();
                        StringBuilder resultadoFinal = new StringBuilder();

                        class PalavraPosicionada {
                            String texto;
                            float x, y;
                            PalavraPosicionada(String texto, float x, float y) {
                                this.texto = texto;
                                this.x = x;
                                this.y = y;
                            }
                        }

                        List<PalavraPosicionada> palavras = new ArrayList<>();

                        for (Text.TextBlock block : result.getTextBlocks()) {
                            for (Text.Line line : block.getLines()) {
                                for (Text.Element element : line.getElements()) {
                                    palavras.add(new PalavraPosicionada(
                                            element.getText(),
                                            element.getBoundingBox().left,
                                            element.getBoundingBox().top
                                    ));
                                }
                            }
                        }

                        // Agrupar palavras por linha (Y próximo)
                        HashMap<Integer, List<PalavraPosicionada>> linhasMap = new HashMap<>();

                        for (PalavraPosicionada palavra : palavras) {
                            boolean adicionada = false;
                            for (Integer yChave : linhasMap.keySet()) {
                                if (Math.abs(palavra.y - yChave) <= Y_TOLERANCIA) {
                                    linhasMap.get(yChave).add(palavra);
                                    adicionada = true;
                                    break;
                                }
                            }
                            if (!adicionada) {
                                List<PalavraPosicionada> novaLinha = new ArrayList<>();
                                novaLinha.add(palavra);
                                linhasMap.put((int) palavra.y, novaLinha);
                            }
                        }

                        // Ordenar linhas por Y (de cima pra baixo)
                        List<Integer> ys = new ArrayList<>(linhasMap.keySet());
                        Collections.sort(ys);

                        for (Integer y : ys) {
                            List<PalavraPosicionada> linha = linhasMap.get(y);
                            // Ordenar palavras da linha por X (esquerda pra direita)
                            linha.sort(Comparator.comparingDouble(p -> p.x));

                            for (PalavraPosicionada p : linha) {
                                exame.add(p.texto);
                                resultadoFinal.append("Posição ")
                                        .append(exame.size() - 1)
                                        .append(": ")
                                        .append(p.texto)
                                        .append("\n");
                            }
                        }

                        texto.setText(resultadoFinal.toString());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ocrInterpreter", "Erro no OCR", e);
                        texto.setText("Erro ao processar imagem: " + e.getMessage());
                    });

        } catch (Exception e) {
            Log.e("ocrInterpreter", "Falha ao abrir imagem para OCR", e);
            texto.setText("Falha ao abrir imagem: " + e.getMessage());
        }
    }
}