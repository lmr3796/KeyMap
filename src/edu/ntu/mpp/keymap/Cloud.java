package edu.ntu.mpp.keymap;

import java.util.ArrayList;

public class Cloud {
	private static final int[] sizeConstraint = {4, 8, 16};
	private double lat = 0;
	private double lng = 0;
	private ArrayList<ArrayList<String>> keyWords = new ArrayList<ArrayList<String>>();
	public Cloud(){
		for(int i = 0 ; i < 3 ; i++)
			keyWords.add(new ArrayList<String>());
	}
	public Cloud(double a, double n, ArrayList<ArrayList<String>> k){
		lat = a;
		lng = n;
		setKeyWords(k);
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public ArrayList<ArrayList<String>> getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(ArrayList<ArrayList<String>> keyWords) {
		this.keyWords = keyWords;
	}
	public void addKeyWord(String s){
		for(int i = 0 ; i < keyWords.size() ; i++)
			if(keyWords.get(i).size() < sizeConstraint[i])
				keyWords.get(i).add(s);
	}
}
