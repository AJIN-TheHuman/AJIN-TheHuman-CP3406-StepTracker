package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

import retrofit2.http.GET

/**
 * Data model for the Quote API.
 * q is the quote text, a is the author.
 */
data class Quote(val q: String, val a: String)

/**
 * Retrofit interface for fetching random quotes from zenquotes.io
 */
interface QuoteApiService {
    @GET("api/random")
    suspend fun getRandomQuote(): List<Quote>
}
