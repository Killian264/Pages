package edu.dsu.cis340.places;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.lang.reflect.Array;

import edu.dsu.cis340.places.Models.Candidate;
import edu.dsu.cis340.places.Models.Location;
import edu.dsu.cis340.places.Models.Photo;

public class MainActivity extends AppCompatActivity {

    final String invalidCharacters = "!@#$%^&*()[]\\{}|-=_+:;'?><,/\"`~";
    final String invalidInputErrorStr = "Error: Input contains invalid characters.";
    final String noLocationFoundErrorStr = "Error: Location was not found.";
    final String noPhotoFoundErrorStr = "Error: Photos not found.";

    // set a app startup
    int maxHeightScreen;
    int maxWidthScreen;

    ArrayAdapter<String> autoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Grab height and width of android screen
        // preferably later this would be stored in some kind of config and only changed at app startup
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // .75 is me guessing the size of the top area
        // a more robust solution would be preferable
        maxHeightScreen = (int)(displayMetrics.heightPixels * .73);
        maxWidthScreen = displayMetrics.widthPixels;

        // error TextView
        final TextView errorView = (TextView) findViewById(R.id.errorView);
        // clear it
        errorView.setText("");
        // location textView
        final TextView locationView = (TextView) findViewById(R.id.locationView);
        // clear it
        locationView.setText("");
        // userInput
        final EditText userInput = (EditText) findViewById(R.id.userTextInput);

        // submitButton
        final Button submitButton = (Button)findViewById(R.id.submitButton);
        // imageView
        final ImageView imageView = (ImageView) findViewById(R.id.imageView) ;

        // Event listener for update of text -- checks if invalid
        userInput.addTextChangedListener(inputTextWatcher(errorView));
        // Even listener for button submit
        submitButton.setOnClickListener(onSubmit(userInput, errorView, imageView, locationView));

    }
    // On submit button click do
    View.OnClickListener onSubmit(final EditText userInput, final TextView errorView, final ImageView imageView, final TextView locationView){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = userInput.getText().toString();
                errorView.setText("");
                locationView.setText("");
                if(!validateString(location)){
                    errorView.setText(invalidInputErrorStr);
                    return;
                }
                searchLocation(location, errorView, imageView, locationView);

            }
        };
    }
    // Using google's API search a location by string and then output the image result to the screen

    // Setup JsonObjectRequest and then call Google's API
    void searchLocation(final String location, final TextView errorView, final ImageView imageView, final TextView locationView){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = URLBuilders.LocationURLBuilder(location);
        // Build request
        JsonObjectRequest LocationCandidatesRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do on response
                        searchLocationOnResponse(response, errorView, imageView, locationView);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorView.setText(error.toString());
                    }
                }
        );
        requestQueue.add(LocationCandidatesRequest);
        return;
    }
    // Handle API response
    void searchLocationOnResponse(JSONObject response, final TextView errorView, final ImageView imageView, final TextView locationView){
        // Read in data do checks to see if exists
        // Alternatively this could be done with Googles included status codes but that would be less safe
        Gson gson = new Gson();
        Location locationInfo  = gson.fromJson(response.toString(), Location.class);

        Candidate LocationCandidate;
        Photo photoInfo;
        try{
            LocationCandidate = locationInfo.getCandidates().get(0);
        }
        catch(Exception ex){
            errorView.setText(noLocationFoundErrorStr);
            return;
        }

        try{
            photoInfo = LocationCandidate.getPhotos().get(0);
            String photoReference = photoInfo.getPhotoReference();
        }
        catch(Exception ex){
            errorView.setText(noPhotoFoundErrorStr);
            return;
        }
        locationView.setText(LocationCandidate.getName());
        setPhoto(photoInfo.getPhotoReference(), photoInfo.getHeight(), photoInfo.getWidth(), errorView, imageView);
    }
    // Call Photos API and display result
    void setPhoto(String photoReference, int maxHeightPhoto, int maxWidthPhoto, final TextView errorView, final ImageView imageView){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = URLBuilders.photoURLBuilder(photoReference, maxHeightPhoto, maxWidthPhoto, maxHeightScreen, maxWidthScreen);
        // build request
        ImageRequest photoStringRequest = new ImageRequest(URL,
            new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    // set image
                    imageView.setImageBitmap(response);

                }
            },
            // These do nothing
            maxHeightPhoto,
            maxHeightPhoto,
            // I would say these do nothing but I don't actually know what these are supposed to do
            ImageView.ScaleType.CENTER_CROP,
            Bitmap.Config.RGB_565,
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorView.setText(error.toString());
                }
            }
        );
        requestQueue.add(photoStringRequest);
        return;
    }

    // Check on input change if new input makes input invalid
    TextWatcher inputTextWatcher(final TextView errorView){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Depending on what start and before actually are this could be simplified
                errorView.setText(validateString(s.toString()) ? "" : invalidInputErrorStr);
                return;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
    // Check on input change if new input makes input invalid
    TextWatcher inputTextWatcher2(final TextView errorView, final AutoCompleteTextView userInput, final ArrayAdapter<String> autoCompleteAdapter){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Depending on what start and before actually are this could be simplified
                errorView.setText(validateString(s.toString()) ? "" : invalidInputErrorStr);
                return;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
    // Validate string
    boolean validateString(String str){
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
