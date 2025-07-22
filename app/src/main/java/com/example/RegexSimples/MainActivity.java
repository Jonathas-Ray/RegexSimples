package com.example.RegexSimples;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView texto;
    private ActivityResultLauncher<Intent> pickPdfSplitter;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ArrayList<String> exame = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        texto = findViewById(R.id.resultado);

        if (savedInstanceState != null) { // Mantém o texto quando virar a tela
            String textoSalvo = savedInstanceState.getString("resultado_texto");
            if (textoSalvo != null) {
                texto.setText(textoSalvo);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pickPdfSplitter = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri pdfUri = result.getData().getData();
                        String resultado = pdfInterpreter.pdfExtraction(this, pdfUri);
                        Map<Integer, String> resultados = pdfInterpreter.pdf_splitter(resultado);

                        exame.clear();
                        StringBuilder resultadoFinal = new StringBuilder();
                        for (Map.Entry<Integer, String> entry : resultados.entrySet()) {
                            exame.add(entry.getValue());
                            resultadoFinal.append("Posição ")
                                    .append(entry.getKey())
                                    .append(": ")
                                    .append(entry.getValue())
                                    .append("\n");
                        }

                        texto.setText(resultadoFinal.toString());
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        ocrInterpreter.ocrFromUri(this, imageUri, texto, exame);
                    }
                }
        );

        findViewById(R.id.Listar_PDF).setOnClickListener(v -> lerPDF());
        findViewById(R.id.ExtractButton).setOnClickListener(v -> callExtract());
        findViewById(R.id.OCR).setOnClickListener(v -> escolherImagem());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Efetivamente salva o texto quando eu o coleto
        super.onSaveInstanceState(outState);
        outState.putString("resultado_texto", texto.getText().toString());
    }

    public void lerPDF() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        pickPdfSplitter.launch(intent);
    }

    public void escolherImagem() {
        // Chama um intent que apenas pega a imagem da Galeria ao invés de abrir a câmera
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void copiarParaAreaDeTransferencia() {
        StringBuilder textoCopiado = new StringBuilder();
        for (String palavra : exame) {
            textoCopiado.append(palavra).append(" ");
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Exame OCR/PDF", textoCopiado.toString().trim());
        clipboard.setPrimaryClip(clip);
    }

    public void callExtract() {
        copiarParaAreaDeTransferencia();

        Extract extractor = new Extract();
        String[] palavras = exame.toArray(new String[0]);
        List<ResultadoExame> exames = extractor.extrairExames(palavras);
        StringBuilder resultadoTexto = new StringBuilder();
        for (ResultadoExame exame : exames) {
            resultadoTexto.append(exame.toString()).append("\n");
        }
        texto.setText(resultadoTexto.toString());
    }

}
