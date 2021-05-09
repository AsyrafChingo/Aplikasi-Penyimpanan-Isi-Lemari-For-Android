package com.dicoding.assosiate.lemariku

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class CostumpagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    var mFm = fm
    var mFragmentItems :ArrayList<Fragment> = ArrayList()
    var mFragmentTitles : ArrayList<String> = ArrayList()

    //Perlu di buat fungsi untuk menamabah fragment

    fun addFragment(fragmentItem : Fragment, fragmentTitle:String)
    {
        mFragmentItems.add(fragmentItem)
        mFragmentTitles.add(fragmentTitle)
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentItems[position]
    }

    override fun getCount(): Int {
        return mFragmentItems.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitles[position]
    }
}