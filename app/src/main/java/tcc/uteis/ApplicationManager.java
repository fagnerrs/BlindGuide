package tcc.uteis;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Stack;

import tcc.blindguide.R;

/**
 * Created by FAGNER on 28/03/2015.
 */
public class ApplicationManager {

    private static FragmentManager m_FragmentManager = null;
    private static Stack<Fragment> _pilhaDeNavegacao = null;

    public static void Navigate(int frameLayout, Fragment fragment)
    {
        FragmentTransaction ft = m_FragmentManager.beginTransaction();
        ft.replace(frameLayout, fragment);
        ft.commit();

        _pilhaDeNavegacao.push(fragment);
    }

    public static void Navigate(Fragment fragment)
    {
        FragmentTransaction ft = m_FragmentManager.beginTransaction();
        ft.replace(R.id.mainactivity_framelayout, fragment);
        ft.commit();

        _pilhaDeNavegacao.push(fragment);
    }

    public static boolean Back(int frameLayout)
    {
        Fragment _frag = _pilhaDeNavegacao.peek();

        if (!_frag.getClass().getName().equals("tcc.fragments.MenuFragment"))
        {
            _pilhaDeNavegacao.pop();
            Fragment  _frg = _pilhaDeNavegacao.pop();
            Navigate(frameLayout, _frg);

            return false;
        }
        else
        {
            return true;
        }
    }

    public static boolean Back()
    {
        Fragment _frag = _pilhaDeNavegacao.peek();

        if (!_frag.getClass().getName().equals("tcc.fragments.MenuFragment"))
        {
            _pilhaDeNavegacao.pop();
            Fragment _frg = _pilhaDeNavegacao.pop();
            Navigate(_frg);

            return false;
        }
        else
        {
            return true;
        }
    }

    public static void Initialize(FragmentManager fragmentManager) {
        m_FragmentManager = fragmentManager;
        _pilhaDeNavegacao = new Stack<Fragment>();
    }


}
