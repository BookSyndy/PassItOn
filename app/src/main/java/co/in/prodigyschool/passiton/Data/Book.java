package co.in.prodigyschool.passiton.Data;


/**
*Book POJO.
 */
public class Book {

    /* for filtering */
    public static final String FIELD_CITY = "city";
    public static final String FIELD_AREA = "area";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRICE = "price";

    private String bookName,bookDescription,bookAddress,bookPhoto;
    boolean isTextbook;
    private int bookPrice;
    private int gradeNumber,boardNumber;
    private String userId;

    public  Book(){

    }
    //for user entry
    public Book(String userId, boolean isTextbook, String bookName, String bookDescription, int gradeNumber, int boardNumber,int bookPrice, String bookAddress,String bookPhoto){

        this.userId = userId;
        this.bookName = bookName;
        this.bookDescription = bookDescription;
        this.bookAddress = bookAddress;
        this.gradeNumber = gradeNumber;
        this.boardNumber = boardNumber;
        this.bookPrice = bookPrice;
        this.isTextbook = isTextbook;
        this.bookPhoto = bookPhoto;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public String getBookAddress() {
        return bookAddress;
    }

    public void setBookAddress(String bookAddress) {
        this.bookAddress = bookAddress;
    }

    public boolean isTextbook() {
        return isTextbook;
    }

    public void setTextbook(boolean textbook) {
        isTextbook = textbook;
    }



    public int getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getGradeNumber() {
        return gradeNumber;
    }

    public void setGradeNumber(int gradeNumber) {
        this.gradeNumber = gradeNumber;
    }

    public int getBoardNumber() {
        return boardNumber;
    }

    public void setBoardNumber(int boardNumber) {
        this.boardNumber = boardNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookPhoto() {
        return bookPhoto;
    }

    public void setBookPhoto(String bookPhoto) {
        this.bookPhoto = bookPhoto;
    }
}
