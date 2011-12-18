package edu.ntu.mpp.keymap;

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
	public void draw(Canvas canvas,int size ,int num,JSONArray list){
		Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
    
        double angle = 360/num;
        int len;
        float offsetx = 0;
        float offsety = ((float)size)/2;
        
        try {
        
        if(r == 0){
        	int angle_offset = 0;
        	/*length of Ellipse*/
        	if(num == 1)
        		len = 0;
        	else
        		len = size;
        	/*plot words*/
        	for(int i=0; i < num ; i++){
        		setColor(paint,i);
				offsetx = -(size*list.get(i).toString().length())/2;
				
        		float x = (float) (M*len*Math.cos(Math.toRadians(angle_offset + i*angle)));
        		float y = (float) (len*Math.sin(Math.toRadians(angle_offset + i*angle)));
        		canvas.drawText(list.get(i).toString(), x+offsetx , y+offsety, paint);
        	}
        	
        	if(num == 1){
        		r += (size/2);
        		M = list.get(0).toString().length();
        		return;
        	}else if(num == 2){
        		r += (size/2 +rbound);
        		M = 2*size/(size/2+rbound)+Mbound;
        	}else{
        		r += (size+rbound);
        		M = (M*size+size)/(size+rbound);
        		return;
        	}

        }else{
        	len = r + size;
        	double Mn = countSize(r,M,size);
        	for(int i=0; i < num ; i++){
        		setColor(paint,i);
        		offsetx = -(size*list.get(i).toString().length())/2;
            	float x = (float) (Mn*len*Math.cos(Math.toRadians(i*angle)));
            	float y = (float) (len*Math.sin(Math.toRadians(i*angle)));
            	canvas.drawText(list.get(i).toString(), x+offsetx , y+offsety , paint);
        	}
        	r += size;
        	M = (Mn*size+size)/size;
        	return;
        }
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	double countSize(int r,double M,int size){
		double v = 0;
		double lenx = M*r;
		double leny = r;
		int N = 1;/*TODO: check max length in string*/
		v = (lenx+N*size)/(leny+size);
		return v;
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
