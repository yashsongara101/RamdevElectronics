package com.believe.ramdevelectronics.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.believe.ramdevelectronics.R;
import com.believe.ramdevelectronics.TabAdapter;
import com.believe.ramdevelectronics.ui.products.AcFragment;
import com.believe.ramdevelectronics.ui.products.FridgeFragment;
import com.believe.ramdevelectronics.ui.products.TvFragment;
import com.believe.ramdevelectronics.ui.products.WashingMachineFragment;
import com.google.android.material.tabs.TabLayout;

public class ShopFragment extends Fragment {

    private ShopViewModel shopViewModel;

    private View root;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shopViewModel =
                ViewModelProviders.of(this).get(ShopViewModel.class);
        root = inflater.inflate(R.layout.fragment_shop, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.white));
        setHasOptionsMenu(true);

        init();
        setTabLayout();

        return root;
    }

    private void init() {
        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);
    }

    private void setTabLayout() {
        adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new TvFragment(), "Tv");
        adapter.addFragment(new FridgeFragment(), "Fridge");
        adapter.addFragment(new AcFragment(), "Ac");
        adapter.addFragment(new WashingMachineFragment(), "Washing Machine");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(adapter.getTabView(i,getActivity()));
        }

        highLightCurrentTab(0); // for initial selected tab view
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    highLightCurrentTab(position); // for tab change
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
        private void highLightCurrentTab(int position) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                assert tab != null;
                tab.setCustomView(null);
                tab.setCustomView(adapter.getTabView(i,getActivity()));
            }
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(adapter.getSelectedTabView(position,getActivity()));

    }
}