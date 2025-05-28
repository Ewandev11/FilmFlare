package com.example.filmflare.data.model

data class MovieResponse(
    val results: List<Movie>,       // List of movies
    val page: Int? = null,          // current page number (if the API supports pagination)
    val total_pages: Int? = null,   // total number of pages (if pagination is supported)
    val total_results: Int? = null, // total number of results (if pagination is supported)
    val status_code: Int? = null,   // status code to handle API errors
    val status_message: String? = null // error message from the API
)
