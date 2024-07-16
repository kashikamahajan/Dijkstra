// === CS400 File Header Information ===
// Name: Kashika Mahajan
// Email: kmahajan7@wisc.edu
// Lecturer: Gary Dahl
// Notes to Grader:

package djikstra;

import java.util.PriorityQueue;

import djikstra.BaseGraph.Node;

import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number> extends BaseGraph<NodeType, EdgeType>
		implements GraphADT<NodeType, EdgeType> {

	/**
	 * While searching for the shortest path between two nodes, a SearchNode
	 * contains data about one specific path between the start node and another node
	 * in the graph. The final node in this path is stored in its node field. The
	 * total cost of this path is stored in its cost field. And the predecessor
	 * SearchNode within this path is referened by the predecessor field (this field
	 * is null within the SearchNode containing the starting node in its node
	 * field).
	 *
	 * SearchNodes are Comparable and are sorted by cost so that the lowest cost
	 * SearchNode has the highest priority within a java.util.PriorityQueue.
	 */
	protected class SearchNode implements Comparable<SearchNode> {
		public Node node;
		public double cost;
		public SearchNode predecessor;

		public SearchNode(Node node, double cost, SearchNode predecessor) {
			this.node = node;
			this.cost = cost;
			this.predecessor = predecessor;
		}

		public int compareTo(SearchNode other) {
			if (cost > other.cost)
				return +1;
			if (cost < other.cost)
				return -1;
			return 0;
		}
	}

	/**
	 * Constructor that sets the map that the graph uses.
	 */
	public DijkstraGraph() {
		super(new PlaceholderMap<>());
	}

	/**
	 * This helper method creates a network of SearchNodes while computing the
	 * shortest path between the provided start and end locations. The SearchNode
	 * that is returned by this method is represents the end of the shortest path
	 * that is found: it's cost is the cost of that shortest path, and the nodes
	 * linked together through predecessor references represent all of the nodes
	 * along that shortest path (ordered from end to start).
	 *
	 * @param start the data item in the starting node for the path
	 * @param end   the data item in the destination node for the path
	 * @return SearchNode for the final end node within the shortest path
	 * @throws NoSuchElementException when no path from start to end is found or
	 *                                when either start or end data do not
	 *                                correspond to a graph node
	 */
	protected SearchNode computeShortestPath(NodeType start, NodeType end) {
		// implement in step 5.3

		PriorityQueue<SearchNode> priorityQ = new PriorityQueue<>();
		MapADT<Node, SearchNode> definedPaths = new PlaceholderMap<>();

		//an error is automatically thrown if the specified nodes are not present in the graph
		Node startNode = nodes.get(start);
		Node endNode = nodes.get(end);


		SearchNode startSearch = new SearchNode(startNode, 0.0, null);
		priorityQ.add(startSearch);
		definedPaths.put(startNode, startSearch);

		while (!priorityQ.isEmpty()) {
			SearchNode top = priorityQ.poll();

			if (top.node.equals(endNode)) {
				return top;
			}

			for (Edge e : top.node.edgesLeaving) {
				SearchNode alternatePath = new SearchNode(e.successor, (top.cost + e.data.doubleValue()), top);
				if (definedPaths.containsKey(e.successor)) {
					SearchNode currentPathNode = definedPaths.get(e.successor);
					if (currentPathNode.compareTo(alternatePath) > 0) {
						definedPaths.remove(e.successor);
						definedPaths.put(e.successor, alternatePath);
						priorityQ.remove(currentPathNode);
						priorityQ.add(alternatePath);
					}
				} else {
					definedPaths.put(e.successor, alternatePath);
					priorityQ.add(alternatePath);
				}

			}
		}

		throw new NoSuchElementException("No path to end node");
	}

	/**
	 * Returns the list of data values from nodes along the shortest path from the
	 * node with the provided start value through the node with the provided end
	 * value. This list of data values starts with the start value, ends with the
	 * end value, and contains intermediary values in the order they are encountered
	 * while traversing this shorteset path. This method uses Dijkstra's shortest
	 * path algorithm to find this solution.
	 *
	 * @param start the data item in the starting node for the path
	 * @param end   the data item in the destination node for the path
	 * @return list of data item from node along this shortest path
	 */
	public List<NodeType> shortestPathData(NodeType start, NodeType end) {
		SearchNode shortestPath = this.computeShortestPath(start, end);
		List<NodeType> path = new LinkedList<>();
		SearchNode pred = shortestPath;
		while (pred != null) {
			path.add(0, pred.node.data);
			pred = pred.predecessor;

		}
		// implement in step 5.4
		return path;
	}

	/**
	 * Returns the cost of the path (sum over edge weights) of the shortest path
	 * freom the node containing the start data to the node containing the end data.
	 * This method uses Dijkstra's shortest path algorithm to find this solution.
	 *
	 * @param start the data item in the starting node for the path
	 * @param end   the data item in the destination node for the path
	 * @return the cost of the shortest path between these nodes
	 */
	public double shortestPathCost(NodeType start, NodeType end) {
		SearchNode shortestPath = this.computeShortestPath(start, end);

		return shortestPath.cost;
	}

	// TODO: implement 3+ tests in step 4.1

	public static void testOne() {
		DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
		graph.insertNode("A");
		graph.insertNode("B");
		graph.insertNode("C");
		graph.insertNode("D");
		graph.insertNode("E");

		graph.insertEdge("A", "B", 15);
		graph.insertEdge("A", "C", 1);
		graph.insertEdge("A", "D", 4);
		graph.insertEdge("B", "E", 1);
		graph.insertEdge("E", "C", 10);
		graph.insertEdge("D", "E", 10);
		graph.insertEdge("B", "D", 2);

		graph.insertEdge("B", "A", 15);
		graph.insertEdge("C", "A", 1);
		graph.insertEdge("D", "A", 4);
		graph.insertEdge("E", "B", 1);
		graph.insertEdge("C", "E", 10);
		graph.insertEdge("E", "D", 10);
		graph.insertEdge("D", "B", 2);

		System.out.println(graph.shortestPathData("A", "E"));
		System.out.println(graph.shortestPathCost("A", "E"));


	}

	
	public static void testTwo() {

		DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
		graph.insertNode("A");
		graph.insertNode("B");
		graph.insertNode("C");
		graph.insertNode("D");
		graph.insertNode("E");

		graph.insertEdge("A", "B", 15);
		graph.insertEdge("A", "C", 1);
		graph.insertEdge("A", "D", 4);
		graph.insertEdge("B", "E", 1);
		graph.insertEdge("E", "C", 10);
		graph.insertEdge("D", "E", 10);
		graph.insertEdge("B", "D", 2);

		graph.insertEdge("B", "A", 15);
		graph.insertEdge("C", "A", 1);
		graph.insertEdge("D", "A", 4);
		graph.insertEdge("E", "B", 1);
		graph.insertEdge("C", "E", 10);
		graph.insertEdge("E", "D", 10);
		graph.insertEdge("D", "B", 2);

		System.out.println(graph.shortestPathData("C", "D"));
		System.out.println(graph.shortestPathCost("C", "D"));


	}

	public static void testThree() {
		DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
		graph.insertNode("A");
		graph.insertNode("B");
		graph.insertNode("C");
		graph.insertNode("D");
		graph.insertNode("E");

		graph.insertEdge("A", "B", 15);
		graph.insertEdge("A", "C", 1);
		graph.insertEdge("A", "D", 4);
		graph.insertEdge("E", "C", 10);

		graph.insertEdge("B", "A", 15);
		graph.insertEdge("C", "A", 1);
		graph.insertEdge("D", "A", 4);
		graph.insertEdge("E", "B", 1);
		graph.insertEdge("E", "D", 10);

		try {
			System.out.println(graph.shortestPathData("A", "E"));
			System.out.println(graph.shortestPathCost("A", "E"));
		} catch (NoSuchElementException e) {
			System.out.println("ok");
		}
	}

	public static void testFour() {
		DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
		graph.insertNode("A");
		graph.insertNode("B");
		graph.insertNode("C");
		graph.insertNode("D");
		graph.insertNode("E");
		graph.insertNode("F");
		graph.insertNode("G");

		graph.insertEdge("A", "E", 3);
		graph.insertEdge("A", "D", 2);
		graph.insertEdge("A", "B", 4);
		graph.insertEdge("E", "D", 4);
		graph.insertEdge("E", "B", 3);
		graph.insertEdge("E", "C", 2);
		graph.insertEdge("C", "B", 2);
		graph.insertEdge("C", "F", 3);
		graph.insertEdge("F", "G", 2);
		graph.insertEdge("D", "G", 8);
		graph.insertEdge("D", "C", 2);

		System.out.println("testfour "+graph.shortestPathData("A", "G"));
		System.out.println(graph.shortestPathCost("A", "G"));

	}

	public static void testFive() {
		DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
		graph.insertNode("A");
		graph.insertNode("B");
		graph.insertNode("C");
		graph.insertNode("D");
		graph.insertNode("E");
		graph.insertNode("F");
		graph.insertNode("G");

		graph.insertEdge("A", "B", 1);//
		graph.insertEdge("A", "C", 4);//
		graph.insertEdge("A", "F", 2);//
		graph.insertEdge("B", "E", 4);//
		graph.insertEdge("B", "F", 3);//
		graph.insertEdge("F", "C", 1);//
		graph.insertEdge("F", "D", 3);//
		graph.insertEdge("C", "D", 1);//
		graph.insertEdge("C", "G", 4);//
		graph.insertEdge("D", "E", 2);//
		graph.insertEdge("D", "G", 2);//
		graph.insertEdge("E", "G", 3);//

		graph.insertEdge("E", "B", 2);
		graph.insertEdge("D", "B", 4);

		System.out.println(graph.shortestPathData("E", "B"));
		System.out.println(graph.shortestPathCost("E", "B"));

		System.out.println(graph.shortestPathData("E", "D"));
		System.out.println(graph.shortestPathCost("E", "D"));

	}
	
	public static void main(String args[]) {
		testOne();
		testTwo();
		testThree();
		testFour();
		testFive();
	}

}
