package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.codeleg.dailyscope.database.model.NewsUiState
import com.codeleg.dailyscope.database.network.RetrofitInstance
import com.codeleg.dailyscope.database.repository.NewsRepository
import com.codeleg.dailyscope.databinding.FragmentHomeBinding
import com.codeleg.dailyscope.ui.adapter.NewsListAdapter
import com.codeleg.dailyscope.ui.viewmodel.MainViewModel
import com.codeleg.dailyscope.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var newsAdapter: NewsListAdapter
    private val newsRepo by lazy {
        NewsRepository(RetrofitInstance.newsApi)
    }
    val mainVM: MainViewModel by activityViewModels {
        MainViewModelFactory(newsRepo)
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainVM.topNews.collect { state ->
                    when (state) {

                        is NewsUiState.Loading -> {
                            Toast.makeText(requireContext(), "Loading news...", Toast.LENGTH_SHORT)
                                .show()
                            binding.swipeRefresh.isRefreshing = true
                        }

                        is NewsUiState.Success -> {
                            newsAdapter.submitList(state.news)
                            binding.swipeRefresh.isRefreshing  = false
                        }

                        is NewsUiState.Error -> {
                            binding.rvNews.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Error fetching news: ${state.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }
                }
            }
        }
       if(mainVM.topNews.value is NewsUiState.Loading) mainVM.fetchNews()

        binding.swipeRefresh.setOnRefreshListener {
            mainVM.fetchNews()
        }

    }

    private fun setupRecyclerView() {
        newsAdapter = NewsListAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}