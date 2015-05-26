package tcc.blindguide;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import tcc.fragments.MenuFragment;
import tcc.fragments.NavigationFragment;
import tcc.uteis.ApplicationManager;


public class MainActivity extends FragmentActivity {

    private TextView m_TvDegrees;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //m_TvDegrees = (TextView)this.findViewById(R.id.tv_degrees);

        ApplicationManager.Initialize( this.getSupportFragmentManager());
        ApplicationManager.Navigate(new MenuFragment());
    }

   /* @Override
    public void onSensorChanged(SensorEvent event) {
        super.onSensorChanged(event);

        updateText(GlobalData.getBearing());
    }*/

    @Override
    public void onBackPressed() {
        if (ApplicationManager.Back())
        {
            super.onBackPressed();
        }
    }

    private void updateText(float bearing) {
        int range = (int) (bearing / (360f / 16f));
        String dirTxt = "";
        if (range == 15 || range == 0) dirTxt = "N";
        else if (range == 1 || range == 2) dirTxt = "NE";
        else if (range == 3 || range == 4) dirTxt = "E";
        else if (range == 5 || range == 6) dirTxt = "SE";
        else if (range == 7 || range == 8) dirTxt = "S";
        else if (range == 9 || range == 10) dirTxt = "SW";
        else if (range == 11 || range == 12) dirTxt = "W";
        else if (range == 13 || range == 14) dirTxt = "NW";
       // m_TvDegrees.setText("" + ((int) bearing) + ((char) 176) + " " + dirTxt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
