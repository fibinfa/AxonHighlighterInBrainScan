package edu.neu.cs6240.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class TransformMatrix {

	// We know matrix size
	private static int N = 21;

	// partitioning the array on basis of Z axis size
	private static int K = 7; 
	private static int EACH_PARTITION_SIZE = 441;
	private static int RECORD_SIZE = 3088;

	public static void main (String args []) {


		int ar[] = {4,8,7,2,6,5,3,1,0};
		int[] result = rotateArray90(ar);
		for (int i =0;i<result.length;i++) {
			System.out.print(result[i] + "  ");
		}
	}


	private static int[] rotateArray90(int ar[]) {
		int result[] = new int[441];
		int c = 0;
		for (int i = 0; i < ar.length; i++) {
			if (i!=0 && i%21 == 0) {
				c++;	
			}
			int newIndex = 21 * (i-21*c) + (21-c-1);
			result[newIndex]= ar[i];
			
		}
		return result;
	}
	
	
	private static int[] rotateArray180(int ar[]) {
		int result[] = new int[441];


		ArrayUtils.reverse(ar);
		
		
		return ar;
			
			
		}
	
	private static int[] rotateArray270(int ar[]) {
		int result[] = new int[441];


		int c = 0;
		for (int i = 0; i < ar.length; i++) {
			if (i!=0 && i%21 == 0) {
				c++;	
			}
			int newIndex = 441 - 21*(i-21*c+1) + c;
			result[newIndex]= ar[i];
			
		}

		return result;
	}
	
	
	public static List<String[]> transform(String ar[]) {

		String[] result = new String[RECORD_SIZE];


		for (int k = 0; k < K; k++) {

		}


		String[][] mat = new String[21][21];

		// feeding the array into the new matrix which will then e rotated
		for (int i = 0; i < ar.length; i++) {
			mat[i/K][i%K] = ar[i];
		}
		List<String[]> list = new ArrayList<>();

		list.add(rotateMatrixLeft(mat)); // 90
		list.add(rotateMatrixLeft(mat)); // 180
		list.add(rotateMatrixLeft(mat)); // 270


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