package com.kinnerapriyap.sugar.mediapreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import com.kinnerapriyap.sugar.R
import com.kinnerapriyap.sugar.ShergilViewModel
import kotlinx.android.synthetic.main.fragment_media_preview.*

class MediaPreviewFragment : Fragment() {

    private val viewModel: ShergilViewModel by activityViewModels()

    private val mediaPreviewAdapter by lazy {
        MediaPreviewAdapter()
    }

    private var listener: MediaPreviewFragmentListener? = null

    companion object {
        fun newInstance() =
            MediaPreviewFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_media_preview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener?.hideSpinnerAndPreviewButton()

        viewPager.adapter = mediaPreviewAdapter

        TabLayoutMediator(tabDots, viewPager) { tab, position ->
        }.attach()

        viewModel.getSelectedMedia().observe(
            requireActivity(),
            Observer {
                mediaPreviewAdapter.selectedMedia = it
            }
        )
    }

    override fun onDestroyView() {
        listener?.showSpinnerAndPreviewButton()
        super.onDestroyView()
        listener = null
    }

    fun setMediaPreviewFragmentListener(listener: MediaPreviewFragmentListener) {
        this.listener = listener
    }
}
