package com.example.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class BookListActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        progressBar = findViewById(R.id.pb_loading);
        URL bookUrl = ApiUtil.buildUrl("cooking");
        new BooksQueryTask().execute(bookUrl);

    }

    public class BooksQueryTask extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try{
                result = ApiUtil.getJSON(searchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tvResults = findViewById(R.id.textViewResponse);
            TextView tvError = findViewById(R.id.textViewError);
            progressBar.setVisibility(View.INVISIBLE);
            if (result == null) {
                tvError.setText(R.string.error_message);
            }
            else {
                tvResults.setText(result);
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
