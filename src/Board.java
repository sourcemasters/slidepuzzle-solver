/***************************************
 * Michael W. 2018 August 10
 * This class implements a board for an sliding puzzle
 * of arbitrary dimension n
 **************************************/
import edu.princeton.cs.algs4.In;
import java.util.Iterator;

public class Board
{
	final private int dimension; // dimension n of the n-by-n board
	final private int[][] boardgrid; // the positions on the board
	private int zx, zy; // coords of the empty slot
	private int manhattand; // manhattan distance of the board
	private int hamming; //number of blocks out of place
	
	private class BoardStack implements Iterable<Board>
	{
		private Node first;
		private int number; //size
		
		private class Node
		{
			private Board value;
			private Node next;
		}
		
		private class BoardIterator implements Iterator<Board>
		{
			Node current;
			public BoardIterator(Node first)
			{
				current = first;
			}
			
			public boolean hasNext()
			{
				return current != null;
			}
			
			public Board next()
			{
				if (!hasNext()) throw new java.util.NoSuchElementException("Frickin' Iterator's underflowed");
				
				Board b = current.value;
				current = current.next;
				return b;
			}
			
			public void remove()
			{
				throw new java.lang.UnsupportedOperationException("Don't try to use remove() y'all");
			}
		}
		
		public BoardStack()
		{
			number = 0;
			first = null;
		}
		
		public void push(Board b)
		{
			Node oldfirst = first;
			first = new Node();
			first.value = b;
			first.next = oldfirst;
			++number;
		}
		
		public Board pop()
		{
			if (isEmpty()) throw new java.util.NoSuchElementException("Stack Underflow");
			Board oldfirst = first.value;
			first = first.next;
			--number;
			return oldfirst;
		}
		
		public boolean isEmpty()
		{
			return first == null;
		}
		
		public int size()
		{
			return number;
		}
		
		public Iterator<Board> iterator()
		{
			return new BoardIterator(first);
		}
		
		
	}
	
	public Board(int[][] blocks) // initialized n-by-n board, assumes blocks is n-by-n where 2 <= n <= 128
	{
		dimension = blocks.length;
		hamming = manhattand = 0;
		boardgrid = new int[dimension][dimension];
		
		
		for (int i = 0; i < dimension; ++i)
			for (int j = 0; j < dimension; ++j)
			{
				int i1,j1; // the correct coordinates for the present index				
				boardgrid[i][j] = blocks[i][j];
				if (boardgrid[i][j] != (i * dimension + j + 1) && (boardgrid[i][j] != 0)) hamming++; // if the position's not right increment hamming
				
				if (boardgrid[i][j] == 0)
				{
					zx = i;
					zy = j;
				} else
				{	
					i1 = boardgrid[i][j] / dimension;
					j1 = boardgrid[i][j] % dimension;
				
					if (i1 == j1 && i1 == 0)
						i1 = j1 = dimension - 1;
					else if (j1 == 0)
					{
						j1 = dimension - 1;
						i1--;
					} else j1--;
				
					manhattand += Math.abs(j1 - j);
					manhattand += Math.abs(i1 - i); // calculate the manhattan distance
				}
			}
	}
	
	public int dimension()                 // board dimension n
	{
		return dimension;
	}
	
    public int hamming()                   // number of blocks out of place
    {
    	return hamming;
    }
    
    public int manhattan()                 // sum of Manhattan distances between blocks and goal
    {
    	return manhattand;
    }
    public boolean isGoal()                // is this board the goal board?
    {
    	return (manhattand == 0);
    }
    
    public Board twin()                    // a board that is obtained by exchanging any pair of blocks
    {
    	int[][] newboard = new int[dimension][dimension];
    	int a, b, x, y; // the pair of coordinates to swap
    	for (int i = 0; i < dimension; ++i)
    		for (int j = 0; j < dimension; ++j)
    			newboard[i][j] = boardgrid[i][j];
    	
    	a = b = 0;
    	for (; a < dimension; ++a)
    	{
    		for (; b < dimension; ++b)
    			if (newboard[a][b] != 0) break;
    		break;
    	}
    	
    	x = y = dimension - 1;
    	for (; x >= 0; x--)
    	{
    		for (; y >= 0; --y)
    			if ((newboard[x][y] != 0) && newboard[a][b] != newboard[x][y]) break;
    		break;
    	}
    	
    	newboard[a][b] = newboard[x][y];
    	newboard[x][y] = boardgrid[a][b];
    	return new Board(newboard);
    }
    
    public boolean equals(Object y)        // does this board equal y?
    {
    	if (y == this) return true;
    	if (y == null) return false;
    	if (y.getClass() != this.getClass()) return false;
    	
    	Board that = (Board) y;
    	if (that.dimension() != this.dimension) return false;
    	for (int i = 0; i < dimension; ++i)
    		for (int j = 0; j < dimension; ++j)
    			if (that.boardgrid[i][j] != this.boardgrid[i][j]) return false;
    	
    	return true;
    	
    }
    
    private void gridswitch(int[][] arr, int i1, int j1, int i2, int j2) // switches the values of (i1, j1) and (i2, j2) in a 2-dimensional array
    {
    	int temp = arr[i1][j1];
    	arr[i1][j1] = arr[i2][j2];
    	arr[i2][j2] = temp;
    }
    
    public Iterable<Board> neighbors()     // all neighboring boards
    {
    	BoardStack moves = new BoardStack();
    	int[][] newboard = new int[dimension][dimension];
    	for (int i = 0; i < dimension; ++i)
    		for (int j = 0; j < dimension; ++j)
    		{
    			newboard[i][j] = boardgrid[i][j];
    		}
    	
    	if ((zx - 1) >= 0)
    	{
    		gridswitch(newboard, zx, zy, zx - 1, zy);
    		moves.push(new Board(newboard));
    		gridswitch(newboard, zx, zy, zx - 1, zy);
    	}
    	if ((zx + 1) < dimension)
    	{
    		gridswitch(newboard, zx, zy, zx + 1, zy);
    		moves.push(new Board(newboard));
    		gridswitch(newboard, zx, zy, zx + 1, zy);
    	}
    	if ((zy - 1) >= 0)
    	{
    		gridswitch(newboard, zx, zy, zx, zy - 1);
    		moves.push(new Board(newboard));
    		gridswitch(newboard, zx, zy, zx, zy - 1);
    	}
    	if ((zy + 1) < dimension)
    	{
    		gridswitch(newboard, zx, zy, zx, zy + 1);
    		moves.push(new Board(newboard));
    		gridswitch(newboard, zx, zy, zx, zy + 1);
    	}
    		
    	return moves;
    }
    
    public String toString()               // string representation of this board
    {
    	StringBuilder s = new StringBuilder();
    	s.append(this.dimension + "\n");
    	
    	for (int i = 0; i < dimension; ++i)
    	{
    		for (int j = 0; j < dimension; ++j)
    			s.append(String.format("%2d ", boardgrid[i][j]));
    		s.append("\n");
    	}
    	
    	return s.toString();
    }

    public static void main(String[] args) // unit tests (not graded)
    {
    	// create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        Board other = initial.twin();
        
        System.out.println(initial.toString() + "\n" + other.toString());
        Iterator<Board> iter = initial.neighbors().iterator();
        
        System.out.println(initial.hamming() + " h");
        
    }
}