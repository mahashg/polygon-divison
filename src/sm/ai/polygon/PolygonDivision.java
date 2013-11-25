package sm.ai.polygon;

import java.util.ArrayList;
import java.util.List;

public class PolygonDivision {
	int width, height;	// dimension of board
	int[][] matrix;	// the board
	int division_size;	// number divisions to make
	List<Point> removalList;	// list of points to be removed
	private int seedCount;	// no of tiles to fill_up
	
	private boolean isConfigured;
	public PolygonDivision() {
		
	}
	
	public boolean configure(DivisionConfiguration config){
		isConfigured = config.isValid();
		if(config == null || !isConfigured){
			return false;
		}
		
		this.width = config.getWidth();
		this.height = config.getHeight();
		this.division_size = config.no_of_division;
		
		matrix = new int[this.width][this.height];		
		this.removalList = config.getRemovalList();
		
		return true;
	}

	private void initMatrix() {
		// 1. init all tiles and mark as 0
		for(int i=0 ; i<matrix.length ; ++i){
			for(int j=0 ; j<matrix[i].length ; ++j){
				matrix[i][j] = 0;
			}
		}
		
		// all removed tiles marked as -1
		for(Point pt : removalList){
			matrix[pt.x][pt.y] = -1;
		}

	}
	
	public boolean play(){
		if(!isConfigured){	return false; }
		seedCount = height*width-removalList.size();
		
		if(seedCount % division_size != 0){
			return false;
		}
		
		initMatrix();
		
		// list of points which were not knocked off
		List<Point> remainPts = new ArrayList<Point>();
		for(int i=0 ; i<matrix.length ; ++i){
			for(int j=0 ; j<matrix[i].length ; ++j){
				if(matrix[i][j] == 0){
					remainPts.add(new Point(i, j));
				}
			}
		}
		
		// init 6 clusters
		Cluster[] clust = new Cluster[division_size];
		int cluster_bound = getSeedCount()/division_size;	// max no of nodes that one cluster can contain
		for(int i=0 ; i<division_size ; ++i){
			clust[i] = new Cluster(i+1);	// set cluster id
			clust[i].setBoundWeight(cluster_bound);
		}
		
		//printMatrix(matrix);
		return playRecursively(remainPts, clust);
	}
	
	public boolean playRecursively(List<Point> remainPts, Cluster[] cluster_list){
		
		// if even two clusters are saturated and they are not congruent then no need to proceed further
		if(!isCongruenceList(cluster_list)){	return false;}

		if(remainPts.isEmpty()){
			//printMatrix(matrix);
			return true;
		}
		
		// remove first point and try to fit into other cluster
		Point currentPt = remainPts.remove(0);	// get Current Point
		List<Point> neighbourList = getFourNeighboursOf(currentPt);	// get all its neighbour
		
		for(Cluster current_cluster : cluster_list){
			// have to add the bound check
			if(current_cluster.isSaturated()){	continue;	}

			if(isAllowed(neighbourList, current_cluster)){
				
				// add pt to cluster
				matrix[currentPt.x][currentPt.y] = current_cluster.getClusterId();				
				current_cluster.add(currentPt);
				
				// check if is valid
				boolean isSuccess = playRecursively(remainPts, cluster_list);
				
				if(isSuccess){return true;}
				
				// if not valid then remove it
				matrix[currentPt.x][currentPt.y] = 0;				
				current_cluster.remove(currentPt);
			}
		}

		// add this point again to the cluster
		remainPts.add(0, currentPt);
		
		return false;
	}

	private boolean isAllowed(List<Point> neighbourList, Cluster divison) {

		int clusterId = divison.getClusterId();
		boolean isAllZero = true;
		boolean isAnyZero = false;
		
		// can i expand gpId th cluster here ?
		for(Point pt : neighbourList){
			if(matrix[pt.x][pt.y] == clusterId){ return true;	}
			if(matrix[pt.x][pt.y] != 0){ isAllZero = false;}
			if(matrix[pt.x][pt.y] == 0){ isAnyZero = true; }
		}
		
		// can i start populating gpId th cluster from this point ?
		if(isAnyZero && !divison.isPopulated()){	
			return true; 
		}
		
		return isAllZero;
	}

	private List<Point> getEightNeighboursOf(Point seedPoint){
		int x = seedPoint.x;
		int y = seedPoint.y;
		
		List<Point> neighbourList = new ArrayList<Point>();		
		
		if(x < (width-1) && matrix[x+1][y] > 0){
			neighbourList.add(new Point(x+1, y));
		}
		
		if(x > 0 && matrix[x-1][y] > 0){
			neighbourList.add(new Point(x-1, y));
		}
		
		if(y < (height-1) && matrix[x][y+1] > 0){
			neighbourList.add(new Point(x, y+1));
		}
		
		if(y > 0 && matrix[x][y-1] > 0){
			neighbourList.add(new Point(x, y-1));
		}
		
		if(x >0 && y > 0 && matrix[x-1][y-1] > 0){
			neighbourList.add(new Point(x-1, y-1));
		}
		
		if(x > 0 && y <(height-1) && matrix[x-1][y+1] > 0){
			neighbourList.add(new Point(x-1, y+1));
		}
		if(x < (width-1) && y > 0 && matrix[x+1][y-1] > 0){
			neighbourList.add(new Point(x+1, y-1));
		}
		if(x < (width-1) && y <(height-1) && matrix[x+1][y+1] > 0){
			neighbourList.add(new Point(x+1, y+1));
		}
		
		return neighbourList;
	}

	private List<Point> getFourNeighboursOf(Point seedPoint) {
		int x = seedPoint.x;
		int y = seedPoint.y;
		
		List<Point> neighbourList = new ArrayList<Point>();		
		
		if(x < (width-1) && matrix[x+1][y] >= 0){
			neighbourList.add(new Point(x+1, y));
		}
		
		if(x > 0 && matrix[x-1][y] >= 0){
			neighbourList.add(new Point(x-1, y));
		}
		
		if(y < (height-1) && matrix[x][y+1] >= 0){
			neighbourList.add(new Point(x, y+1));
		}
		
		if(y > 0 && matrix[x][y-1] >= 0){
			neighbourList.add(new Point(x, y-1));
		}
		
		return neighbourList;
	}

	public int getSeedCount() {
		return seedCount;
	}
	
	private boolean isCongruenceList(Cluster[] clust) {

		List<int[][]> featureList = new ArrayList<int[][]>();
		
		for(int i=0 ; i<clust.length ; ++i){
			if(clust[i].isSaturated()){
				int[][] featureMat = computeFeatureOf(clust[i]);
				
				featureList.add(featureMat);
			}		
		}
		
		while(featureList.size() > 1){
			int[][] featureMat1 = featureList.get(0);
			int[][] featureMat2 = featureList.get(1);
			
			List<Integer> feature1 = new ArrayList<Integer>();
			feature1.add(featureMat1[0][0]);
			feature1.add(featureMat1[0][1]);
			feature1.add(featureMat1[0][2]);
			feature1.add(featureMat1[1][0]);
			
			List<Integer> feature2 = new ArrayList<Integer>();
			feature2.add(featureMat2[0][0]);
			feature2.add(featureMat2[0][1]);
			feature2.add(featureMat2[0][2]);
			feature2.add(featureMat2[1][0]);
			
			if(sequenceMatches(feature1, feature2)){				
				featureList.remove(0);	// no need to compare this as its replica exists
			}else {
				return false;
			}
		
		}		
		return true;
		
	}
	
	private int[][] computeFeatureOf(Cluster gd){
		int[][] featureMatrix = new int[3][3];
		
		for(Point pt : gd.pointList){
			
			List<Point> ptList = getEightNeighboursOf(pt);
			
			for(Point neigh : ptList){
				if(matrix[neigh.x][neigh.y] == gd.getClusterId()){
					featureMatrix[pt.x-neigh.x+1][pt.y-neigh.y+1]++;					
				}
			}
		}
		
		return featureMatrix;		
	}

	private boolean sequenceMatches(List<Integer> feature1,
			List<Integer> feature2) {
		
		String fs1 = "", fs2="", fs1Rev="";
		for(int i=0 ; i<feature1.size() ; ++i){
			fs1 += feature1.get(i)+", ";
			fs2 += feature2.get(i)+", ";
			fs1Rev += feature1.get(feature1.size()-1-i)+", ";
		}
		
		
		fs1 = fs1 + fs1;
		fs1Rev = fs1Rev + fs1Rev;
			
		return (fs1.contains(fs2) || fs1Rev.contains(fs2));
	}
}