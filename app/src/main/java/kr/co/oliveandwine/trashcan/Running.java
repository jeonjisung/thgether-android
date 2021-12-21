package kr.co.oliveandwine.trashcan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.annotation.Nonnull;

import kr.co.oliveandwine.trashcan.databinding.FragmentRunningBinding;

public class Running extends Fragment {

    private FragmentManager fragmentManager;
    private Running_Tab1 RunningTab1 = new Running_Tab1();
    private Running_Tab2 RunningTab2 = new Running_Tab2();

    FragmentRunningBinding binding;

    ViewGroup viewGroup;

    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentManager = getChildFragmentManager();
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_running, container, false);
        View root = binding.getRoot();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.running_fragment, RunningTab1).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = binding.runningNavigation;
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        return root;

    }

    private class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.running_tab1:
                    transaction.replace(R.id.running_fragment, RunningTab1).commitAllowingStateLoss();
                    break;
                case R.id.running_tab2:
                    transaction.replace(R.id.running_fragment, RunningTab2).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}