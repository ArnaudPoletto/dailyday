package com.example.dailyday

class User {
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    constructor() { }

    constructor(username: String, email: String, password: String) {
        this.username = username
        this.email = email
        this.password = password
    }

    fun getUsername(): String {
        return username
    }

    fun getEmail(): String {
        return email
    }
}