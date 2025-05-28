package com.example.filmflare.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmflare.R
import com.example.filmflare.data.model.Movie
import com.example.filmflare.data.model.Genre
import com.example.filmflare.data.remote.RetrofitClient
import com.example.filmflare.databinding.FragmentMovieListBinding
import com.example.filmflare.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListMovFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var adapter: AdaptMovie
    private var selectedCategory: String = "popular" // Default category
    private var selectedGenre: String? = null // Selected genre name
    private var genreList: List<Genre> = emptyList() // Holds the list of genres

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_movieListFragment_to_loginFragment)
        }

        // Initialize the adapter with an empty list
        adapter = AdaptMovie(emptyList()) { movie ->
            val action = ListMovFragmentDirections
                .actionMovieListFragmentToMovieDetailFragment(movie)
            findNavController().navigate(action)
        }

        // Set up the RecyclerView with the AdaptMovie adapter
        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMovies.adapter = adapter

        // Fetch genres
        fetchGenres()

        // Fetch movies for the default category
        fetchCategory(selectedCategory)

        // Handle genre selection

        binding.spinnerGenres.setOnItemClickListener { _, _, position, _ ->
            selectedGenre = genreList[position].name // Set the selected genre name

            // Now fetch movies based on the selected category and filter them by the selected genre
            fetchCategory(selectedCategory) // This method will fetch movies and pass them to filterMovies()
        }

        // Handle category selection from chip group
        binding.categoryChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            selectedCategory = chip?.text.toString().lowercase()
            fetchCategory(selectedCategory)
        }
    }

    // Fetch movies by category (upcoming, top rated, popular)
    private fun fetchCategory(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = when (category) {
                    "upcoming" -> RetrofitClient.apiService.getUpcomingMovies(Constants.API_KEY)
                    "top_rated" -> RetrofitClient.apiService.getTopRatedMovies(Constants.API_KEY)
                    else -> RetrofitClient.apiService.getPopularMovies(Constants.API_KEY)
                }
                val movies = response.results
                activity?.runOnUiThread {
                    filterMovies(movies) // Apply genre filter after fetching movies
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error fetching $category movies", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Helper function to get genre ID by name
    private fun getGenreIdByName(genreName: String?): Int? {
        return genreList.find { it.name.equals(genreName, ignoreCase = true) }?.id
    }

    // Function to filter movies based on the selected genre
    private fun filterMovies(movies: List<Movie>) {
        val filteredMovies = if (selectedGenre != null) {
            val selectedGenreId = getGenreIdByName(selectedGenre)
            movies.filter { it.genre_ids.contains(selectedGenreId) } // Corrected filtering
        } else {
            movies // No genre selected, return all movies
        }

        adapter.updateData(filteredMovies) // Update the adapter with the filtered movie list
    }

    // Fetch genres from the API and store them in genreList
    private fun fetchGenres() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getGenres(apiKey = Constants.API_KEY)
                genreList = response.genres // Store the list of genres for future use
                activity?.runOnUiThread {
                    // Optionally populate the genre spinner or chips
                    val genreNames = genreList.map { it.name }
                    val genreAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, genreNames)
                    binding.spinnerGenres.setAdapter(genreAdapter)
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error fetching genres", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
