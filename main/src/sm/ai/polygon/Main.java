package sm.ai.polygon;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		PolygonDivision division = new PolygonDivision();
		Scanner read = new Scanner(System.in);
		
		System.out.println("Input file ?");
		String fileName = read.nextLine();
		DivisionConfiguration config;
		
		config = readConfig(fileName);
				
		division.configure(config);
		boolean result = division.play();
		
		if(result)
			printMatrix(division.matrix);
		else{
			System.err.println("Error !! No Such Conifguration Exists.");
		}		
	}

	private static void printMatrix(int[][] mat) {
		for(int i=0 ; i<mat.length ; ++i){
			for(int j=0 ; j<mat[i].length ; ++j){
				if(mat[i][j] == -1){	System.out.print("-, ");}
				else {	System.out.print(mat[i][j]+", ");	}
			}
			System.out.println();
		}
		System.out.println("------------------------------------------");
	
	}

	private static DivisionConfiguration readConfig(String fileName) {
		DivisionConfiguration config = new DivisionConfiguration();
		config.configureFromFile(fileName);
		
		return config;
		
	}
}
