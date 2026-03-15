package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.databinding.FragmentBookmarkBinding
import com.codeleg.dailyscope.ui.adapter.BookmarkedAdapter
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.getValue

class BookmarkFragment : Fragment() {
    private val mainVM: MainViewModel by activityViewModels {
        val newsRepo = (requireActivity().application as DailyScope).newsRepository
        MainViewModelFactory(newsRepo)
    }
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private val bookmarkedAdapter by lazy {
        BookmarkedAdapter(
            onItemClick = { article ->
                val action =
                    BookmarkFragmentDirections.actionBookmarkFragmentToArticleFragment(article)
                findNavController().navigate(action)
            },
            onBookmarkClick = { article ->
                mainVM.setBookmark(article)
                Snackbar.make(binding.root , if (article.isBookmarked) "Added to bookmarks" else "Removed from bookmarks", Snackbar.LENGTH_SHORT).show()
            }
        )
    }
    private lateinit var rvBookmarked: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(layoutInflater , container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvBookmarked = binding.rvBookmarkedNews
        rvBookmarked.adapter = bookmarkedAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            mainVM.bookmarkedArticles.collect { articles ->
                bookmarkedAdapter.submitList(articles)
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menu.clear()
                menuInflater.inflate(R.menu.bookmark_page_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
               return when(menuItem.itemId){
                    R.id.option_remove_all_bookmarks -> {
                        showRemoveAllBookmarksConfirmation()
                        Snackbar.make(binding.root , "Cleared all bookmarks", Snackbar.LENGTH_SHORT).show()
                        true
                    }
                   else -> false
                }
            }

        } , viewLifecycleOwner , Lifecycle.State.STARTED)

    }
    private fun showRemoveAllBookmarksConfirmation() {
        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Clear Bookmarks")
            .setMessage("Are you sure you want to remove all items from bookmarks?")
            .setPositiveButton("Yes") { _, _ ->
                mainVM.clearBookmarks()
            }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }

}