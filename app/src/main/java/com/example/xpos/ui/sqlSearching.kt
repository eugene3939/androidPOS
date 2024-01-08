package com.example.xpos.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xpos.R

class sqlSearching : Fragment() {

    companion object {
        fun newInstance() = sqlSearching()
    }

    private lateinit var viewModel: SqlSearchingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sql_searching, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SqlSearchingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}