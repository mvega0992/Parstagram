package me.mvega.parstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private OnItemSelectedListener listener;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        Button btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfileFragment", "Log out button clicked!");
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ProfileFragment", "Log out done!");
                        listener.onLogOutSelected();
                    }
                });
            }
        });
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragmentProfile = new ProfileFragment();
        fragmentProfile.setArguments(new Bundle());
        return fragmentProfile;
    }

    public interface OnItemSelectedListener {
        void onLogOutSelected();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ProfileFragment.OnItemSelectedListener");
        }
    }
}
