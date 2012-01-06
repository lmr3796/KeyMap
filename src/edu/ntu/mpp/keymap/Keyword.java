package edu.ntu.mpp.keymap;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Keyword {
	private int r;
	private double M;
	private int rbound;
	private double Mbound;
	Keyword(){
		r  = 0;
		M = 1;
		rbound = 10;
		Mbound = 0.3;
	}
	public void draw(Canvas canvas,int size ,int num,ArrayList<String> list){
		Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
    
        double angle = 360/num;
        int len;
        float offsetx = 0;
        float offsety = ((float)size)/2;
        float offsetr = ((float)size)*findmax(list);
        
       
        
        if(r == 0){
        	int angle_offset = 0;
        	/*length of Ellipse*/
        	if(num == 1)
        		len = 0;
        	else
        		len = size;
        	
        	if(num == 3)
        		angle = 90;
        	
        	/*plot words*/
        	for(int i=0; i < num ; i++){
        		String str = list.get(i).toString();
        		setColor(paint,i);
				offsetx = -(size*str.length()*checkchar(str))/2;
				float Mn = ((float)str.length()*checkchar(str))/2;
				
        		float x = (float) (Mn*len*Math.cos(Math.toRadians(angle_offset + i*angle)));
        		float y = (float) (len*Math.sin(Math.toRadians(angle_offset + i*angle)));
        		canvas.drawText(list.get(i).toString(), x+offsetx , y+offsety, paint);
        	
        	}
        	
        	if(num == 1){
        		r += (size/2);
        		M = list.get(0).toString().length();
        		return;
        	}else{
        		r += (size+rbound);
        		M = (offsetr)/(size+rbound);
        		return;
        	}

        }else{
        	len = r + size;
        	double Mn = M;
        	for(int i=0; i < num ; i++){
        		String str = list.get(i).toString();
        		setColor(paint,i);
        		offsetx = (float) (-(size*str.length())/2 * checkchar(str));
        		Mn = (M*r + ((float)(size*str.length())*checkchar(str))/2)/len;
        		
            	float x = (float) (Mn*len*Math.cos(Math.toRadians(i*angle)));
            	float y = (float) (len*Math.sin(Math.toRadians(i*angle)));
            	canvas.drawText(str, x+offsetx , y+offsety , paint);
        	}
        	r += size;
        	M = (Mn*size+size)/size;
        	return;
        }
	}
	float findmax(ArrayList<String> list){
		float max = 0;	
			for(int i = 0 ;i < list.size() ; i++){
				String str = list.get(i).toString();
				if(max <= str.length()*checkchar(str))
					max = str.length()*checkchar(str);
			}
		return max;
	}
	/*
	double countSize(int r,double M,int size,JSONArray list){
		double v = 0;
		double lenx = M*r;
		double leny = r;
		float N = findmax(list);
		v = (lenx+N*size)/(leny+size);
		return v;
	}*/
	float checkchar(String str){
		float check = 1;
		Character.UnicodeBlock block = Character.UnicodeBlock.of(str.charAt(0));
		if(!Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block)
		&& !Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(block)
		&& !Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(block)) 
			check /=2 ;
		
		return check;
		
	}
	void setColor(Paint paint,int a){
		a = a % 5;
		switch(a){
			case 0:
				paint.setColor(Color.BLUE);
				break;
			case 1:
				paint.setColor(Color.RED);
				break;
			case 2:
				paint.setColor(Color.rgb(96, 56, 17));
				break;
			case 3:
				paint.setColor(Color.rgb(0, 150, 50));
				break;
			case 4:
				paint.setColor(Color.MAGENTA);
				break;
			
		}
		return;
	}
}
