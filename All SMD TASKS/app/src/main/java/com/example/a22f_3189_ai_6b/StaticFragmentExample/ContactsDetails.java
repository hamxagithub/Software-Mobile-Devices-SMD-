package com.example.a22f_3189_ai_6b.StaticFragmentExample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a22f_3189_ai_6b.R;

public class ContactsDetails extends Fragment {
    private int currentIndex = -1;
    private int arrayLength = 0;
    private TextView contactDetailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contactsdetail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactDetailTextView = view.findViewById(R.id.txtcontactdetail);

        // Ensure array length is initialized
        if (StaticFragmentMainActivity.contactArray != null) {
            arrayLength = StaticFragmentMainActivity.contactArray.length;
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void contactIndex(int index) {
        if (index < 0 || index >= arrayLength) {
            return;
        }

        currentIndex = index;
        if (contactDetailTextView != null) {
            contactDetailTextView.setText(StaticFragmentMainActivity.contactsDetailArray[currentIndex]);
        }
    }
}
