package be.jorgedc.androidmanifestfitter;

import com.intellij.openapi.editor.Document;

/**
 * Created by Jorge on 11/08/13.
 */
public class WriteAction implements Runnable {
    private String text;
    private Document document;

    WriteAction(String text, Document document) {
        this.text = text;
        this.document = document;
    }

    public void run() {
        document.setText(text);
    }
}
