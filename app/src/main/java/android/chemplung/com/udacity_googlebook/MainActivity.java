package android.chemplung.com.udacity_googlebook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //    // Log
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q=";
    private String encodedurl = new String();
    ArrayList<Book> booklist = new ArrayList<>();
    ListView bookListView;
    TextView noResult;
    private BookAdapter bookAdapter;
    private ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookAdapter = new BookAdapter(this, booklist);
        bookListView = (ListView) findViewById(R.id.list_book_item);
        noResult = (TextView) findViewById(R.id.empty_book);
        Button search = (Button) findViewById(R.id.search_button);
        final LinearLayout result = (LinearLayout) findViewById(R.id.result);
        bookListView.setAdapter(bookAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey("list")) {
            {
                result.setVisibility(View.VISIBLE);
                noResult.setVisibility(View.GONE);
                booklist = savedInstanceState.getParcelableArrayList("list");
                bookAdapter.addAll(booklist);
            }
        }
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editQuery = (EditText) findViewById(R.id.search_box);
                try {
                    encodedurl = URLEncoder.encode(String.valueOf(editQuery.getText()), "UTF-8");
                    System.out.println(encodedurl);
                } catch (UnsupportedEncodingException e) {
                    System.err.println(e);
                }

                int lenghtBook = editQuery.length();
                if (lenghtBook == 0) {
                    Toast.makeText(MainActivity.this, "Error : Please enter search query", Toast.LENGTH_SHORT).show();
                    editQuery.requestFocus();
                } else {

                    if (isNetworkAvailable()) {
                        // Kick off an {@link AsyncTask} to perform the network request
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute(GOOGLE_BOOKS_API + encodedurl + "&maxResults=20");
                    } else {
                        Toast.makeText(getApplicationContext(), "Network not available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book currentBook = bookAdapter.getItem(i);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });
    }

    //Check if network is available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LinearLayout result = (LinearLayout) findViewById(R.id.result);
        result.setVisibility(View.VISIBLE);
        outState.putParcelableArrayList("list", booklist);
        super.onSaveInstanceState(outState);
    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        //Show progress dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDialog = new ProgressDialog(MainActivity.this);
            progDialog.setMessage("Loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.show();
        }

        @Override
        protected List<Book> doInBackground(String... url) {
            List<Book> result = QueryUtils.fetchBookData(url[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            bookAdapter.clear();
            if (books != null && !books.isEmpty()) {
                bookListView.setVisibility(View.VISIBLE);
                noResult.setVisibility(View.GONE);
                booklist = (ArrayList<Book>) books;
                bookAdapter.addAll(booklist);
                progDialog.dismiss();
            } else {
                bookListView.setVisibility(View.GONE);
                noResult.setVisibility(View.VISIBLE);
            }
        }
    }
}