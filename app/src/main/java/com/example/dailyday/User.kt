package com.example.dailyday

class User {
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    constructor() { }

    /**
     * Constructor for the user
     * @param username the username
     * @param email the email
     * @param password the password
     */
    constructor(username: String, email: String, password: String) {
        this.username = username
        this.email = email
        this.password = password
    }

    /**
     * Get the username
     * @return the username
     */
    fun getUsername(): String {
        return username
    }

    /**
     * Get the email
     */
    fun getEmail(): String {
        return email
    }
}