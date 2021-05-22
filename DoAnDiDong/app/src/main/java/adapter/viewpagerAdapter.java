package adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.uit_learning.TaiKhoanFragment;
import com.example.uit_learning.TaiLieuFragment;
import com.example.uit_learning.TracNghiemFragment;
import com.example.uit_learning.TrangChuFragment;

public class viewpagerAdapter extends FragmentStatePagerAdapter {
    public viewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position){
            case 0:
                return new TrangChuFragment();
            case 1:
                return new TaiLieuFragment();
            case 2:
                return new TracNghiemFragment();
            case 3:
                return new TaiKhoanFragment();
            default:
                return new TrangChuFragment();
        }
        //return  null;
    }

    @Override
    public int getCount()
    {
        return 5;
    }
}
