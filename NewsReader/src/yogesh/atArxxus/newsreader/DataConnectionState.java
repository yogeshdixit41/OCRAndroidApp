package yogesh.atArxxus.newsreader;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

public class DataConnectionState extends PhoneStateListener{

	boolean isDataConnected = false;

	@Override
	public void onDataConnectionStateChanged(int state) {
		// TODO Auto-generated method stub
		super.onDataConnectionStateChanged(state);
		
		switch(state)
		{
		
			case TelephonyManager.DATA_DISCONNECTED:
				isDataConnected = false;
				break;
				
			case TelephonyManager.DATA_CONNECTING:
				isDataConnected = false;
				break;
				
			case TelephonyManager.DATA_CONNECTED:
				isDataConnected = true;
				break;
				
			default:
				isDataConnected = false;
				break;
		}
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		// TODO Auto-generated method stub
		super.onSignalStrengthsChanged(signalStrength);
	}
	
	

}
