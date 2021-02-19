package com.manuj.userdairy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabLayoutAdapter  extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public TabLayoutAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Users_Fragment tab1 = new Users_Fragment();
                return tab1;
            case 1:
                Enroll_Fragment tab2 = new Enroll_Fragment();
                return tab2;
            default:

                return null;
        }
    }

    @Override
    public int getCount() {
        return  mNumOfTabs;
    }
}
