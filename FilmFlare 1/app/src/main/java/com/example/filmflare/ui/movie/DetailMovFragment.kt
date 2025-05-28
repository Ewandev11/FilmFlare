package com.example.filmflare.ui.movie

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.filmflare.R
import com.example.filmflare.databinding.FragmentMovieDetailBinding
import com.example.filmflare.utils.Constants

class DetailMovFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailMovFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val movie = args.movie

        binding.textTitle.text = movie.title
        binding.textOverview.text = movie.overview
        binding.textRating.text = getString(R.string.rating_format, movie.vote_average)
        binding.textReleaseDate.text = getString(R.string.release_format, movie.release_date)

        binding.progressBar.visibility = View.VISIBLE

        Glide.with(this)
            .load(Constants.IMAGE_BASE_URL + movie.poster_path)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any, target: Target<Drawable>,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(binding.imagePoster)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
