package com.starstats.starstats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class WidgetInstructionFragment extends Fragment {

    public WidgetInstructionFragment() {
        // Required empty public constructor
    }

    public static WidgetInstructionFragment newInstance() {
        WidgetInstructionFragment fragment = new WidgetInstructionFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_widget_instruction, container, false);

        LinearLayout l = v.findViewById(R.id .instBack);
        l.setOnClickListener(view -> {
            requireActivity().onBackPressed();
        });
        return v;
    }
}