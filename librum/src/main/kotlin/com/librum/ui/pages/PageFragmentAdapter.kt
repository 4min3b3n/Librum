package com.librum.ui.pages

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.librum.ui.pages.EpubPageFragment
import org.readium.r2_streamer.model.publication.link.Link
import javax.inject.Inject

/**
 * @author lusinabrian on 05/09/17.
 * @Notes Page adapter for the publication
 */
class PageFragmentAdapter
@Inject
constructor(fragmentManager: FragmentManager, var spineReferences : List<Link>,
            var epubFileName : String) : FragmentStatePagerAdapter(fragmentManager){

    override fun getCount() = spineReferences.size

    override fun getItem(position: Int): Fragment {
        val mPageFragment = EpubPageFragment.newInstance(position, epubFileName, spineReferences[position])
        mPageFragment.setFragmentPos(position)
        return mPageFragment
    }
}