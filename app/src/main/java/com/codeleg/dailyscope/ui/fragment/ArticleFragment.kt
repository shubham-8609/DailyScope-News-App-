package com.codeleg.dailyscope.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
import kotlin.getValue

class ArticleFragment : Fragment() {

    private val args: ArticleFragmentArgs by navArgs()

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private val mainVM: MainViewModel by activityViewModels {
        val newsRepo = (requireActivity().application as DailyScope).newsRepository
        MainViewModelFactory(newsRepo)
    }

    private lateinit var article: Article
    private lateinit var textToSpeech: TextToSpeech
    private var isSpeaking = false
    private var wasSpeaking = false

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
        lifecycleScope.launch {
            textToSpeech = TextToSpeech(requireContext()) { status ->

                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.language = Locale.US
                }

            }
        }
        binding.bookmarkButton.setImageResource(if(article.isBookmarked) R.drawable.filled_bookmark_24 else R.drawable.outline_bookmark_24)
        binding.speakButton.setOnClickListener {
            if (isSpeaking) {
                stopSpeaking()
            } else {
                speakArticle()
            }
        }
        binding.bookmarkButton.setOnClickListener { it ->
            article = article.copy(isBookmarked = !article.isBookmarked)
            mainVM.setBookmark(article)
            binding.bookmarkButton.setImageResource(if(article.isBookmarked) R.drawable.filled_bookmark_24 else R.drawable.outline_bookmark_24)
             Snackbar.make(it, if(article.isBookmarked) "Article bookmarked" else "Bookmark removed", Snackbar.LENGTH_SHORT).show()
        }
        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.article_page_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.option_open_in_web -> {
                        openInBrowser()
                        true
                    }

                    R.id.option_share_article -> {
                        shareArticle()
                        true
                    }

                    else -> false
                }
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
        lifecycleScope.launch {
            delay(700) // Simulate loading delay
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.articleContentLayout.visibility = View.VISIBLE
        }
    }

    private fun shareArticle() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, article.title)
            putExtra(Intent.EXTRA_TEXT, "${article.title}\n${article.url}")
        }

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun speakArticle(){
        val text = "${article.title}. ${article.text}. Summary:  ${article.summary ?: "No summary available."}"

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
        binding.speakButton.alpha = 1f

        isSpeaking = true
    }
    private fun stopSpeaking() {

        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        binding.speakButton.alpha  = 0.5f
        isSpeaking = false
    }

    private fun openInBrowser() {
        val url = article.url
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        if(isSpeaking) {
            stopSpeaking()
             wasSpeaking = true
        }
    }

    override fun onResume() {
        super.onResume()
        if(wasSpeaking){
            speakArticle()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}