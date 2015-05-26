package tcc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tcc.blindguide.R;
import tcc.uteis.ApplicationManager;


/**
 * A fragment that launches other parts of the demo application.
 */
public class LaunchpadSectionFragment extends Fragment {

    private Fragment m_Fragment;

    public LaunchpadSectionFragment()
    {}

    public LaunchpadSectionFragment(Fragment fragment)
    {
        m_Fragment = fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.framelayout_rotas, container, false);

        ApplicationManager.Navigate(R.id.framelayout_rotas, m_Fragment);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}