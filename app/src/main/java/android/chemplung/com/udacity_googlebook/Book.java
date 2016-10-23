package android.chemplung.com.udacity_googlebook;

/**
 * Created by NUSNAFIF on 10/22/2016.
 */

public class Book {

    private String title;
    private String author;
    private String url;

    public Book(String title, String author, String url) {
        this.title = title;
        this.author = author;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthors(String authors) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
