package tcc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import tcc.blindguide.R;
import tcc.uteis.ApplicationManager;


public class MenuFragment extends Fragment {


    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _view = inflater.inflate(R.layout.fragment_menu, container, false);

        ImageButton _btnNavigation = (ImageButton) _view.findViewById(R.id.menufragment_navigation);
        ImageButton _btnConfiguration = (ImageButton) _view.findViewById(R.id.menufragment_configuration);

        _btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationManager.Navigate(new NavigationFragment());
            }
        });

        _btnConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationManager.Navigate(new RotaFragment());
            }
        });


        return _view;
    }

}
