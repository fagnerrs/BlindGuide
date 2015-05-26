package tcc.uteis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by FAGNER on 09/02/2015.
 */
public class BGMensagem {

    public static void ShowWarning(Context context, String message)
    {
        ShowWarning(context, message, null);
    }

    public static void ShowWarning(Context context, String message, DialogInterface.OnClickListener clickListener ) {

        AlertDialog _dialog;
        AlertDialog.Builder _builder = new AlertDialog.Builder(context);
        _builder.setMessage(message);
        _builder.setPositiveButton("Ok", clickListener);

        _dialog = _builder.create();
        _dialog.show();
    }
}
