package com.codeleg.dailyscope.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* requireActivity().addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                menu.clear()
                menuInflater.inflate(R.menu.search_page_menu, menu)
                val searchItem = menu.findItem(R.id.option_search_news)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = "Search news..."
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query.isNullOrBlank()) {
                            Snackbar.make(
                                binding.root,
                                "Please enter a search query",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            return true
                        }
                        return true

                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        // You can implement live search here if needed
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }

        }, viewLifecycleOwner , Lifecycle.State.RESUMED)*/

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}