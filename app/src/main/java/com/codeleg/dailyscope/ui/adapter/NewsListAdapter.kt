package com.codeleg.dailyscope.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.databinding.ItemArticleBinding


class NewsListAdapter(private val onItemClick: (Article) -> Unit) :
    PagingDataAdapter<Article, NewsListAdapter.NewsViewHolder>(DiffCallback) {

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
        holder.itemView.setOnClickListener {
                getItem(position)?.let { article ->
                    onItemClick(article)
                }
            }
    }

    class NewsViewHolder(
        private val binding: ItemArticleBinding
    ) : ViewHolder(binding.root) {

        fun bind(article: Article?) {
            Glide.with(binding.newsImage.context).clear(binding.newsImage)
            with(binding) {
                article?.let {
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
                        .override(600, 325)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.news_placeholder)
                        .error(R.drawable.image_unavailable)
                        .into(newsImage)

                }
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