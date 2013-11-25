package sm.ai.polygon;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DivisionConfiguration {
	int width;
	int height;
	int no_of_division;

	List<Point> removalList;
	
	public DivisionConfiguration() {
		this.removalList = new ArrayList<Point>();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getNo_of_division() {
		return no_of_division;
	}

	public void setNo_of_division(int no_of_division) {
		this.no_of_division = no_of_division;
	}
	
	public List<Point> getRemovalList() {
		return removalList;
	}

	public void setRemovalList(List<Point> removalList) {
		this.removalList = removalList;
	}
	
	public boolean isValid(){
		// 1. do bound check on pts value
		if(this.width <= 0 || this.height <= 0 || this.no_of_division <= 0 ){
			return false;
		}
		
		// 2. do bound check on points to be removed.
		if(this.removalList != null){
			for(Point pt : removalList){
				if(pt.getX() < 0 || pt.getX() >= width ||
						pt.getY() < 0 || pt.getY() > height){
					return false;
				}
			}
			
		}
		
		return true;
	}

	public void configureFromFile(String fileName) {
		Properties props = new Properties();
		
		try {
			props.load(new FileInputStream(fileName));
		}catch (IOException e) {
		
			System.err.println("Error !! "+fileName+" not found.");
			return;
		}
		
		try{
			this.width = Integer.parseInt(props.getProperty("width"));
			this.height = Integer.parseInt(props.getProperty("height"));
			this.no_of_division = Integer.parseInt(props.getProperty("no_of_division"));
			int listSize = Integer.parseInt(props.getProperty("listSize"));
			
			this.removalList = new ArrayList<Point>(listSize);
			
			for(int i=1 ; i<= listSize ; ++i){
				int x = Integer.parseInt(props.getProperty("removal_x"+i));
				int y = Integer.parseInt(props.getProperty("removal_y"+i));
				
				this.removalList.add(new Point(x, y));
			}
		}catch(Exception e){
			System.err.println("Error "+e.getMessage()+" occured during execution.");
			System.err.println("Error during reading properties file.");
		}
	}
}
