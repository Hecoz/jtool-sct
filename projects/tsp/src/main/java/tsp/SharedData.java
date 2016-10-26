package tsp;

public class SharedData
{
    public int[][] weights = new int[Tsp.MAX_TOUR_SIZE][Tsp.MAX_TOUR_SIZE];
    public int TourStackTop;
    public int Done;
    public int PrioQLast;
    public int MinTourLen;
    public int[] MinTour = new int[Tsp.MAX_TOUR_SIZE];
    public Integer MinLock = new Integer(0);
    public Integer TourLock = new Integer(0);
    public Integer barrier = new Integer(0);
    public PrioQElement[] PrioQ = new PrioQElement[Tsp.MAX_NUM_TOURS];
    public int[] TourStack = new int[Tsp.MAX_NUM_TOURS];
    public TourElement[] Tours = new TourElement[Tsp.MAX_NUM_TOURS];
}
