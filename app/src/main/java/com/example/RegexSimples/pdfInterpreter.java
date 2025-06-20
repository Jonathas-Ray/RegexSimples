package com.example.RegexSimples;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import java.util.LinkedHashMap;
import java.util.Map;

import java.io.InputStream;

public class pdfInterpreter {
    public static String pdfExtraction(Context context, Uri pdfUri){ //Activity Atual e Local do PDF em URI
        StringBuilder result = new StringBuilder();

        try {
            PDFBoxResourceLoader.init(context);

            InputStream inputStream = context.getContentResolver().openInputStream(pdfUri);
            PDDocument document = PDDocument.load(inputStream);

            PDFTextStripper stripper = new PDFTextStripper();
            String extracted_pdf = stripper.getText(document);
            // AQUI EU IMPLEMENTO O OCR
            document.close();
            return extracted_pdf;

        } catch (Exception e) {
            Log.e("pdfInterpreter", "Falha ao ler o PDF", e);
            return "Falha ao ler o PDF, erro: " + e.getMessage();
        }
    }

    public static Map<Integer, String> pdf_splitter(String extracted_pdf) {
        Map<Integer, String> resultado = new LinkedHashMap<>();
        String[] tokens = extracted_pdf.split("\\s+");

        for (int i = 0; i < tokens.length; i++) {
            resultado.put(i, tokens[i]);
        }

        return resultado;
    }
}
