package com.example.RegexSimples;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

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

    public static String [] pdf_splitter(String extracted_pdf){
        String[] tokens = extracted_pdf.split("\\s+");  // Quebra por espaços e quebras de linha
        return tokens;
    }
}
