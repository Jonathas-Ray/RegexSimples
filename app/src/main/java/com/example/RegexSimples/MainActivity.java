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

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView texto;
    private ActivityResultLauncher<Intent> pickPdfSplitter;
    private ActivityResultLauncher<Intent> pickPdfString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        texto = findViewById(R.id.resultado);

        // Restaura o conteúdo do TextView se houver
        if (savedInstanceState != null) {
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

                        StringBuilder resultadoFinal = new StringBuilder();
                        for (Map.Entry<Integer, String> entry : resultados.entrySet()) {
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

        pickPdfString = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri pdfUri = result.getData().getData();
                        String resultado = pdfInterpreter.pdfExtraction(this, pdfUri);
                        texto.setText(resultado);
                    }
                }
        );

        findViewById(R.id.Listar_PDF).setOnClickListener(v -> lerPDF());
        findViewById(R.id.PDF_String).setOnClickListener(v -> lerPDFString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Salva o conteúdo atual do TextView
        outState.putString("resultado_texto", texto.getText().toString());
    }

    public void lerPDF() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        pickPdfSplitter.launch(intent);
    }

    public void lerPDFString() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        pickPdfString.launch(intent);
    }
}
