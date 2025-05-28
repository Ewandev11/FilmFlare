package com.example.filmflare.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filmflare.data.model.Movie
import com.example.filmflare.databinding.ItemMovieBinding
import com.example.filmflare.utils.Constants

class AdaptMovie(
    private var movieList: List<Movie>,
    private val clickListener: (Movie) -> Unit
) : RecyclerView.Adapter<AdaptMovie.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie, clickListener)
    }

    override fun getItemCount(): Int = movieList.size

    class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie, clickListener: (Movie) -> Unit) {
            binding.textTitle.text = movie.title ?: "No Title Available"

            Glide.with(binding.root.context)
                .load(Constants.IMAGE_BASE_URL + movie.poster_path)
                .into(binding.imagePoster)

            binding.textRating.text = "Rating: ${movie.vote_average ?: "N/A"}"

            binding.root.setOnClickListener {
                clickListener(movie)
            }
        }
    }

    fun updateData(movies: List<Movie>) {
        movieList = movies
        notifyDataSetChanged()
    }
}
