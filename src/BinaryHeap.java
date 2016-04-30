/** Binary heap with largest value on top**/

public class BinaryHeap {
	private static final int d = 2;
	private int heapsize;
	private Vertex[] heap;

	public BinaryHeap(int size) {
		heapsize = 0;
		heap = new Vertex[size + 1];
	}

	public int parent(int child) {
		return (child -1)/ d;
	}

	/*returns index of child k of parent i*/
	public int getChild(int i, int k) {
		return d * i + k;
	}

	public int getSize() {
		return heapsize;
	}

	public boolean isFull() {
		return heapsize == heap.length;
	}

	public boolean isEmpty() {
		return heapsize == 0;
	}

	public void insert(Vertex v) throws Exception {
		if(isFull())
			throw new Exception("Overflow");
		heap[heapsize++] = v;
		heapifyUp(heapsize - 1);
	}

	public void update(int v, int val) {
		if(heap[v] != null) { //if heapsize is 0
			//System.out.println("update index is " + v + " with value " + val + "and heapsize is..." + heapsize);
			heap[v].setD(val);
			Vertex inserted = heap[v];

			//swap nodes
			while(v > 0 && inserted.getD() < heap[parent(v)].getD()) {
				heap[v] = heap[parent(v)];
				heap[v].setIndex(v);
				heap[parent(v)] = inserted;
				inserted.setIndex(parent(v));
				v = parent(v);
			}
		}
	}

	public Vertex extractMax() {
		Vertex v = heap[0];
		heapifyDown(0);
		return v;
	}
	/*compares new node to each parent and swaps if less then*/
	public void heapifyUp(int index) {

		Vertex inserted = heap[index];
		inserted.setIndex(index);

		//swap nodes
		while(index > 0 && inserted.getD() > heap[parent(index)].getD()) {
			heap[index] = heap[parent(index)];
			heap[index].setIndex(index);
			heap[parent(index)] = inserted;
			inserted.setIndex(parent(index));
			index = parent(index);
		}
	}

	public void heapifyDown(int index) {
		heap[index] = heap[heapsize - 1]; //delete min and swap it with last element
		heap[heapsize -1] = null; //delete last element
		heapsize--;
		Vertex node = heap[index];
		if(node == null)
			return;
		int nodeInd = index;
		node.setIndex(index);

		/*now test this node against children and swap if > them*/
		for(int i = 0; i < Math.log(heapsize)/Math.log(2); i++) { //iterates the depth of the tree

			//if left AND right children are null
			if(heap[getChild(nodeInd, 1)] == null && heap[getChild(nodeInd, 2)] == null) {
				break;
			}

			//if right child is null 
			if(heap[getChild(nodeInd, 2)] == null) {
				if(node.getD() < heap[getChild(nodeInd, 1)].getD()) { //if left is smaller
					heap[nodeInd] = heap[getChild(nodeInd, 1)];
					heap[nodeInd].setIndex(nodeInd);
					heap[getChild(nodeInd, 1)] = node;
					node.setIndex(getChild(nodeInd, 1));
					nodeInd = getChild(nodeInd, 1);
				}
				continue;
			}

			//if neither child is null
			if(node.getD() < heap[getChild(nodeInd, 1)].getD() || node.getD() < heap[getChild(nodeInd, 2)].getD()) {       
				if(heap[getChild(nodeInd, 1)].getD() > heap[getChild(nodeInd, 2)].getD()) { 
					heap[nodeInd] = heap[getChild(nodeInd, 1)];
					heap[nodeInd].setIndex(nodeInd);
					heap[getChild(nodeInd, 1)] = node;
					node.setIndex(getChild(nodeInd, 1));
					nodeInd = getChild(nodeInd, 1);
				} else {
					heap[nodeInd] = heap[getChild(nodeInd, 2)];
					heap[nodeInd].setIndex(nodeInd);
					heap[getChild(nodeInd, 2)] = node;
					node.setIndex(getChild(nodeInd, 2));
					nodeInd = getChild(nodeInd, 2);
				}
			}
		}
	}

	public String HeapString() {
		int size = heapsize;
		int level = 0;
		int index = 0;
		String s = "";

		while(size > 0) {
			for(int i = (int) Math.pow(2, level); i > 0; i--) {
				if(heap[index] != null) {
					s += (heap[index].getV() + " " + heap[index].getD() + "|");
					size--;
					index++;
				}
			}
			s += ("\n");
			level++;
		}
		s += ("\n");
		return s;
	}

	public void printHeap() {
		int size = heapsize;
		int level = 0;
		int index = 0;

		while(size > 0) {
			for(int i = (int) Math.pow(2, level); i > 0; i--) {
				if(heap[index] != null) {
					System.out.print(heap[index].getD() + " ");
					size--;
					index++;
				}
			}
			System.out.println();
			level++;
		}
		System.out.println();
	}

	public static void main(String[] args) throws Exception {
		BinaryHeap b = new BinaryHeap(20);
		b.insert(new Vertex(5));
		b.insert(new Vertex(8));
		b.insert(new Vertex(35));
		b.insert(new Vertex(3));
		b.insert(new Vertex(15));
//		b.insert(new Vertex(1,5));
//		b.insert(new Vertex(1,13));
//		b.insert(new Vertex(1,26));
//		b.insert(new Vertex(1,4));
//		b.insert(new Vertex(1,65));

//		String s = b.HeapString();
//		System.out.print(s);
		
		        b.printHeap();
		//        
		        b.extractMax();
		        b.printHeap();
		//        
		        b.extractMax();
		        b.printHeap();
		        
		        b.extractMax();
		        b.printHeap();
		        
		        b.extractMax();
		        b.printHeap();

	}


}
