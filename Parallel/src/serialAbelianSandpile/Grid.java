//Copyright M.M.Kuttel 2024 CSC2002S, UCT
package serialAbelianSandpile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;

import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;


//This class is for the grid for the Abelian Sandpile cellular automaton
public class Grid {
	private int row, columns;
	private int [][] grid; //grid 
	public int[][] updateGrid;//grid for next time step

	//added
	static final ForkJoinPool fjPool = ForkJoinPool.commonPool();
	static int CUTOFF = 88;
	//added 
    
	public Grid(int w, int h) {
		row = w+2; //for the "sink" border
		columns = h+2; //for the "sink" border
		grid = new int[this.row][this.columns];
		updateGrid=new int[this.row][this.columns];
		/* grid  initialization */
		for(int i=0; i<this.row; i++ ) {
			for( int j=0; j<this.columns; j++ ) {
				grid[i][j]=0;
				updateGrid[i][j]=0;
			}
		}

	}

	public Grid(int[][] newGrid) {
		this(newGrid.length,newGrid[0].length); //call constructor above
		//don't copy over sink border
		for(int i=1; i<row-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=newGrid[i-1][j-1];
			}
		}
		
	}
	public Grid(Grid copyGrid) {
		this(copyGrid.row,copyGrid.columns); //call constructor above
		/* grid  initialization */
		for(int i=0; i<row; i++ ) {
			for( int j=0; j<columns; j++ ) {
				this.grid[i][j]=copyGrid.get(i,j);
			}
		}
	}

	public Grid(Grid cGrid, int hi, int lo) {
		this(cGrid);

	}
	
	public int getrow() {
		return row-2; //less the sink
	}

	public int getColumns() {
		return columns-2;//less the sink
	}


	int get(int i, int j) {
		return this.grid[i][j];
	}

	void setAll(int value) {
		//borders are always 0
		for( int i = 1; i<row-1; i++ ) {
			for( int j = 1; j<columns-1; j++ ) 			
				grid[i][j]=value;
			}
	}

	//for the next timestep - copy updateGrid into grid
	public void nextTimeStep() {
		for(int i=1; i<row-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=updateGrid[i][j];
			}
		}
	}

	
	
	
	boolean abelian() {
		return fjPool.invoke( new ParallelGrid( grid ) );
	}




	
	public class ParallelGrid extends RecursiveTask<Boolean>  
	{

		int[][] grod;
		int[][] uGrod;
		int lo, hi;
		boolean main;

		public ParallelGrid(int[][] grid){
			this.grod = grid;
			this.uGrod = grid;
			this.lo = 1;
			this.hi = grid.length-1;
		}
		public ParallelGrid(int[][] grid, int[][] uGrid, int lo, int hi) {
			this.grod = grid;
			this.uGrod = uGrid;
			this.lo = lo;
			this.hi = hi;
		}

		protected Boolean compute()
		{
			boolean change=false;
			if(hi-lo <= CUTOFF) {
				for(int i=lo; i<hi; i++){
					for (int j = 1; j < grid.length-1; j++) {
						updateGrid[j][i] = (grid[j][i] % 4) + 
								(grid[j-1][i] / 4) +
								grid[j+1][i] / 4 +
								grid[j][i-1] / 4 + 
								grid[j][i+1] / 4;
						if (grid[j][i]!=updateGrid[j][i]) {  
							change=true;
						}
					}
				}	
				return change;
				
			}
			else{
				
				ParallelGrid left = new ParallelGrid(grod, uGrod, lo, (lo+hi)/2);
				ParallelGrid right = new ParallelGrid(grod, uGrod, (lo+hi)/2, hi);

				left.fork();
				boolean rightAns = right.compute();
				boolean leftAns = left.join();
				return (rightAns || leftAns);
			}

		}

	}
	
	
	
	//display the grid in text format
	void printGrid( ) {
		int i,j;
		//not border is not printed
		System.out.printf("Grid:\n");
		System.out.printf("+");
		for( j=1; j<columns-1; j++ ) System.out.printf("  --");
		System.out.printf("+\n");
		for( i=1; i<row-1; i++ ) {
			System.out.printf("|");
			for( j=1; j<columns-1; j++ ) {
				if ( grid[i][j] > 0) 
					System.out.printf("%4d", grid[i][j] );
				else
					System.out.printf("    ");
			}
			System.out.printf("|\n");
		}
		System.out.printf("+");
		for( j=1; j<columns-1; j++ ) System.out.printf("  --");
		System.out.printf("+\n\n");
	}
	
	//write grid out as an image
	void gridToImage(String fileName) throws IOException {
        BufferedImage dstImage =
                new BufferedImage(row, columns, BufferedImage.TYPE_INT_ARGB);
        //integer values from 0 to 255.
        int a=0;
        int g=0;//green
        int b=0;//blue
        int r=0;//red

		for( int i=0; i<row; i++ ) {
			for( int j=0; j<columns; j++ ) {
			     g=0;//green
			     b=0;//blue
			     r=0;//red

				switch (grid[i][j]) {
					case 0:
		                break;
		            case 1:
		            	g=255;
		                break;
		            case 2:
		                b=255;
		                break;
		            case 3:
		                r = 255;
		                break;
		            default:
		                break;
				
				}
		                // Set destination pixel to mean
		                // Re-assemble destination pixel.
		              int dpixel = (0xff000000)
		                		| (a << 24)
		                        | (r << 16)
		                        | (g<< 8)
		                        | b; 
		              dstImage.setRGB(i, j, dpixel); //write it out

			
			}}
		
        File dstFile = new File(fileName);
        ImageIO.write(dstImage, "png", dstFile);
	}
	
	


}
