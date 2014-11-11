package mn.aug.restfulandroid.service;

import android.os.Parcelable;

public interface ProcessorCallback {
	
	void send(int resultCode);
    void send(int resultCode, Parcelable resource);

}
