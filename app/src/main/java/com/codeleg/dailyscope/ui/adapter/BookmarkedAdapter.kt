package com.codeleg.dailyscope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.databinding.ItemArticleBinding

class BookmarkedAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onBookmarkClick: (Article) -> Unit,
    private var disableCache: Boolean = false
) : ListAdapter<Article, NewsListAdapter.NewsViewHolder>(NewsListAdapter.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListAdapter.NewsViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsListAdapter.NewsViewHolder(binding, onBookmarkClick)
    }

    override fun onBindViewHolder(holder: NewsListAdapter.NewsViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article, disableCache)

        holder.itemView.setOnClickListener {
            onItemClick(article)
        }
    }


}