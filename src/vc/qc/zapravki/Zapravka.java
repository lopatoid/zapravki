package vc.qc.zapravki;

import com.google.android.gms.maps.model.LatLng;

public class Zapravka {

	boolean isGood;
	String azs;
	String address;
	String compName;
	String checkDate;
	String fuelBrand;
	String oktNo;
	String sulfMgkg;
	String benzolVol;
//	String lubrMkm;
	LatLng latLng;

	public Zapravka(boolean isGood, String[] values) {
		this.isGood = isGood;
		azs = values[1];
		address = values[2];
		if (isGood) {
			compName = values[4];
			// isGood = "true".equals(values[5]);
			checkDate = values[6];
			latLng = new LatLng(Double.parseDouble(values[8]),Double.parseDouble(values[7]));
		} else {
			compName = values[3];
			fuelBrand = values[5];
			oktNo = values[6];
			sulfMgkg = values[7];
			benzolVol = values[8];
//			lubrMkm = values[9];
			checkDate = values[12];
			latLng = new LatLng(Double.parseDouble(values[14]),Double.parseDouble(values[13]));
		}
	}
}
