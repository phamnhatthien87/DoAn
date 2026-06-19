package model;

public class Book {
    private int id;
    private String title;
    private String author;
    private int quantity;
    private int categoryId;
    private int publisherId;
    private String categoryName;
    private String publisherName;

    public Book() {}

    public Book(int id, String title, String author, int quantity, int categoryId, int publisherId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.publisherId = publisherId;
    }

    public Book(int id, String title, String author, int quantity, String categoryName, String publisherName) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.categoryName = categoryName;
        this.publisherName = publisherName;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getQuantity() { return quantity; }
    public int getCategoryId() { return categoryId; }
    public int getPublisherId() { return publisherId; }
    public String getCategoryName() { return categoryName; }
    public String getPublisherName() { return publisherName; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
}
