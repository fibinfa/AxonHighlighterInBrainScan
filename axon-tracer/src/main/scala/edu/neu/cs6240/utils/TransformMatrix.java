package edu.neu.cs6240.utils;

import java.util.ArrayList;
import java.util.List;

public class TransformMatrix {

	// We know matrix size
	private static int N = 21;

	// partitioning the array on basis of Z axis size
//	private static int K = 7; 
	
	public static List<String[]> transform(String ar[]) {
			
				String[][] mat = new String[21][21];
				String lastElement = ar[ar.length-1];
				// feeding the array into the new matrix which will then e rotated
				for (int i = 0; i < ar.length; i++) {
					mat[i/N][i%N] = ar[i];
				}
				List<String[]> list = new ArrayList<>();
				String[] r90 = rotateMatrixLeft(mat);
				r90[r90.length-1] =lastElement;
				
				
				
				String[] r180 = rotateMatrixLeft(mat);
				r180[r180.length-1] =lastElement;
				String[] r270 = rotateMatrixLeft(mat);
				r270[r270.length-1] =lastElement;
				list.add(r90); // 90
				list.add(r180); // 180
				list.add(r270); // 270
				
				
				return list;
	}
	
	

	// Anticlockwise rotation
	public static String[] rotateMatrixLeft(String[][] mat){

		


		// Consider all squares one by one
		for (int x = 0; x < N / 2; x++)
		{
			// Consider elements in group of 4 in 
			// current square
			for (int y = x; y < N-x-1; y++)
			{
				// store current cell in temp variable
				String temp = mat[x][y];

				// move values from right to top
				mat[x][y] = mat[y][N-1-x];

				// move values from bottom to right
				mat[y][N-1-x] = mat[N-1-x][N-1-y];

				// move values from left to bottom
				mat[N-1-x][N-1-y] = mat[N-1-y][x];

				// assign temp to left
				mat[N-1-y][x] = temp;
			}
		}

		// Converting the transformed matrix back into a resultant array and return the resultant Array 
		String result[] = new String[21*21*7];
		int c = 0;
		for (int i = 0;i < N; i++) {
			for (int j = 0;j<N;j++) {
				result[c++] = mat[i][j];
			}
		}
		return result;
	}

}