package com.example.cardviewfilehandling_mam.FRAGMENT;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cardviewfilehandling_mam.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link firstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class firstFragment extends Fragment {
    EditText editText;
    Button sendButton;
    TextView receivedText;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public firstFragment() {
        // Required empty public constructor
    }


    public static firstFragment newInstance(String param1, String param2) {
        firstFragment fragment = new firstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        editText = view.findViewById(R.id.editText);
        sendButton = view.findViewById(R.id.sendButton);
        receivedText = view.findViewById(R.id.receivedText);

        sendButton.setOnClickListener(v -> sendDataToFragmentTwo());

        return view;
    }
    private void sendDataToFragmentTwo() {
        String data = editText.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("message", data);

        secondFragment fragmentTwo = new secondFragment();
        fragmentTwo.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragmentTwo).commit();
    }
}