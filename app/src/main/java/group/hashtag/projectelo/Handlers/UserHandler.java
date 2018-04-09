package group.hashtag.projectelo.Handlers;

/**
 * Created by nikhilkamath on 18/02/18.
 */

public class UserHandler {
    public String name;
    public String UserId;
    public String Display_Pic;
    public String country;
    public String dob_date;
    public String dob_month;
    public String dob_year;
    public String webLink;
    public String gender;
    public String likes;
    public String email;

    /**
     * Default constructor
     */
    // DataSnapshot.getValue(User.class)
    public UserHandler() {
    }

    /**
     * Parameterized Constructor
     * @param name
     * @param userId
     * @param email
     */
    public UserHandler(String name, String userId, String email) {
        this.name = name;
        UserId = userId;
        this.email = email;
    }

    /**
     * Parameterized Constructor
     * @param name
     * @param email
     * @param UserId
     * @param DisplayPic
     */
    public UserHandler(String name, String email, String UserId, String DisplayPic) {
        this.name = name;
        this.email = email;
        this.UserId = UserId;
        this.Display_Pic = DisplayPic;
    }

    /**
     * Parameterized Constructor
     * @param name
     * @param userId
     * @param country
     * @param dob_month
     * @param dob_year
     * @param webLink
     * @param email
     * @param gender
     * @param dob_date
     * @param likes
     * @param DisplayPic
     */
    public UserHandler(String name, String userId, String country, String dob_month, String dob_year, String webLink, String email, String gender, String dob_date, String likes, String DisplayPic) {
        this.name = name;
        UserId = userId;
        this.country = country;
        this.dob_date = dob_date;
        this.dob_month = dob_month;
        this.dob_year = dob_year;
        this.webLink = webLink;
        this.email = email;
        this.gender = gender;
        this.likes = likes;
        this.Display_Pic = DisplayPic;
    }

    /**
     * Parameterized Constructor
     * @param name
     * @param userId
     * @param country
     * @param dob_month
     * @param dob_year
     * @param webLink
     * @param email
     * @param gender
     * @param dob_date
     * @param likes
     */
    public UserHandler(String name, String userId, String country, String dob_month, String dob_year, String webLink, String email, String gender, String dob_date, String likes) {
        this.name = name;
        UserId = userId;
        this.country = country;
        this.dob_date = dob_date;
        this.dob_month = dob_month;
        this.dob_year = dob_year;
        this.webLink = webLink;
        this.email = email;
        this.gender = gender;
        this.likes = likes;
    }

    /**
     * Parameterized Constructor
     * @param name
     * @param userId
     * @param country
     * @param dob_month
     * @param dob_year
     * @param webLink
     * @param email
     * @param gender
     * @param dob_date
     */
    public UserHandler(String name, String userId, String country, String dob_month, String dob_year, String webLink, String email, String gender, String dob_date) {
        this.name = name;
        UserId = userId;
        this.country = country;
        this.dob_date = dob_date;
        this.dob_month = dob_month;
        this.dob_year = dob_year;
        this.webLink = webLink;
        this.email = email;
        this.gender = gender;
    }

//    public String getDisplay_Pic() {
//        return Display_Pic;
//    }


    /**
     * Parameterized Constructor
     * @param displayPic
     */
    public UserHandler(String displayPic) {
        Display_Pic = displayPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Default constructor required for calls to

    public String getLikes() {
        return likes;
    }

    public String getCountry() {
        return country;
    }

    public String getDob_month() {
        return dob_month;
    }

    public String getDob_year() {
        return dob_year;
    }

    public String getWebLink() {
        return webLink;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob_date() {
        return dob_date;
    }

    public String getGender() {
        return gender;
    }
}
