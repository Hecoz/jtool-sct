package tsp;
/*
 * Copyright (C) 2000 by ETHZ/INF/CS
 * All rights reserved
 * 
 * @version $Id$
 * @author Florian Schneider
 */

public class TspSolver extends Thread {

    static final boolean debug = Tsp.debug;
    private final SharedData sh;

    /* non-static member variables, used by recusive_solve and visit_nodes */
    int CurDist, PathLen;
    int[] Visit = new int[Tsp.MAX_TOUR_SIZE];
    int[] Path = new int[Tsp.MAX_TOUR_SIZE];
    int visitNodes;
    
    TspSolver(SharedData sh)
    {
        this.sh = sh;
    }
    
    public void run() {
	Worker();
    }

    void Worker() {
	int curr = -1;
	for ( ; ; ) {
	    /* Continuously get a tour and split. */
	    curr = get_tour(curr, sh);
	    if (curr < 0) {
		if (debug) 
		    System.out.println("Worker: curr = " + curr); 
		break;
	    }
	    recursive_solve(curr);
	    if (debug) 
		DumpPrioQ(sh);
	}
    }

    /*
     * new_tour():
     *
     *    Create a new tour structure given an existing tour structure and
     *  the next edge to add to the tour.  Returns the index of the new structure.
     *
     */
    static int new_tour(int prev_index, int move, SharedData sh) {
	    int index = 0;
	    int i;
	    TourElement curr, prev;
	    
	    synchronized(sh.TourLock) {
		if (debug) 
		    System.out.println("new_tour");

		if (sh.TourStackTop >= 0) 
		    index = sh.TourStack[sh.TourStackTop--];
		else {
		    System.out.println("TourStackTop: " + sh.TourStackTop);
		    System.exit(-1);
		}

		curr = sh.Tours[index];
		prev = sh.Tours[prev_index];
       
		for (i = 0; i < Tsp.TspSize; i++) {
		    curr.prefix[i] = prev.prefix[i];
		    curr.conn = prev.conn;
		}
		curr.last = prev.last;
		curr.prefix_weight = prev.prefix_weight + 
		    sh.weights[curr.prefix[curr.last]][move];
		curr.prefix[++curr.last] = move;

		if (debug) 
		    MakeTourString(curr.last, curr.prefix);

		curr.conn |= 1 << move;
	
		return calc_bound(index, sh);
	    }
	}


    /*
     * set_best():
     *
     *  Set the global `best' value.
     *
     */
    static void set_best(int best, int[] path, SharedData sh) {
	if (best >= sh.MinTourLen) {
	    if (debug) 
		System.out.println("set_best: " + best + " <-> " + sh.MinTourLen);
	    return;
	}
	synchronized(sh.MinLock) {
	    if (best < sh.MinTourLen) {
		if (debug) {
		    System.out.print("(" + Thread.currentThread().getName() + ") ");
		    System.out.println("set_best MinTourLen: " + best + " (old: " + sh.MinTourLen + ")");
		}
		sh.MinTourLen = best;
		for (int i = 0; i < Tsp.TspSize; i++)
		    sh.MinTour[i] = path[i];
		if (debug) 
		    MakeTourString(Tsp.TspSize, sh.MinTour);
	    }
	}
    }


    static void MakeTourString(int len, int[] path) {
	int i;
	String tour_str="";

	for (i=0; i < len; i++) {
	    System.out.print(path[i]+" - ");
	    if (path[i] >= 10) System.out.print("   ");
	    else System.out.print("  ");
	}
	System.out.println(path[i]);
    }


    /*
     *  Add up min edges connecting all unconnected vertixes (AHU p. 331-335)
     *  At some point, we may want to use MST to get a lower bound. This
     *  bound should be higher (and therefore more accurate) but will take
     *  longer to compute. 
     */
    static int calc_bound(int curr_index, SharedData sh) {
	int i, j, wt, wt1, wt2;
	TourElement curr = sh.Tours[curr_index];
	
	synchronized(sh.TourLock) {
	    if (debug) 
		System.out.println("calc_bound");

	    /*
	     * wt1: the value of the edge with the lowest weight from the node
	     *	    we're testing to another unconnected node.
	     * wt2: the value of the edge with the second lowest weight
	     */

	    /* if we have one unconnected node */
	    if (curr.last == (Tsp.TspSize - 2)) {
		for (i = 0; i < Tsp.TspSize; i++) {
		    if ((curr.conn & (1<<i))==0) {
			/* we have found the one unconnected node */
			curr.prefix[Tsp.TspSize-1] = i;
			curr.prefix[Tsp.TspSize] = Tsp.StartNode;
		    
			/* add edges to and from the last node */
			curr.prefix_weight += sh.weights[curr.prefix[Tsp.TspSize-2]][i] +
			    sh.weights[i][curr.prefix[Tsp.StartNode]];
		    
			if (curr.prefix_weight < sh.MinTourLen) {
			    /* Store our new best path and its weight. */
			    set_best(curr.prefix_weight, curr.prefix, sh);
			}
		    
			/* De-allocate this tour so someone else can use it */
			curr.lower_bound = Tsp.BIGINT;
		    
			sh.TourStack[++sh.TourStackTop] = curr_index; /* Free tour. */
			return(Tsp.END_TOUR);
		    }
		}
	    }

	    curr.mst_weight = 0;

	    /*
	     * Add up the lowest weights for edges connected to vertices
	     * not yet used or at the ends of the current tour, and divide by two.
	     * This could be tweaked quite a bit.  For example:
	     *   (1) Check to make sure that using an edge would not make it
	     *       impossible for a vertex to have degree two.
	     *   (2) Check to make sure that the edge doesn't give some
	     *       vertex degree 3.
	     */

	    if (curr.last != Tsp.TspSize - 1) {
		for (i = 0; i < Tsp.TspSize; i++) {
		    if ((curr.conn & 1<<i)!=0) continue;
		    for (j = 0, wt1 = wt2 = Tsp.BIGINT; j < Tsp.TspSize; j++) {
			/* Ignore j's that are not connected to i (global->weights[i][j]==0), */
			/* or that are already in the tour and aren't either the      */
			/* first or last node in the current tour.		      */
			wt = sh.weights[i][j];
			if ((wt==0) || (((curr.conn&(1<<i))!=0)&&(j != curr.prefix[0])&&
					(j != curr.prefix[curr.last])))
			    continue;
		    
			/* Might want to check that edges go to unused vertices */
			if (wt < wt1) {
			    wt2 = wt1;
			    wt1 = wt;
			} else if (wt < wt2) wt2 = wt;
		    }

		    /* At least three unconnected nodes? */
		    if (wt2 != Tsp.BIGINT) curr.mst_weight += (wt1 + wt2) >> 1;
		    /* Exactly two unconnected nodes? */
		    else if (wt1 != Tsp.BIGINT) curr.mst_weight += wt1;
		}
		curr.mst_weight += 1;
	    }
	    curr.lower_bound = curr.mst_weight + curr.prefix_weight;
	    return(curr_index);
	}
    }

    static int LEFT_CHILD(int x) {
	return (x<<1);
    }

    static int RIGHT_CHILD(int x) {
	return (x<<1)+1;
    }

    static boolean less_than(PrioQElement x, PrioQElement y, SharedData sh) {
	return ((x.priority  < y.priority) || 
		(x.priority == y.priority) && 
		(sh.Tours[x.index].last > sh.Tours[y.index].last));
    }

    /*
     * DumpPrioQ():
     *
     * Dump the contents of PrioQ in some user-readable format (for debugging).
     *
     */
    static void DumpPrioQ(SharedData sh) {
	int pq, ind;
	
	System.out.println("DumpPrioQ");
	for (pq = 1; pq <= sh.PrioQLast; pq++) {
	    ind = sh.PrioQ[pq].index;
	    System.out.print(ind+"("+sh.PrioQ[pq].priority+"):\t");
	    if ((LEFT_CHILD(pq)<sh.PrioQLast) && (sh.PrioQ[pq].priority>sh.PrioQ[LEFT_CHILD(pq)].priority))
		System.out.print(" left child wrong! ");
	    if ((LEFT_CHILD(pq)<sh.PrioQLast) && (sh.PrioQ[pq].priority>sh.PrioQ[RIGHT_CHILD(pq)].priority)) 
		System.out.print(" right child wrong! ");
	    MakeTourString(sh.Tours[ind].last, sh.Tours[ind].prefix);
	}
    }


    /*
     * split_tour():
     *
     *  Break current tour into subproblems, and stick them all in the priority
     *  queue for later evaluation.
     *
     */
    static void split_tour(int curr_ind, SharedData sh) {
	int n_ind, last_node, wt;
	int i, pq, parent, index, priority;
	TourElement curr;
	PrioQElement cptr, pptr;

	synchronized(sh.TourLock) {
	    if (debug) 
		System.out.println("split_tour");

	    curr = sh.Tours[curr_ind];
	
	    if (debug)
		MakeTourString(curr.last, curr.prefix);

	    /* Create a tour and add it to the priority Q for each possible
	       path that can be derived from the current path by adding a single
	       node while staying under the current minimum tour length. */
	
	    if (curr.last != (Tsp.TspSize - 1)) {
		boolean t1, t2, t3;
		TourElement new_;
	    
		last_node = curr.prefix[curr.last];
		for (i = 0; i < Tsp.TspSize; i++) {
		    /*
		     * Check: 1. Not already in tour
		     *	      2. Edge from last entry to node in graph
		     *	and   3. Weight of new partial tour is less than cur min.
		     */
		    wt = sh.weights[last_node][i];
		    t1 = ((curr.conn & (1<<i)) == 0);
		    t2 = (wt != 0);
		    t3 = (curr.lower_bound + wt) <= sh.MinTourLen;
		    if (t1 && t2 && t3) {
			if ((n_ind = new_tour(curr_ind, i, sh)) == Tsp.END_TOUR) {
			    continue;
			}
			/*
			 * If it's shorter than the current best tour, or equal
			 * to the current best tour and we're reporting all min
			 * length tours, put it on the priority q.
			 */
			new_ = sh.Tours[n_ind];
		    
			if (sh.PrioQLast >= Tsp.MAX_NUM_TOURS-1) {
			    System.out.println("pqLast "+sh.PrioQLast);
			    System.exit(-1);
			}

			if (debug)
			    MakeTourString(new_.last, new_.prefix);
		    
			pq = ++sh.PrioQLast;
			cptr = sh.PrioQ[pq];
			cptr.index = n_ind;
			cptr.priority = new_.lower_bound;
		    
			/* Bubble the entry up to the appropriate level to maintain
			   the invariant that a parent is less than either of it's
			   children. */
			for (parent = pq >> 1, pptr = sh.PrioQ[parent];
			     (pq > 1) && less_than(cptr, pptr, sh);
			     pq = parent,  cptr = pptr, parent = pq >> 1, pptr = sh.PrioQ[parent]) {
			    /* PrioQ[pq] lower priority than parent -> SWITCH THEM. */
			    index = cptr.index;
			    priority = cptr.priority;
			    cptr.index = pptr.index;
			    cptr.priority = pptr.priority;
			    pptr.index = index;
			    pptr.priority = priority;
			}
		    } else if (debug) 
			System.out.println(" [" + curr.lower_bound + " + " + wt + " > " + sh.MinTourLen);
		}
	    }
	}
    }

    /*
     * find_solvable_tour():
     *
     * Used by both the normal TSP program (called by get_tour()) and
     * the RPC server (called by RPCserver()) to return the next solvable
     * (sufficiently short) tour.
     *
     */
    static int find_solvable_tour(SharedData sh) {
	int curr, i, left, right, child, index;
	int priority, last;
	PrioQElement pptr, cptr, lptr, rptr;
	
	synchronized(sh.TourLock) {
	    if (debug) 
		System.out.println("find_solvable_tour");

	    if (sh.Done != 0) { 
		if (debug) 
		    System.out.println("(" + Thread.currentThread().getName() + ") - done"); 
		return -1; 
	    }
	
	    for ( ; sh.PrioQLast != 0; ) {
		pptr = sh.PrioQ[1];
		curr = pptr.index;
		if (pptr.priority >= sh.MinTourLen) {
		    /* We're done -- there's no way a better tour could be found. */
		    if (debug) {
			System.out.print("\t" + Thread.currentThread().getName() + ": ");		
			MakeTourString(sh.Tours[curr].last, sh.Tours[curr].prefix);
		    }
		    sh.Done = 1;
		    return -1;
		}

		/* Bubble everything maintain the priority queue's heap invariant. */
		/* Move last element to root position. */
		cptr = sh.PrioQ[sh.PrioQLast];
		pptr.index  = cptr.index;
		pptr.priority = cptr.priority;
		sh.PrioQLast--;

		/* Push previous last element down tree to restore heap structure. */
		for (i = 1; i <= (sh.PrioQLast >> 1); ) {
		
		    /* Find child with lowest priority. */
		    left  = LEFT_CHILD(i);
		    right = RIGHT_CHILD(i);
	    
		    lptr = sh.PrioQ[left];
		    rptr = sh.PrioQ[right];
    	
		    if (left == sh.PrioQLast || less_than(lptr,rptr, sh)) {
			child = left;
			cptr = lptr;
		    } else {
			child = right;
			cptr = rptr;
		    }
    
    
		    /* Exchange parent and child, if necessary. */
		    if (less_than(cptr,pptr, sh)) {
			/* PrioQ[child] has lower prio than its parent - switch 'em. */
			index = pptr.index;
			priority = pptr.priority;
			pptr.index = cptr.index;
			pptr.priority = cptr.priority;
			cptr.index = index;
			cptr.priority = priority;
			i = child;
			pptr = cptr;
		    } else break;
		}
	    
		last = sh.Tours[curr].last;
	    
		/* If we're within `NodesFromEnd' nodes of a complete tour, find
		   minimum solutions recursively.  Else, split the tour. */
		if (last < Tsp.TspSize || last < 1) {
		    if (last >= (Tsp.TspSize - Tsp.NodesFromEnd - 1)) return(curr);
		    else split_tour(curr, sh);	/* The current tour is too long, */
		}				/* to solve now, break it up.	 */
		else {
		    /* Bogus tour index. */
		    if (debug) {
			System.out.print("\t" + Thread.currentThread().getName() + ": ");
			MakeTourString(Tsp.TspSize, sh.Tours[curr].prefix);
		    }
		}
		sh.TourStack[++sh.TourStackTop] = curr; /* Free tour. */

	    }
	    /* Ran out of candidates - DONE! */
	    sh.Done = 1;
	    return(-1);
	}
    }

    static int get_tour(int curr, SharedData sh) {

	if (debug) 
	    System.out.println("get_tour");

	synchronized(sh.TourLock) {
	    if (curr != -1) sh.TourStack[++sh.TourStackTop] = curr;
	    curr = find_solvable_tour(sh);
	}
	return(curr);
    }


    /*
     *   recursive_solve(curr_ind)
     *
     *	We're now supposed to recursively try all possible solutions
     *	starting with the current tour.  We do this by copying the
     *	state to local variables (to avoid unneeded conflicts) and
     *	calling visit_nodes to do the actual recursive solution.
     *
     */
    void recursive_solve(int index) {
	int i, j;
	TourElement curr = sh.Tours[index];

	if (debug) 
	    System.out.println("recursive_solve");

	CurDist = curr.prefix_weight;
	PathLen = curr.last + 1;
	
	for (i = 0; i < Tsp.TspSize; i++) Visit[i] = 0;
	
	for (i = 0; i < PathLen; i++) {
	    Path[i] = curr.prefix[i];
	    Visit[Path[i]] = 1;
	}

	if (PathLen > Tsp.TspSize) {
	    System.out.println("Pathlen: "+ PathLen);
	    System.exit(0);
	}
	
	if (CurDist == 0 || debug) {
	    if (debug) 
		System.out.print("\t" + Thread.currentThread().getName() + ": Tour " + index + " is ");
	    for (i = 0; i < PathLen-1; i++) {
		System.out.print(Path[i]+" - ");
	    }
	    if (debug) { 
		System.out.println(Path[i]);
		System.out.println("\t" + Thread.currentThread().getName() + ": Cur: " + CurDist + ", Min " + sh.MinTourLen + 
				   ", Len:" + (PathLen - 1) + ", Sz: " + Tsp.TspSize + "\n");
	    }
	}
	
	visit_nodes(Path[PathLen-1]);
    }

    /*
     *   visit_nodes()
     *
     *       Exhaustively visits each node to find Hamilton cycle.
     *       Assumes that search started at node from.
     *
     */
    void visit_nodes(int from) {
	int i;
	int dist, last;

	visitNodes++;	
	
	for (i = 1; i < Tsp.TspSize; i++) {
	    if (Visit[i]!=0) continue;	/* Already visited. */
	    if ((dist = sh.weights[from][i]) == 0) 
		continue; /* Not connected. */
	    if (CurDist + dist > sh.MinTourLen) 
		continue; /* Path too long. */
	    
	    /* Try next node. */
	    Visit[i] = 1;
	    Path[PathLen++] = i;
	    CurDist += dist;
	    
	    if (PathLen == Tsp.TspSize) {
		/* Visiting last node - determine if path is min length. */
		if ((last = sh.weights[i][Tsp.StartNode]) != 0 &&
		    (CurDist += last) < sh.MinTourLen) {
		    set_best(CurDist, Path, sh);
		}
		CurDist -= last;
	    } /* if visiting last node */
	    else if (CurDist < sh.MinTourLen) 
		visit_nodes(i);	/* Visit on. */
	    
	    /* Remove current try and loop again at this level. */
	    CurDist -= dist;
	    PathLen--;
	    Visit[i] = 0;
	}
    }
}

		     




