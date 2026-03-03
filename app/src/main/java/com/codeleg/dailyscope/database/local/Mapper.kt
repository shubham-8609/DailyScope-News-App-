package com.codeleg.dailyscope.database.local

import com.codeleg.dailyscope.database.model.Article

fun   Article.toEntity(): ArticleEntity {
    return ArticleEntity(
        id = this.id,
        title = this.title,
        text = this.text,
        summary = this.summary,
        url = this.url,
        image = this.image,
        video = this.video,
        publishDate = this.publishDate,
        authors = this.authors,
        category = this.category,
        language = this.language,
        sourceCountry = this.sourceCountry,
        sentiment = this.sentiment
    )
}
fun ArticleEntity.toArticle(): Article {
    return Article(
        id = this.id,
        title = this.title,
        text = this.text,
        summary = this.summary,
        url = this.url,
        image = this.image,
        video = this.video,
        publishDate = this.publishDate,
        authors = this.authors,
        category = this.category,
        language = this.language,
        sourceCountry = this.sourceCountry,
        sentiment = this.sentiment
    )
}