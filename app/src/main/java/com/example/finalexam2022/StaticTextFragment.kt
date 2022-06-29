package com.example.finalexam2022

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.finalexam2022.databinding.FragmentTextBinding


class StaticTextFragment : Fragment() {
    var binding: FragmentTextBinding?= null //
    val data = arrayListOf<String>("ImageData1","ImageData2","ImageData3")
    val myViewModel:MyViewModel by activityViewModels() // 액티비티와 연동되는 뷰 모델

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTextBinding.inflate(layoutInflater,  container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val i = requireActivity().intent
        val imgNum = i.getIntExtra("imgNum", -1)
        if(imgNum != -1){ // intent 에 의해 정보를 넘겨받음
            binding!!.textView.text = data[imgNum]
        }else{ // landscape
            myViewModel.selectednum.observe(viewLifecycleOwner, Observer {
                binding!!.textView.text = data[it] // it 에 바뀐 값이 들어옴
            })
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}