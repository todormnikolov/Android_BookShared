package com.tnt.android.android_bookshared.common;

/**
 * Created by USER on 5.7.2016 г..
 */
public class Book {

    private String title;
    private String author;
    private String originalOwner;
    private String currentOwner;

    public Book(){

    }

    public Book(String title, String author, String owner, String currentOwner){
        setTitle(title);
        setAuthor(author);
        setOriginalOwner(owner);
        setCurrentOwner(currentOwner);
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOriginalOwner() {
        return originalOwner;
    }

    public void setOriginalOwner(String originalOwner) {
        this.originalOwner = originalOwner;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(String currentOwner) {
        this.currentOwner = currentOwner;
    }
}
