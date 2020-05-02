package edu.dsu.cis340.places;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final String invalidCharacters = "!@#$%^&*()[]\\{}|-=_+:;'?><,./\"`~";
    final String invalidInputErrorStr = "Error: Input contains invalid characters.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // error TextView
        final TextView errorView = (TextView) findViewById(R.id.errorView);
        // clear it
        errorView.setText("");
        // userInput TextView
        final EditText userInput = (EditText) findViewById(R.id.userTextInput);
        // submitButton
        final Button submitButton = (Button)findViewById(R.id.submitButton);

        // Event listener for update of text -- checks if invalid
        userInput.addTextChangedListener(inputTextWatcher(errorView));
        // Even listener for button submit
        submitButton.setOnClickListener(onSubmit(userInput, errorView));

    }
    View.OnClickListener onSubmit(final EditText userInput, final TextView errorView){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = userInput.getText().toString();
                errorView.setText("");
                if(!validateString(location)){
                    errorView.setText(invalidInputErrorStr);
                    return;
                }
            }
        };
    }

    TextWatcher inputTextWatcher(final TextView errorView){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorView.setText(validateString(s.toString()) ? "" : invalidInputErrorStr);
                return;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    boolean validateString(String str){
        String invalidCharacters = "!@#$%^&*()[]\\{}|-=_+:;'?><,./\"`~";
        for(char character : str.toCharArray()){
            for(char invalid : invalidCharacters.toCharArray()){
                if(invalid == character){
                    return false;
                }
            }
        }
        return true;
    }
}
