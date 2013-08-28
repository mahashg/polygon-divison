package sm.ai.polygon;

import java.util.ArrayList;
import java.util.List;

class Cluster{
	
	List<Point> pointList;
	
	private int boundWeight;
	private int clusterId;
	
	public Cluster(int id) {
		this.clusterId = id;
		this.pointList = new ArrayList<Point>();
	}

	public boolean isSaturated() {
		return (pointList.size() >= boundWeight);
	}

	public int size() {
		return pointList.size();
	}

	public void setBoundWeight(int bw){
		this.boundWeight = bw;
	}

	public boolean add(Point pt){		
		if(isSaturated()){	return false;}
		return pointList.add(pt);
	}
	
	public Point remove(int index) {
		if(!isPopulated()){return null; }
		return pointList.remove(index);		
	}
	
	public boolean remove(Point currentPt) {
		if(!isPopulated()){return false; }
		return pointList.remove(currentPt);		
	}
	
	public boolean contains(Point pt){
		return pointList.contains(pt);
	}
	
	public boolean isPopulated(){
		return (pointList.size() != 0);
	}

	public int getClusterId(){	
		return this.clusterId;
	}	
}
