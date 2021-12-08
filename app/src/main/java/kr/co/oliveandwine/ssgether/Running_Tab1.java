package kr.co.oliveandwine.ssgether;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import android.widget.TextView;

import javax.annotation.Nonnull;

import kr.co.oliveandwine.ssgether.Save.Save_Var;
import kr.co.oliveandwine.ssgether.databinding.FragmentRunningTab1Binding;
import kr.co.oliveandwine.ssgether.util.PedometerService;
import me.itangqi.waveloadingview.WaveLoadingView;

import static kr.co.oliveandwine.ssgether.MainActivity.walk_num;


public class Running_Tab1 extends Fragment {
    WaveLoadingView waveLoadingView;

    public static Running_Tab1 running;

    ViewGroup viewGroup;

    TextView manboText, kcalText, distanceText;

    private FragmentRunningTab1Binding binding;
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_running_tab1, container, false);
        View root = binding.getRoot();
        manboText = binding.manboCount;
        kcalText = binding.kcal;
        distanceText = binding.distanceText;
        running = this;
        walk_num = manboText;

        waveLoadingView = binding.waveLoadingView;


        kcalText.setText(Float.toString(PedometerService.mSteps/33));
        distanceText.setText(Float.toString(PedometerService.mSteps/1.25f));
        manboText.setText(Integer.toString(PedometerService.mSteps));
        waveLoadingView.setProgressValue(PedometerService.mSteps/10);


        return root;
    }

    public FragmentRunningTab1Binding getBinding() {
        return binding;
    }

    public void setBinding(FragmentRunningTab1Binding binding) {
        this.binding = binding;
    }
}