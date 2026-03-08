package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.databinding.FragmentArticleBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

class ArticleFragment : Fragment() {

    private val args: ArticleFragmentArgs by navArgs()

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var article: Article

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        val article: Article = args.article
        Log.d("codeleg", "Received article ID: ${article.id}, title: ${article.title}")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        article = args.article
        populateData()
    }

    private fun populateData() {

        binding.articleTitle.text = article.title
        binding.articleContent.text = article.text
        Glide.with(binding.articleImage.context).load(article.image).centerCrop()
            .placeholder(R.drawable.news_placeholder).error(R.drawable.image_unavailable)
            .into(binding.articleImage)
        binding.articleCategory.text = article.category ?: "Unknown"
        val metaData = "By ${article.authors?.joinToString(", ") ?: "Unknown"} | " + "${
            article.publishDate.take(10)
        } | " + "Sentiment: ${article.sentiment ?: "N/A"}"
        binding.articleMeta.text = metaData
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.articleContentLayout.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}