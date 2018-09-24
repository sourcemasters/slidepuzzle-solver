/***************************************
 * Michael W. 2018 August 10
 * This class implements a solver for a sliding puzzle of size n by n
 * The solver's algorithm is based on A* search with Manhattan priority.
 **************************************/
import edu.princeton.cs.algs4.In;
import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

public class Solver
{
	private MinPQ<BoardNode> inBoard; // input board from client
	private MinPQ<BoardNode> twinBoard; // twin of input board
	private int stepnumber; // number of moves to solve, -1 if unsolvable
	private BoardStack solution; // solution sequence of boards
	
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
	
	private class BoardNode implements Comparable<BoardNode> // search BoardNode with predecessor, number of moves, and board
	{
		private BoardNode prev;
		private int moves;
		private Board tiles;
		
		public BoardNode(int m, Board t)
		{
			prev = null;
			moves = m;
			tiles = t;
		}
		
		public BoardNode(BoardNode p, int m, Board t)
		{
			prev = p;
			moves = m;
			tiles = t;
		}
		
		public int compareTo(BoardNode that)
		{
			return (this.tiles.manhattan() + this.moves) - (that.tiles.manhattan() + that.moves);
		}
	}
	
    public Solver(Board initial)           // find a solution to the initial board (using the A* algorithm)
    {
    	if (initial == null) throw new java.lang.IllegalArgumentException("The solver got a null board.");
    	solution = new BoardStack();
    	inBoard = new MinPQ<BoardNode>();
    	twinBoard = new MinPQ<BoardNode>();
    	inBoard.insert(new BoardNode(0, initial));
    	twinBoard.insert(new BoardNode(0, initial.twin()));
    	
    	for (int i = 0; ; ++i)
    	{
    		if (i % 2 == 0)
    		{
    			BoardNode newmin = inBoard.delMin();
    			if (newmin.tiles.isGoal())
    			{
    				stepnumber = newmin.moves;
    				while (newmin.prev != null)
    				{
    					solution.push(newmin.tiles);
    					newmin = newmin.prev;
    				}
    				solution.push(initial);
    				break;
    			}
    			Iterable<Board> positions = newmin.tiles.neighbors();
    			for (Board nextboard : positions)
    			{
//    				System.out.println("move count: " + newmin.moves);
    				if ((newmin.prev == null) || !(nextboard.equals(newmin.prev.tiles))) 
    					inBoard.insert(new BoardNode(newmin, newmin.moves + 1, nextboard));
    			}
    		} else {
    			BoardNode nexmin = twinBoard.delMin();
    			if (nexmin.tiles.isGoal())
    			{
    				stepnumber = -1;
    				break;
    			}
    			Iterable<Board> positions = nexmin.tiles.neighbors();
    			for (Board nextboard : positions)
    				if ((nexmin.prev == null) || !(nextboard.equals(nexmin.prev.tiles))) 
    					twinBoard.insert(new BoardNode(nexmin, nexmin.moves + 1, nextboard));
    		}
    	}
    }
    
    public boolean isSolvable()            // is the initial board solvable?
    {
    	return (stepnumber != -1);
    }
    
    public int moves()                     // min number of moves to solve initial board; -1 if unsolvable
    {
    	return stepnumber;
    }
    
    public Iterable<Board> solution()      // sequence of boards in a shortest solution; null if unsolvable
    {
    	if (stepnumber == -1) return null;
    	return solution;
    }
 
    public static void main(String[] args) // solve a slider puzzle (given below)
    {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
    