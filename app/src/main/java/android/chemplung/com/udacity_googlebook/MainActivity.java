package android.chemplung.com.udacity_googlebook;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // GoogleBook API url
    private static final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes/";
    private String keyword = new String();
    public Button btnSearch;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Handle Error to MainActivity
    private TextView emptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = (Button) findViewById(R.id.search_button);
        btnSearch.setOnClickListener(this);
    }

    public void onClick(View view) {
        TextView searchBook = (TextView) findViewById(R.id.search_box);
        keyword = String.valueOf(searchBook.getText());
        int lenghtBook = searchBook.length();
        if (lenghtBook == 0) {
            Toast.makeText(this, "Error : Please enter search query", Toast.LENGTH_SHORT).show();
            searchBook.requestFocus();
        } else {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(MainActivity.this, "Network is not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            BookAsyncTask bookAsyncTask = new BookAsyncTask();
            bookAsyncTask.execute();
        }
    }


    private void updateUi(List<Book> books) {
        ListView bookListView = (ListView) findViewById(R.id.list_book_item);
        final BookAdapter bookAdapter = new BookAdapter(this, books);
        bookListView.setEmptyView(findViewById(R.id.empty_book));
        bookListView.setAdapter(bookAdapter);

        // Open book base on URL given
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Book currentBook = bookAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            onClick(null);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            onClick(null);
        }
    }


    private class BookAsyncTask extends AsyncTask<URL, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(URL... urls) {
            List<Book> books = QueryUtils.fetchBookData(GOOGLE_BOOKS_API + "?q=" + keyword + "&maxResults=20");
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books == null) {
                updateUi(new ArrayList<Book>());
                // Show error when no data return
                emptyStateTextView = (TextView) findViewById(R.id.empty_book);
                emptyStateTextView.setText(R.string.no_book_found);
            } else {
                updateUi(books);
            }
        }
    }
}
