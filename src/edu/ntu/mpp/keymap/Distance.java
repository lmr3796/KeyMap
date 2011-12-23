package edu.ntu.mpp.keymap;

public class Distance {

	static double dist(double lat1, double lon1, double lat2, double lon2) {

		double theta = lon1 - lon2;

		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		double miles = dist * 60 * 1.609344 * 1000;
		return miles;
	}
	static double deg2rad(double degree) {
		return degree / 180 * Math.PI;
	}
	static double rad2deg(double radian) {
		return radian * 180 / Math.PI;
	}
}
