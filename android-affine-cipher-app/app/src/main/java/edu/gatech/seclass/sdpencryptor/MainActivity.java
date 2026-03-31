package edu.gatech.seclass.sdpencryptor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText sourceText, slopeInput, offsetInput;
    private TextView transformedText;
    private Button transformButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView appTitle = findViewById(R.id.appTitleID);
        appTitle.setText("SDP Encryptor Spring 2025");

        sourceText = findViewById(R.id.sourceTextID);
        slopeInput = findViewById(R.id.slopeInputID);
        offsetInput = findViewById(R.id.offsetInputID);
        transformedText = findViewById(R.id.transformedTextID);
        transformButton = findViewById(R.id.transformButtonID);

        slopeInput.setText("1");
        offsetInput.setText("1");

        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTransform();
            }
        });
    }

    public void handleClick(View view) {
        handleTransform();
    }

    private void handleTransform() {
        String text = sourceText.getText().toString();
        String slopeStr = slopeInput.getText().toString();
        String offsetStr = offsetInput.getText().toString();

        boolean valid = true;

        if (text.isEmpty() || !text.matches(".*[a-zA-Z0-9].*")) {
            sourceText.setError("Invalid Source Text");
            valid = false;
        }

        int slope = 0, offset = 0;

        try {
            slope = Integer.parseInt(slopeStr);
            if (slope < 1 || slope > 61 || slope % 2 == 0 || slope % 31 == 0) {
                slopeInput.setError("Invalid Slope Input");
                valid = false;
            }
        } catch (NumberFormatException e) {
            slopeInput.setError("Invalid Slope Input");
            valid = false;
        }

        try {
            offset = Integer.parseInt(offsetStr);
            if (offset < 1 || offset >= 62) {
                offsetInput.setError("Invalid Offset Input");
                valid = false;
            }
        } catch (NumberFormatException e) {
            offsetInput.setError("Invalid Offset Input");
            valid = false;
        }

        if (valid) {
            transformedText.setText(encrypt(text, slope, offset));
        } else {
            transformedText.setText("");
        }
    }

    private String encrypt(String oldString, int arg1, int arg2) {
        String charSet = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
        StringBuilder newString = new StringBuilder();

        for (int i = 0; i < oldString.length(); ++i) {
            char c = oldString.charAt(i);
            int pos = charSet.indexOf(c);
            if (pos != -1) { 
                int converter = (pos * arg1 + arg2) % 62;
                newString.append(charSet.charAt(converter));
            } else { 
                newString.append(c);
            }
        }
        return newString.toString();
    }
}
