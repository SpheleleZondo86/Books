package com.example.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private ProgressBar progressBar;
    private RecyclerView rvBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        progressBar = findViewById(R.id.pb_loading);
        rvBooks = findViewById(R.id.rv_books);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvBooks.setLayoutManager(linearLayoutManager);
        String query = getIntent().getStringExtra("Query");
        URL bookUrl;
        try {
            if (query==null || query.isEmpty()){
                bookUrl = ApiUtil.buildUrl("cooking");
            }else {
                bookUrl = new URL(query);
            }
            new BooksQueryTask().execute(bookUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        ArrayList<String> recentList = SharedPreferencesUtil.getQueryList(getApplicationContext());
        int itemNum = recentList.size();
        MenuItem recentMenu;
        for (int i=0;i<itemNum;i++){
            recentMenu = menu.add(Menu.NONE, i, Menu.NONE, recentList.get(i));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_advanced_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                int position = item.getItemId()+1;
                String preferenceName = SharedPreferencesUtil.QUERY+position;
                String query = SharedPreferencesUtil.getPrefsString(getApplicationContext(), preferenceName);
                String[] prefParams = query.split("\\,");
                String[] queryParams = new String[4];
                for (int i=0;i<prefParams.length;i++){
                    queryParams[i] = prefParams[i];
                }
                URL bookUrl = ApiUtil.buildUrl(
                        queryParams[0]==null?"":queryParams[0],
                        queryParams[1]==null?"":queryParams[1],
                        queryParams[2]==null?"":queryParams[2],
                        queryParams[3]==null?"":queryParams[3]
                );
                new BooksQueryTask().execute(bookUrl);
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
           URL bookUrl = ApiUtil.buildUrl(query);
           new BooksQueryTask().execute(bookUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
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
            TextView tvError = findViewById(R.id.textViewError);
            progressBar.setVisibility(View.INVISIBLE);
            if (result == null) {
                tvError.setText(R.string.error_message);
            }
            else {
                ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
                BooksAdapter adapter = new BooksAdapter(books);
                rvBooks.setAdapter(adapter);
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
