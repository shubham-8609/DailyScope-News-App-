package com.codeleg.dailyscope.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.codeleg.dailyscope.DailyScope
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.database.model.Article
import com.codeleg.dailyscope.databinding.FragmentSearchBinding
import com.codeleg.dailyscope.ui.adapter.NewsListAdapter
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import com.codeleg.dailyscope.ui.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.paging.LoadState
import com.google.android.material.chip.Chip

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels {
        val newsRepo = (requireActivity().application as DailyScope).newsRepository
        val settingsRepo = (requireActivity().application as DailyScope).settingsRepository
        MainViewModelFactory(newsRepo , settingsRepo)
    }
    private lateinit var searchAdapter: NewsListAdapter
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchMenu()
        setupSuggestionChips()
        collectResults()
        observeLoadState()
        setupBackPressHandling()
    }


    private fun setupSuggestionChips() {

        val chipGroup = binding.searchSuggestions

        for (i in 0 until chipGroup.childCount) {

            val chip = chipGroup.getChildAt(i) as? Chip ?: continue

            chip.setOnClickListener {

                val query = chip.text.toString()

                searchView?.apply {
                    setQuery(query, true) // true = submit
                    clearFocus()
                }

                searchViewModel.submitQuery(query)
                binding.suggestionContainer.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = NewsListAdapter(::onArticleClicked, ::onBookmarkClicked , disableCache = searchViewModel.disableCache)
        binding.rvSearchResults.adapter = searchAdapter
        binding.rvSearchResults.setHasFixedSize(true)
    }

    private fun setupSearchMenu() {

        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_page_menu, menu)

                val searchItem = menu.findItem(R.id.menu_search_action)
                val searchActionView = searchItem.actionView as SearchView

                searchView = searchActionView

                searchView?.apply {

                    queryHint = getString(R.string.search_hint)

                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                        override fun onQueryTextSubmit(query: String?): Boolean {

                            val term = query?.trim().orEmpty()

                            if (term.isNotBlank()) {
                                searchViewModel.submitQuery(term)
                                clearFocus()
                                binding.suggestionContainer.visibility = View.GONE
                            }

                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {

                            return true
                        }
                    })

                    setOnCloseListener {
                        searchViewModel.clearQuery()
                        binding.suggestionContainer.visibility = View.GONE
                        true
                    }

                    collectAutoOpenPreference(searchItem, this)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun collectAutoOpenPreference(searchItem: MenuItem, searchActionView: SearchView) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.autoOpenSearch
                    .collectLatest { enabled ->
                        if (enabled) {
                            searchItem.expandActionView()
                            searchActionView.onActionViewExpanded()
                        }
                    }
            }
        }
    }

    private fun collectResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchResults.collectLatest { pagingData ->
                    searchAdapter.submitData(pagingData)
                }
            }
        }
    }
    private fun observeLoadState() {
        searchAdapter.addLoadStateListener { loadState ->
            val query = searchViewModel.query.value
            val isEmptyResult = loadState.refresh is LoadState.NotLoading && searchAdapter.itemCount == 0 && query.isNotBlank()
            val shouldShowEmpty = query.isBlank() || isEmptyResult
            binding.emptyState.isVisible = shouldShowEmpty
            binding.emptyState.text = if (query.isBlank()) {
                getString(R.string.search_empty_state)
            } else {
                getString(R.string.search_no_results)
            }
        }
    }

    private fun setupBackPressHandling() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBack()
        }
    }

    private fun handleBack() {

        searchView?.let {

            if (!it.isIconified) {
                it.setQuery("", false)
                it.isIconified = true
                it.clearFocus()
                hideKeyboard(it)
                return
            }
        }

        findNavController().navigateUp()
    }

    private fun onArticleClicked(article: Article) {
        val action = SearchFragmentDirections.actionSearchFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }

    private fun onBookmarkClicked(article: Article) {
        searchViewModel.setBookmark(article)
        Snackbar.make(binding.root, if (article.isBookmarked) "Added to bookmarks" else "Removed from bookmarks", Snackbar.LENGTH_SHORT).show()
    }

    private fun showKeyboard(targetView: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        targetView.post { imm.showSoftInput(targetView, InputMethodManager.SHOW_IMPLICIT) }
    }

    private fun hideKeyboard(targetView: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(targetView.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView = null
        _binding = null
    }


}