//Copyright M.M.Kuttel 2024 CSC2002S, UCT
package serialAbelianSandpile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;

import javax.imageio.ImageIO;

//This class is for the grid for the Abelian Sandpile cellular automaton
public class Grid extends RecursiveTask<Integer> {
	private int rows, columns;
	private int [][] grid; //grid 
	private int [][] updateGrid;//grid for next time step

	int hi, lo;
	public static int SEQUENTIAL_CUTOFF=1000;
	public static boolean change=false;
    
	public Grid(int w, int h) {
		rows = w+2; //for the "sink" border
		columns = h+2; //for the "sink" border
		grid = new int[this.rows][this.columns];
		updateGrid=new int[this.rows][this.columns];
		/* grid  initialization */
		for(int i=0; i<this.rows; i++ ) {
			for( int j=0; j<this.columns; j++ ) {
				grid[i][j]=0;
				updateGrid[i][j]=0;
			}
		}

		hi = (grid.length-2) * (grid.length-2);
		lo = 0;

	}

	public Grid(int[][] newGrid) {
		this(newGrid.length,newGrid[0].length); //call constructor above
		//don't copy over sink border
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=newGrid[i-1][j-1];
			}
		}
		
	}
	public Grid(Grid copyGrid) {
		this(copyGrid.rows,copyGrid.columns); //call constructor above
		/* grid  initialization */
		for(int i=0; i<rows; i++ ) {
			for( int j=0; j<columns; j++ ) {
				System.out.println(i +" "+j+" "+columns+" "+rows);
				this.grid[i][j]=copyGrid.get(i,j);
			}
		}
	}

	public Grid(int[][] newGrid, int lo, int hi) {
		//this(grid.length-2,grid[0].length-2);
		this(newGrid.length-2,newGrid[0].length-2); //call constructor above
		//don't copy over sink border
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=newGrid[i-1][j-1];
			}
		}
		this.hi = hi;
		this.lo = lo;

	}
	
	public int getRows() {
		return rows-2; //less the sink
	}

	public int getColumns() {
		return columns-2;//less the sink
	}


	int get(int i, int j) {
		return this.grid[i][j];
	}

	void setAll(int value) {
		//borders are always 0
		for( int i = 1; i<rows-1; i++ ) {
			for( int j = 1; j<columns-1; j++ ) 			
				grid[i][j]=value;
			}
	}

	
	static class Coord {
		public int x, y;
		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public static Coord IndexToCoord(int index, int[][] g) 
		{
	
			int x = (index % (g.length-2)) + 1;
			int y = (index / (g.length-2)) + 1;
			
			Coord coord = new Coord(x, y);
	
			return coord;
	
		}
		public static int CoordToIndex(Coord c, int[][] g)
		{

			int index = 0;
			index = (c.y-1) * (g.length-2);
			index += (c.x-1);

			return index;
		}

		public String toString() {
			return x + " " + y;
		}
	}

	//for the next timestep - copy updateGrid into grid
	public void nextTimeStep() {
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=updateGrid[i][j];
			}
		}
	}


	protected Integer compute()  // we still need to call nextTimeStep() after doing whole grid
	{
		
		int count = lo;
		if(hi-lo < 20){
			
			while (count < hi) {

				Coord loC = Grid.Coord.IndexToCoord(count, updateGrid);

				//if(loC.x+1 > grid.length-1) {System.out.println(loC.x);}
				//System.out.println(get(33,33));

				if(updateGrid[loC.x][loC.y]>3) {System.out.println(loC.x + " "+loC.y+" "+count + "SADSD");}
				//if(updateGrid[loC.x][loC.y]<3) {System.out.println(loC.x + " "+loC.y+" "+count);}
				//else{System.out.println(updateGrid[loC.x][loC.y]);}
				updateGrid[loC.x][loC.y] = (grid[loC.x][loC.y] % 4) + 
						(grid[loC.x-1][loC.y] / 4) +
						grid[loC.x+1][loC.y] / 4 +
						grid[loC.x][loC.y-1] / 4 + 
						grid[loC.x][loC.y+1] / 4;
				if (grid[loC.x][loC.y]!=updateGrid[loC.x][loC.y]) {  
					change=true;
				}

				count++;

			}

			return 0;

		} 
		else {
			
			Grid left = new Grid(grid, lo, (hi+lo)/2);
			Grid right = new Grid(grid, (lo + hi)/2, hi);

			left.fork();
			right.compute();
			left.join();
			nextTimeStep();
			return 0;

		}

	}




	
	//key method to calculate the next update grod
	boolean update() {
		boolean change=false;
		//do not update border
		for( int i = 1; i<rows-1; i++ ) {
			for( int j = 1; j<columns-1; j++ ) {
				updateGrid[i][j] = (grid[i][j] % 4) + 
						(grid[i-1][j] / 4) +
						grid[i+1][j] / 4 +
						grid[i][j-1] / 4 + 
						grid[i][j+1] / 4;
				if (grid[i][j]!=updateGrid[i][j]) {  
					change=true;
				}
		}} //end nested for
	if (change) { nextTimeStep();}
	return change;
	}
	
	
	
	//display the grid in text format
	void printGrid( ) {
		int i,j;
		//not border is not printed
		System.out.printf("Grid:\n");
		System.out.printf("+");
		for( j=1; j<columns-1; j++ ) System.out.printf("  --");
		System.out.printf("+\n");
		for( i=1; i<rows-1; i++ ) {
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
                new BufferedImage(rows, columns, BufferedImage.TYPE_INT_ARGB);
        //integer values from 0 to 255.
        int a=0;
        int g=0;//green
        int b=0;//blue
        int r=0;//red

		for( int i=0; i<rows; i++ ) {
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
