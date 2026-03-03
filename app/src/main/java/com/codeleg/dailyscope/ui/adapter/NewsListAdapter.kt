package com.codeleg.dailyscope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.databinding.ItemArticleBinding


class NewsListAdapter :
    ListAdapter<Article, NewsListAdapter.NewsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {

        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NewsViewHolder(
        private val binding: ItemArticleBinding
    ) : ViewHolder(binding.root) {

        fun bind(article: Article) {
            with(binding) {
                newsTitle.text = article.title
                newsSummary.text = article.summary ?: (article.text.take(100) + "...")
                categoryChip.text = article.category ?: "Unknown"
                val metaData = "By ${article.authors?.joinToString(", ") ?: "Unknown"} | " +
                        "${article.publishDate.take(10)} | " +
                        "Sentiment: ${article.sentiment ?: "N/A"}"
                newsMeta.text = metaData
                Glide.with(newsImage.context)
                    .load(article.image)
                    .centerCrop()
                    .placeholder(R.drawable.news_placeholder)
                    .error(R.drawable.error_image)
                    .into(newsImage)

            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem == newItem
        }
    }
}