package yogesh.atArxxus.newsreader;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class NewsPaperListDialog extends DialogFragment{

	String[] newsPaperOptions = {"Times Of India", "Hindustan Times"};
	boolean[] newsPaperSelectionBoolean = new boolean[newsPaperOptions.length];
	ArrayList<String> selectedNewsPaper = new ArrayList<String>();
	
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		
		dialogBuilder.setTitle("Select News Paper").
					  setMultiChoiceItems(newsPaperOptions, newsPaperSelectionBoolean, selectionListener).
				      setPositiveButton("OK", newsPaperSelected);
	
		return dialogBuilder.create();
	}
	

	
	
	OnMultiChoiceClickListener selectionListener = new OnMultiChoiceClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	OnClickListener newsPaperSelected  = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int whichButtonPressed) {
			
			switch(whichButtonPressed)
			{
			case Dialog.BUTTON_POSITIVE:
										selectedNewsPaper.clear();
										for(int i=0;i< newsPaperOptions.length; i++)
										{
											if(newsPaperSelectionBoolean[i])
											{
												selectedNewsPaper.add(newsPaperOptions[i]);
											}
										}
										break; 
			}
		}
	};
	
	public ArrayList<String> getSelectedNewsPaper()
	{
		for(String newsPaper:selectedNewsPaper)
		{
			Log.e("newsPaper found:", newsPaper);
		}
		return selectedNewsPaper;
	}
	
}
