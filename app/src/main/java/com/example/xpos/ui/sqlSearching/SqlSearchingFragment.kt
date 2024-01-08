package com.example.xpos.ui.sqlSearching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.xpos.databinding.FragmentSlideshowBinding
import com.example.xpos.databinding.FragmentSqlSearchingBinding

class SqlSearchingFragment : Fragment() {

    private var _binding: FragmentSqlSearchingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSqlSearchingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 初始化 ViewModel
        val sqlSearchingViewModel =
            ViewModelProvider(this).get(SqlSearchingViewModel::class.java)

        // 在這裡添加你的邏輯

        val textView: TextView = binding.textSqlSearchShow
        sqlSearchingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
