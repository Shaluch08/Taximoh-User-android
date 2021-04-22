package com.uveous.loopfoonpay.Welcome;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uveous.loopfoonpay.R;

public class BlankFragment extends Fragment {

    private static final String ARG_POSITION = "slider-position";
    // prepare all title ids arrays

    private static final String[] PAGE_TITLES =
            new String[] {"Registration","Search Taxi", "Hire Car","Enjoy Trip"};
    // prepare all subtitle ids arrays
  /*  @StringRes
    private static final int[] PAGE_TEXT =
            new int[] {
                    R.string.discover_text, R.string.shop_text, R.string.offers_text, R.string.rewards_text
            };
    // prepare all subtitle images arrays*/
    @StringRes
    private static final int[] PAGE_IMAGE =
            new int[] {
                    R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d
            };

    private int position;


    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(int position) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.slider, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set page background
        TextView title = view.findViewById(R.id.text);

       ImageView imageView = view.findViewById(R.id.image);
        // set page title
        title.setText(PAGE_TITLES[position]);

        imageView.setImageResource(PAGE_IMAGE[position]);
    }
}