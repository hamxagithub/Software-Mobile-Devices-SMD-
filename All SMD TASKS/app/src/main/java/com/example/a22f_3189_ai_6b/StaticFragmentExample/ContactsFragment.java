package com.example.a22f_3189_ai_6b.StaticFragmentExample;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.a22f_3189_ai_6b.R;

public class ContactsFragment extends ListFragment {
    public ListSelectionListener mListener = null;
    public interface ListSelectionListener{
        public void onSelection(int position);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ListSelectionListener) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.contacttextview,StaticFragmentMainActivity.contactArray));
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        getListView().setItemChecked(position,true);
        mListener.onSelection(position);
    }
}
