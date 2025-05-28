package com.example.filmflare.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmflare.R
import com.example.filmflare.data.model.Genre
import com.example.filmflare.data.remote.RetrofitClient
import com.example.filmflare.databinding.FragmentMovieListBinding
import com.example.filmflare.utils.Constants
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListMovFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var adapter: AdaptMovie
    private var genreList: List<Genre> = emptyList()
    private var selectedGenreNames: List<String> = emptyList()
    private var selectedSort: String = "popularity.desc"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chipPopular.isChecked = true

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_movieListFragment_to_loginFragment)
        }

        adapter = AdaptMovie(emptyList()) { movie ->
            val action = ListMovFragmentDirections
                .actionMovieListFragmentToMovieDetailFragment(movie)
            findNavController().navigate(action)
        }

        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMovies.adapter = adapter

        fetchGenres()

        // Handle genre dropdown selection (multi-select)
        binding.spinnerGenres.setOnDismissListener {
            val input = binding.spinnerGenres.text.toString()
            selectedGenreNames = input.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            fetchMoviesByGenreIds()
        }

        // Handle chip sorting
        binding.categoryChipGroup.setOnCheckedChangeListener { _, checkedId ->
            val chip = view.findViewById<Chip>(checkedId)
            selectedSort = when (chip?.text.toString().lowercase()) {
                "top rated" -> "vote_average.desc"
                "upcoming" -> "release_date.desc"
                else -> "popularity.desc"
            }
            fetchMoviesByGenreIds()
        }
    }

    private fun getGenreIdsByNames(names: List<String>): List<Int> {
        return names.mapNotNull { name ->
            genreList.find { it.name.equals(name, ignoreCase = true) }?.id
        }
    }

    private fun fetchGenres() {
        genreList = listOf(
            Genre(28, "Action"),
            Genre(35, "Comedy"),
            Genre(18, "Drama"),
            Genre(27, "Horror"),
            Genre(878, "Science Fiction")
        )

        val genreNames = genreList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, genreNames)

        binding.spinnerGenres.setAdapter(adapter)
        binding.spinnerGenres.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        // Default = all genres (empty selection)
        selectedGenreNames = emptyList()
        fetchMoviesByGenreIds()
    }

    private fun fetchMoviesByGenreIds() {
        val genreIds = getGenreIdsByNames(selectedGenreNames)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getMoviesByGenre(
                    apiKey = Constants.API_KEY,
                    genreId = if (genreIds.isNotEmpty()) genreIds.joinToString(",") else "",
                    sortBy = selectedSort
                )

                val movies = response.results
                activity?.runOnUiThread {
                    adapter.updateData(movies)
                    if (movies.isEmpty()) {
                        Toast.makeText(requireContext(), "No movies found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to fetch movies", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
