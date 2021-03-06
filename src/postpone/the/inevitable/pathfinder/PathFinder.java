package postpone.the.inevitable.pathfinder;

import java.util.ArrayList;

import postpone.the.inevitable.db.Level;
import postpone.the.inevitable.game.AbstractTower;

import android.graphics.Point;

public class PathFinder {

	// Stores an array of the walkable search nodes.
	private SearchNode[][] searchNodes;

	// The width of the map.
	private final int levelWidth;

	// The height of the map.
	private final int levelHeight;
	
	// Holds search nodes that are avaliable to search.
	private final ArrayList<SearchNode> openList = new ArrayList<SearchNode>();
	// Holds the nodes that have already been searched.
	private final ArrayList<SearchNode> closedList = new ArrayList<SearchNode>();
	
	/// <summary>
	/// Constructor.
	/// </summary>
	public PathFinder(Level lvl)
	{
		levelWidth = lvl.Width();
		levelHeight = lvl.Height();
		InitializeSearchNodes(lvl);
	}
	
	public SearchNode getNode(int x, int y) {
		return searchNodes[x][y];
	}
	
	// <summary>
	/// Splits our level up into a grid of nodes.
	/// </summary>
	private void InitializeSearchNodes(Level lvl)
	{

		searchNodes = new SearchNode[levelWidth][levelHeight];
		//For each of the tiles in our map, we
		// will create a search node for it.
		for (int x = 0; x < levelWidth; x++)
		{
			for (int y = 0; y < levelHeight; y++)
			{
				//Create a search node to represent this tile.
				final SearchNode node = new SearchNode();
				node.Position = new Point(x, y);
				
				// Our enemies can only walk on grass tiles.
				node.Walkable = lvl.GetIndex(x, y) == 0;

				node.towerType = AbstractTower.TOWER_TYPE.values()[lvl.GetIndex(x, y)];
				
				// We only want to store nodes
				// that can be walked on.
				//if (node.Walkable == true)
				//{
					node.Neighbors = new SearchNode[4];
					node.NeighborsDiagonal = new SearchNode[4];
					searchNodes[x][y] = node;
				//}
			}
		}
		
		// Now for each of the search nodes, we will
		// connect it to each of its neighbours.
		for (int x = 0; x < levelWidth; x++)
		{
			for (int y = 0; y < levelHeight; y++)
			{
				final SearchNode node = searchNodes[x][y];

				// We only want to look at the nodes that 
				// our enemies can walk on.
				if (node == null || node.Walkable == false)
				{
					continue;
				}

			    // An array of all of the possible neighbors this 
			    // node could have. (We will ignore diagonals for now.)
				Point[] neighbors = new Point[] {
						new Point (x, y - 1), // The node above the current node
						new Point (x, y + 1), // The node below the current node.
						new Point (x - 1, y), // The node left of the current node.
						new Point (x + 1, y), // The node right of the current node
				};

				// An array of all of the possible diagonal neighbors this 
			    // node could have.
				Point[] neighborsDiagonal = new Point[] {
						new Point (x-1, y-1), // The uppe left node
						new Point (x-1, y+1), // The lower left node.
						new Point (x+1, y-1), // The upper right node.
						new Point (x+1, y+1), // The lower right node.
				};

			    // We loop through each of the possible neighbors
				for (int i = 0; i < neighbors.length; i++)
				{
					final Point position = neighbors[i];

			        // We need to make sure this neighbour is part of the level.
					if (position.x < 0 || position.x > levelWidth - 1 ||
							position.y < 0 || position.y > levelHeight - 1)
			        {
						continue;
			        }

					final SearchNode neighbor = searchNodes[position.x][position.y];

			        // We will only bother keeping a reference 
			        // to the nodes that can be walked on.
			        //if (neighbor == null || neighbor.Walkable == false)
			        //{
			        //    continue;
			        //}

			        // Store a reference to the neighbor.
			        node.Neighbors[i] = neighbor;
				}
				
				
			    // We loop through each of the possible diagonal neighbors
				for (int i = 0; i < neighborsDiagonal.length; i++)
				{
					final Point position = neighborsDiagonal[i];

			        // We need to make sure this diagonal neighbour is part of the level.
					if (position.x < 0 || position.x > levelWidth - 1 ||
							position.y < 0 || position.y > levelHeight - 1)
			        {
						continue;
			        }

					final SearchNode neighbor = searchNodes[position.x][position.y];

			        // We will only bother keeping a reference 
			        // to the nodes that can be walked on.
			        //if (neighbor == null || neighbor.Walkable == false)
			        //{
			        //    continue;
			        //}

			        // Store a reference to the neighbor.
			        node.NeighborsDiagonal[i] = neighbor;
				}
			}
		}
	}


	/// <summary>
	/// Returns an estimate of the distance between two points. (H)
	/// </summary>
	private float Heuristic(Point point1, Point point2)
	{
	    return Math.abs(point1.x - point2.x) +
	           Math.abs(point1.y - point2.y);
	}
	
	/// <summary>
	/// Resets the state of the search nodes.
	/// </summary>
	private void ResetSearchNodes()
	{
	    openList.clear();
	    closedList.clear();

	    for (int x = 0; x < levelWidth; x++)
	    {
	        for (int y = 0; y < levelHeight; y++)
	        {
	            final SearchNode node = searchNodes[x][y];

	            if (node == null)
	            {
	                continue;
	            }

	            node.InOpenList = false;
	            node.InClosedList = false;

	            node.DistanceTraveled = Float.MAX_VALUE;
	            node.DistanceToGoal = Float.MAX_VALUE;
	        }
	    }
	}
	
	
	/// <summary>
	/// Returns the node with the smallest distance to goal.
	/// </summary>
	private SearchNode FindBestNode()
	{
	    SearchNode currentTile = openList.get(0);

	    float smallestDistanceToGoal = Float.MAX_VALUE;

	    // Find the closest node to the goal.
	    for (int i = 0; i < openList.size(); i++)
	    {
	        if (openList.get(i).DistanceToGoal < smallestDistanceToGoal)
	        {
	            currentTile = openList.get(i);
	            smallestDistanceToGoal = currentTile.DistanceToGoal;
	        }
	    }
	    return currentTile;
	}
	
	/// <summary>
	/// Use the parent field of the search nodes to trace
	/// a path from the end node to the start node.
	/// </summary>
	private ArrayList<Point> FindFinalPath(SearchNode startNode, SearchNode endNode)
	{
	    closedList.add(endNode);

	    SearchNode parentTile = endNode.Parent;

	    // Trace back through the nodes using the parent fields
	    // to find the best path.
	    while (parentTile != startNode)
	    {
	        closedList.add(parentTile);
	        parentTile = parentTile.Parent;
	    }

	    final ArrayList<Point> finalPath = new ArrayList<Point>();

	    // Reverse the path and transform into world space.
	    for (int i = closedList.size() - 1; i >= 0; i--)
	    {
	    	//No idea to use this
	        //finalPath.add(new Point(closedList.get(i).Position.x * 32,
	        //                          closedList.get(i).Position.y * 32));
	        finalPath.add(new Point(closedList.get(i).Position.x,
                    closedList.get(i).Position.y));
	    }

	    return finalPath;
	}	
	
	
	/// <summary>
	/// Finds the optimal path from one point to another.
	/// </summary>
	public ArrayList<Point> FindPath(Point startPoint, Point endPoint)
	{
		//Man kan bygga p� start position utan att det spelar n�gpn roll. Men tack vare denna s� �r det l�st
	    if (!searchNodes[startPoint.x][startPoint.y].Walkable) {
	        return new ArrayList<Point>();
	    }
		
		
	    // Only try to find a path if the start and end points are different.
	    if (startPoint == endPoint)
	    {
		    //Log.e("Pathfinder:","Samma start som slutpunkt");
	    	
	        return new ArrayList<Point>();
	    }

	    /////////////////////////////////////////////////////////////////////
	    // Step 1 : Clear the Open and Closed Lists and reset each node�s F 
	    //          and G values in case they are still set from the last 
	    //          time we tried to find a path. 
	    /////////////////////////////////////////////////////////////////////
	    ResetSearchNodes();

	    // Store references to the start and end nodes for convenience.
	    final SearchNode startNode = searchNodes[startPoint.x][startPoint.y];
	    final SearchNode endNode = searchNodes[endPoint.x][endPoint.y];

	    /////////////////////////////////////////////////////////////////////
	    // Step 2 : Set the start node�s G value to 0 and its F value to the 
	    //          estimated distance between the start node and goal node 
	    //          (this is where our H function comes in) and add it to the 
	    //          Open List. 
	    /////////////////////////////////////////////////////////////////////
	    startNode.InOpenList = true;

	    startNode.DistanceToGoal = Heuristic(startPoint, endPoint);
	    startNode.DistanceTraveled = 0;

	    openList.add(startNode);

	    /////////////////////////////////////////////////////////////////////
	    // Setp 3 : While there are still nodes to look at in the Open list : 
	    /////////////////////////////////////////////////////////////////////
	    while (openList.size() > 0)
	    {

	    	/////////////////////////////////////////////////////////////////
	        // a) : Loop through the Open List and find the node that 
	        //      has the smallest F value.
	        /////////////////////////////////////////////////////////////////
	        SearchNode currentNode = FindBestNode();

	        /////////////////////////////////////////////////////////////////
	        // b) : If the Open List empty or no node can be found, 
	        //      no path can be found so the algorithm terminates.
	        /////////////////////////////////////////////////////////////////
	        if (currentNode == null)
	        {
	            break;
	        }

	        /////////////////////////////////////////////////////////////////
	        // c) : If the Active Node is the goal node, we will 
	        //      find and return the final path.
	        /////////////////////////////////////////////////////////////////
	        if (currentNode == endNode)
	        {
	    	    // Trace our path back to the start.
	            return FindFinalPath(startNode, endNode);
	        }

	        /////////////////////////////////////////////////////////////////
	        // d) : Else, for each of the Active Node�s neighbours :
	        /////////////////////////////////////////////////////////////////
	        for (int i = 0; i < currentNode.Neighbors.length; i++)
	        {
	            SearchNode neighbor = currentNode.Neighbors[i];

	            //////////////////////////////////////////////////
	            // i) : Make sure that the neighbouring node can 
	            //      be walked across. 
	            //////////////////////////////////////////////////
	            if (neighbor == null || neighbor.Walkable == false)
	            {
	                continue;
	            }

	            //////////////////////////////////////////////////
	            // ii) Calculate a new G value for the neighbouring node.
	            //////////////////////////////////////////////////
	            float distanceTraveled = currentNode.DistanceTraveled + 1;
	    	    
	            // An estimate of the distance from this node to the end node.
	            float heuristic = Heuristic(neighbor.Position, endPoint);

	            //////////////////////////////////////////////////
	            // iii) If the neighbouring node is not in either the Open 
	            //      List or the Closed List : 
	            //////////////////////////////////////////////////
	            if (neighbor.InOpenList == false && neighbor.InClosedList == false)
	            {
	            	
	            	// (1) Set the neighbouring node�s G value to the G value 
	                //     we just calculated.
	                neighbor.DistanceTraveled = distanceTraveled;

	                // (2) Set the neighbouring node�s F value to the new G value + 
	                //     the estimated distance between the neighbouring node and
	                //     goal node.
	                neighbor.DistanceToGoal = distanceTraveled + heuristic;
	                // (3) Set the neighbouring node�s Parent property to point at the Active 
	                //     Node.
	                neighbor.Parent = currentNode;
	                // (4) Add the neighbouring node to the Open List.
	                neighbor.InOpenList = true;
	                openList.add(neighbor);
	            }
	            //////////////////////////////////////////////////
	            // iv) Else if the neighbouring node is in either the Open 
	            //     List or the Closed List :
	            //////////////////////////////////////////////////
	            else if (neighbor.InOpenList || neighbor.InClosedList)
	            {
	                // (1) If our new G value is less than the neighbouring 
	                //     node�s G value, we basically do exactly the same 
	                //     steps as if the nodes are not in the Open and 
	                //     Closed Lists except we do not need to add this node 
	                //     the Open List again.
	                if (neighbor.DistanceTraveled > distanceTraveled)
	                {
	                    neighbor.DistanceTraveled = distanceTraveled;
	                    neighbor.DistanceToGoal = distanceTraveled + heuristic;

	                    neighbor.Parent = currentNode;
	                }
	            }
	        }
	        
	        /////////////////////////////////////////////////////////////////
	        // e) Remove the Active Node from the Open List and add it to the 
	        //    Closed List
	        /////////////////////////////////////////////////////////////////
	        openList.remove(currentNode);
	        currentNode.InClosedList = true;
	    }

	    // No path could be found.
	    //Log.e("Pathfinder:","Ingen v�g hittad");
	    return new ArrayList<Point>();
	}	
	
}


