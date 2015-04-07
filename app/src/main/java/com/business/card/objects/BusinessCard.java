package com.business.card.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class BusinessCard {

    private String id;
    private String userId;
    private String title;
    private String email;
    private String phone;
    private String address;
    private String firstName;
    private String lastName;
    private String isPublic;

    public static BusinessCard parseBusinessCardFromJson(JSONObject json) {
        BusinessCard businessCard = new BusinessCard();
        try {
            if (json.has("id")) {
                businessCard.setId(json.getString("id"));
            }

            if (json.has("firstName")) {
                businessCard.setFirstName(json.getString("firstName"));
            }

            if (json.has("lastname")) {
                businessCard.setLastName(json.getString("lastname"));
            }

            businessCard.setUserId(json.getString("userId"));
            businessCard.setTitle(json.getString("title"));
            businessCard.setEmail(json.getString("email"));
            businessCard.setPhone(json.getString("phone"));
            businessCard.setAddress(json.getString("address"));
            businessCard.setIsPublic(json.getString("public"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return businessCard;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
}
