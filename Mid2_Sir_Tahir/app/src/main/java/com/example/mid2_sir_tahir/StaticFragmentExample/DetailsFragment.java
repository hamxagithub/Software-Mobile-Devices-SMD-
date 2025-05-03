package com.example.mid2_sir_tahir.StaticFragmentExample;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mid2_sir_tahir.R;

public class DetailsFragment extends Fragment {
    public TextView mContactDetailView;
     int currentIndex = -1;
     int length;

    public int getCurrentIndex() {
        Log.d("TAG", getClass().getSimpleName() + ": entered getShownIndex()");
        return currentIndex;
    }

    public void ContactIndex(int index) {
        if (index < 0 || index >= length) {
            return;
        }
        currentIndex = index;
        mContactDetailView.setText(StaticFragmentMainActivity.contactsDetailArray[index]);
       // Log.d("TAG", getClass().getSimpleName() + ": entered showContactAtIndex()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContactDetailView = getActivity().findViewById(R.id.contactDetails);
        length=StaticFragmentMainActivity.contactsDetailArray.length;
    }
}
