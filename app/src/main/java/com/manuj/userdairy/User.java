package com.manuj.userdairy;

public class User {
    public String FirstName;
    public String LastName;
    public String DateOfBirth;
    public String Gender;
    public String Country;
    public String State;
    public String City;
    public String Phone;
    public String ImageUid;


    public User() {
    }

    public User(String firstName, String lastName, String dateOfBirth, String gender, String country, String state, String city, String phone,String imageUid) {
        FirstName = firstName;
        LastName = lastName;
        DateOfBirth = dateOfBirth;
        Gender = gender;
        Country = country;
        State = state;
        City = city;
        Phone = phone;
        ImageUid=imageUid;
    }

    @Override
    public String toString() {
        return "User{" +
                "FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", DateOfBirth='" + DateOfBirth + '\'' +
                ", Gender='" + Gender + '\'' +
                ", Country='" + Country + '\'' +
                ", State='" + State + '\'' +
                ", City='" + City + '\'' +
                ", Phone='" + Phone + '\'' +
                '}';
    }
}
