package com.example.dailyday

class EntryScore {
    private var score: Int = 0

    constructor() { }

    /**
     * Constructor for an entry score
     * @param score the score
     */
    constructor(score: Int) {
        if (score < -10 || score > 10) {
            throw IllegalArgumentException("Score must be between -10 and 10")
        }

        this.score = score
    }

    /**
     * get the score
     * @return the score
     */
    fun getScore(): Int {
        return score
    }

    /**
     * set the score
     * @param score the score
     */
    fun setScore(score: Int) {
        if (score < -10 || score > 10) {
            throw IllegalArgumentException("Score must be between -10 and 10")
        }

        this.score = score
    }
}