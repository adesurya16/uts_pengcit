import java.awt.Color;
import java.awt.Point;
// import android.graphics.Point;
// import android.util.Log;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.sql.Array;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;

class Skeleton{
    private static final int MAX_DIRECTION = 8;
    private int matrixBlackWhite[][];
    private int width;
    private int height;
    private ArrayList<Point> resultThinningList;
    // private ArrayList<Integer> chainCode;
    private Point pointMax;
    private Point pointMin;
    private final int MAX_INTERSECT_PATTERN = 20;
    private final int MAX_ENDPOINT_PATTERN = 4;
    private final int MAX_SISA = 4;
    private final int sisaPointPattern[][][] = {
        // 0
        {{1, 0, 0},
        {0, 1, 0},
        {0, 0, 0}},

        // 1
        {{0, 0, 1},
        {0, 1, 0},
        {0, 0, 0}},

        // 2
        {{0, 0, 0},
        {0, 1, 0},
        {0, 0, 1}},

        // 3
        {{0, 0, 0},
        {0, 1, 0},
        {1, 0, 0}}
    };
    private final int intersectPoinPattern[][][] = {
        // 0
        {{-1, -1, -1},
        {1, 1, 1},
        {0, 1, 0}},

        // 1
        {{ 0, 1, -1},
        { 1, 1, -1},
        { 0, 1, -1}},

        // 2
        {{ 0, 1, 0},
        { 1, 1, 1},
        { -1, -1, -1}},

        // 3
        {{ -1, 1, 0},
        { -1, 1, 1},
        { -1, 1, 0}},

        // 4
        {{ -1, 1, 0},
        { 0, 1, 1},
        { 1, 0, -1}},
        
        // 5
        {{ 1, 0, -1},
        { 0, 1, 1},
        { -1, 1, 0}},
        
        // 6
        {{ -1, 0, 1},
        { 1, 1, 0},
        { 0, 1, -1}},

        // 7
        {{ 0, 1, -1},
        { 1, 1, 0},
        { -1, 0, 1}},

        // 8
        {{ -1, 0, 1},
        { 0, 1, 0},
        { 1, 0, 1}},
        
        // 9
        {{ 1, 0, -1},
        { 0, 1, 0},
        { 1, 0, 1}},

        // 10
        {{ 1, 0, 1},
        { 0, 1, 0},
        { 1, 0, -1}},

        // 11
        {{ 1, 0, 1},
        { 0, 1, 0},
        { -1, 0, 1}},

        // 12
        {{ -1, 0, 1},
        { 1, 1, 0},
        { 0, 0, 1}},

        // 13
        {{ 0, 0, 1},
        { 1, 1, 0},
        { -1, 0, 1}},

        // 14
        {{ 0, 1, -1},
        { 0, 1, 0},
        { 1, 0, 1}},

        // 15
        {{ -1, 1, 0},
        { 0, 1, 0},
        { 1, 0, 1}},

        // 16
        {{ 1, 0, 0},
        { 0, 1, 1},
        { 1, 0, -1}},

        // 17
        {{ 1, 0, -1},
        { 0, 1, 1},
        { 1, 0, 0}},

        // 18
        {{ 1, 0, 1},
        { 0, 1, 0},
        { -1, 1, 0}},

        // 19
        {{ 1, 0, 1},
        { 0, 1, 0},
        { 0, 1, -1}}
    };
    private final int endPointPattern[][][]= { 
        {{0, 0, 0},
        {0, 1, 0},
        {-1, 1, -1}},

        {{0, 0, -1},
        {0, 1, 1},
        {0, 0, -1}},

        {{-1, 1, -1},
        {0, 1, 0},
        {0, 0, 0}},

        {{-1, 0, 0},
        {1, 1, 0},
        {-1, 0, 0}}
    };
    /*
    P9 P2 P3
    P8 P1 P4
    P7 P6 P5
    Dir
    */

    private final int iterationDirections[][] = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    // private final  int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};

    // private ArrayList<Point> toZerolist;

    public Skeleton(ArrayList<Point> pList, int height, int width){
        this.width = width;
        this.height = height;
        // this.toZerolist = new ArrayList<>();
        this.resultThinningList = new ArrayList<>();
        for (Point p: pList){
            this.resultThinningList.add(new Point(p.x, p.y));
        }
        // this.resultThinningList.addAll(pList);
        this.pointMax = new Point();
        this.pointMin = new Point();

        // init matrix
        this.matrixBlackWhite = new int[height][];
        for (int i=0;i<height;i++){
            this.matrixBlackWhite[i] = new int[width];
        }

//        bacaFile();
        for(int i =0;i<height;i++){
            for(int j=0;j<width;j++){
//                Log.d("isi matrix : ", " " + matrix[i][j]);
                this.matrixBlackWhite[i][j] = 0;
            }
        }
        getBoundPoints();
       initMatrixFromList();
    }

    public void initMatrixFromList(){
        for(int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                this.matrixBlackWhite[i][j] = 0;
            }
        }

        for(Point p: this.resultThinningList){
            this.matrixBlackWhite[p.x][p.y] = 1;
        }
    }

    public void initMatrixFromList(ArrayList<Point> pList){
        for(int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                this.matrixBlackWhite[i][j] = 0;
            }
        }

        for(Point p: pList){
            this.matrixBlackWhite[p.x][p.y] = 1;
        }
    }

    public Point getPointMax(){
        return this.pointMax;
    }

    public Point getPointMin(){
        return this.pointMin;
    }

    public int[][] getMatrixBlackAndWhite(){
        return this.matrixBlackWhite;
    }

    public ArrayList<Point> getResultThinningList(){
        return this.resultThinningList;
    }

    public int getWidth(){
        return this.width;
    }
    
    public int getHeight(){
        return this.height;
    }

    public void copyToMatrix(int matrix[][]){
        for(int i =0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                matrix[i][j] = this.matrixBlackWhite[i][j];
            }
        }
    }
 
    public boolean isFoundListOfPoint(ArrayList<Point> pList, int x, int y){
        for (Point p: pList){
            if(p.x == x && p.y == y){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Point> getIntersectPoint(){
        ArrayList<Point> pList = new ArrayList<>();
        for (Point p: this.resultThinningList){
            if (isValidIntersectPoint(p.x, p.y)){
                pList.add(new Point(p));
            }
        }
        return pList;
    }

    public boolean isNeighboorValidIntersect(int xCenter, int yCenter){
        boolean isValid = false;
        for(int i=0;i < this.iterationDirections.length - 1 ;i++){
            if( (xCenter + this.iterationDirections[i][1]) >= 0 && (xCenter + this.iterationDirections[i][1]) < this.height && (yCenter + this.iterationDirections[i][0]) >= 0 && (yCenter + this.iterationDirections[i][0]) < this.width){
                if (isValidIntersectPoint(xCenter + this.iterationDirections[i][1], yCenter + this.iterationDirections[i][0])){
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }

    public boolean isValidIntersectPoint(int x, int y){
        if(this.matrixBlackWhite[x][y] == 0){
            return false;
        }

        boolean isValid = false;
        int centerX = 1;
        int centerY = 1;
        for(int idx = 0;idx < this.MAX_INTERSECT_PATTERN; idx++){
            isValid = true;
            for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                if( (x + this.iterationDirections[i][1]) >= 0 && (x + this.iterationDirections[i][1]) < this.height && (y + this.iterationDirections[i][0]) >= 0 && (y + this.iterationDirections[i][0]) < this.width){
                    if(this.intersectPoinPattern[idx][centerX + this.iterationDirections[i][1]][centerY + this.iterationDirections[i][0]] > -1){
                        if(this.intersectPoinPattern[idx][centerX + this.iterationDirections[i][1]][centerY + this.iterationDirections[i][0]] != this.matrixBlackWhite[x + this.iterationDirections[i][1]][y + this.iterationDirections[i][0]]){
                            isValid = false;
                            break;
                        }
                    }
                } 
            }
            if (isValid){
                break;
            }
        }
        return isValid;
    }

    public boolean isValidEndPoint(int x, int y){
        if(this.matrixBlackWhite[x][y] == 0){
            return false;
        }

        boolean isValid = false;
        int centerX = 1;
        int centerY = 1;
        for(int idx = 0;idx < this.MAX_ENDPOINT_PATTERN; idx++){
            isValid = true;
            for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                if( (x + this.iterationDirections[i][1]) >= 0 && (x + this.iterationDirections[i][1]) < this.height && (y + this.iterationDirections[i][0]) >= 0 && (y + this.iterationDirections[i][0]) < this.width){
                    if(this.endPointPattern[idx][centerX + this.iterationDirections[i][1]][centerY + this.iterationDirections[i][0]] > -1){
                        if(this.endPointPattern[idx][centerX + this.iterationDirections[i][1]][centerY + this.iterationDirections[i][0]] != this.matrixBlackWhite[x + this.iterationDirections[i][1]][y + this.iterationDirections[i][0]]){
                            isValid = false;
                            break;
                        }
                    }
                } 
            }
            if (isValid){
                break;
            }
        }
        return isValid;
    } 

    public boolean isValidSisaPoint(int x, int y){
        if(this.matrixBlackWhite[x][y] == 0){
            return false;
        }

        boolean isValid = false;
        int centerX = 1;
        int centerY = 1;
        for(int idx = 0;idx < this.MAX_SISA; idx++){
            isValid = true;
            for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                if( (x + this.iterationDirections[i][1]) >= 0 && (x + this.iterationDirections[i][1]) < this.height && (y + this.iterationDirections[i][0]) >= 0 && (y + this.iterationDirections[i][0]) < this.width){
                    if(this.sisaPointPattern[idx][centerX + this.iterationDirections[i][1]][centerY + this.iterationDirections[i][0]] > -1){
                        if(this.sisaPointPattern[idx][centerX + this.iterationDirections[i][1]][centerY + this.iterationDirections[i][0]] != this.matrixBlackWhite[x + this.iterationDirections[i][1]][y + this.iterationDirections[i][0]]){
                            isValid = false;
                            break;
                        }
                    }
                } 
            }
            if (isValid){
                break;
            }
        }
        return isValid;
    }

    public int numNeighbors(int h, int w){
        int count = 0;
        // System.out.println("num neighbors");
        for(int i=0;i < this.iterationDirections.length - 1 ;i++){
            // System.out.println(this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]]);
            if (this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]] == 1){
                count++;
            }
        }
        return count;
    }

    public int numTransitions(int h, int w){
        int count = 0;
        for(int i=0;i < this.iterationDirections.length - 1 ;i++){
            if (this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]] == 0){
                if (this.matrixBlackWhite[h + this.iterationDirections[i + 1][1]][w + this.iterationDirections[i + 1][0]] == 1){
                    count++;
                }
            }
        }
        return count;
    }

    // public boolean atLeastOneIsZero(int h, int w, int step){
    //     int count = 0;
    //     int[][] currentGroup = this.nbrGroups[step];
    //     for(int i=0;i<2;i++){
    //         for(int j=0;j< currentGroup[i].length;j++){
    //             int[] nbr = this.iterationDirections[currentGroup[i][j]];
    //             if (this.matrixBlackWhite[h + nbr[1]][w + nbr[0]] == 0){
    //                 count++;
    //                 break;
    //             }
    //         }
    //     }
    //     return count > 1;
    // }

    public void setThinningList(){
        this.resultThinningList.clear();
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if (this.matrixBlackWhite[i][j] == 1){
                    this.resultThinningList.add(new Point(i, j));
                }
            }
        }
    }

    public void getBoundPoints(){
        // ArrayList<Point> pList = getEndPoint();
        int i = 0;
        int xmax = 0, xmin = 0,ymax = 0,ymin = 0;
        for (Point p: this.resultThinningList){
            i++;
            if (i == 1){
                xmax = p.x;
                xmin = p.x;
                ymax = p.y;
                ymin = p.y;
            }else{
                if(p.x > xmax){
                    xmax = p.x;
                }
                if(p.x < xmin){
                    xmin = p.x;
                }
                if(p.y > ymax){
                    ymax = p.y;
                }
                if(p.y < ymin){
                    ymin = p.y;
                }
            }
            // System.out.println("(" + p.x + ", " + p.y + ")");
        }

        // System.out.println("-----------------------------  Bounded Point  --------------------------");
        this.pointMax.setLocation(xmax + 1, ymax + 1);
        // System.out.println(xmax + " " + ymax);
        this.pointMin.setLocation(xmin - 1, ymin - 1);
        // System.out.println(xmin + " " + ymin);
    }
    

    public int getAreaQuadran(Point p){
        int h = p.x;
        int w = p.y;
        // 1 .. 4
        int area = 0;
        if ((h > this.pointMin.x) && (h <= ((this.pointMax.x + this.pointMin.x) / 2)) && (w > this.pointMin.y) && (w < (this.pointMax.y + this.pointMin.y) / 2)){
            area = 1;
        }else if(h > this.pointMin.x && h < ((this.pointMax.x + this.pointMin.x) / 2) && w >= ((this.pointMax.y + this.pointMin.y) / 2) && w < this.pointMax.y){
            area = 2;
        }else if(h >= ((this.pointMax.x + this.pointMin.x) / 2) && h < this.pointMax.x && w > ((this.pointMax.y + this.pointMin.y) / 2) && w < this.pointMax.y){
            area = 3;
        }else if(h > ((this.pointMax.x + this.pointMin.x) / 2) && h < this.pointMax.x && w > this.pointMin.y && (w <= (this.pointMax.y + this.pointMin.y) / 2)){
            area = 4;
        }
        // else origin
        return area;
    }

    public int getDirection(Point p){
        int h = p.x;
        int w = p.y;
        int dir = 0;
        for(int i=0;i < this.iterationDirections.length - 1 ;i++){
            // System.out.println(this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]]);
            if (this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]] == 1){
                dir = i + 1;
            }
        }
        return dir;
    }

    public double getGradien(Point p1,Point p2){
        return (double)(p2.y - p1.y) / (double)(p2.x - p1.x); 
    }

    public ArrayList<Point> getListDeleteFromEndPoint(int x, int y){
        // int distance = 0;
        // x,y adalah end point
        ArrayList<Point> chainCodePoint = new ArrayList<Point>();
        int dir = MAX_DIRECTION - 1;
        // this.matrixBlackWhite[x][y] = 0;
        int xBegin = x;
        int yBegin = y;
        int xPrev = xBegin;
        int yPrev = yBegin;

        chainCodePoint.add(new Point(xBegin, yBegin));
        // int from;
        // ArrayList<int> chainCode = new ArrayList();
        while(!isNeighboorValidIntersect(xBegin, yBegin) ){
            int from = 0;
            xPrev = xBegin;
            yPrev = yBegin;
            if (dir % 2 == 0){
                from = (dir + 7) % MAX_DIRECTION;
            }else{
                from = (dir + 6) % MAX_DIRECTION;
            }
            boolean found = false;
            // System.out.println("LOOP");
            for(int i=0;i<MAX_DIRECTION;i++){
                // System.out.println( "dir = " + from);
                if (from == 0){
                    xBegin = xPrev;
                    yBegin = yPrev + 1;
                }else if (from == 1){
                    xBegin = xPrev - 1;
                    yBegin = yPrev + 1;
                }else if (from == 2){
                    xBegin = xPrev - 1;
                    yBegin = yPrev;
                }else if (from == 3){
                    xBegin = xPrev - 1;
                    yBegin = yPrev - 1;
                }else if (from == 4){
                    xBegin = xPrev;
                    yBegin = yPrev - 1;
                }else if (from == 5){
                    xBegin = xPrev + 1;
                    yBegin = yPrev - 1;
                }else if (from == 6){
                    xBegin = xPrev + 1;
                    yBegin = yPrev;
                }else if (from == 7){
                    xBegin = xPrev + 1;
                    yBegin = yPrev + 1;
                }

                if ((xBegin >= 0 && xBegin < this.height) && (yBegin >= 0 && yBegin < this.width)){
                   
                    if (this.matrixBlackWhite[xBegin][yBegin] == 1){
                        found = true;
                        chainCodePoint.add(new Point(xBegin, yBegin));
                    }
                }

                if (found){
                    break;
                }else{
                    from = (from + 1) % MAX_DIRECTION;                    
                }
            }

            if(found){
                dir = from;
            }
            if(isValidEndPoint(xBegin, yBegin) || numNeighbors(xBegin, yBegin) == 1) break; 
        }
        return chainCodePoint;
    }

    public void postProcessingThreshold(int tholdPrecentage){
        // thold percentage from longer width or height
        System.out.println("Post Processing using threshold process (Only 1 shape skeleton)");   
        // getBoundPoints();
        // int sizeFromBounded = (this.pointMax.x - this.pointMin.x) * (this.pointMax.x - this.pointMin.x) + (this.pointMax.y - this.pointMin.y) * (this.pointMax.y - this.pointMin.y);
        // double sizeFromBounded2 = Math.sqrt((double) sizeFromBounded);
        int size = this.resultThinningList.size();
        System.out.println("size before post processing : " + size);
        int tholdSize = (int) size * tholdPrecentage / 100;
        System.out.println("thold size : " + tholdSize);

        printBoundedPoint();
        // if (this.height > this.width){
        //     tholdSize = (int)this.height*thold / (int) 100;
        // }else{
        //     tholdSize = (int)this.width*thold / (int) 100;
        // }
        ArrayList<Point> delPoint = new ArrayList<>();
        ArrayList<Point> pList = new ArrayList<>();
        boolean isChanged = true;
        while(isChanged){
            isChanged = false; 
            delPoint.clear();
            pList.clear();
            pList.addAll(getEndPoint());
            System.out.println("size list end point now : " + pList.size());
            printAllEndPoint();
            if (pList.size() > 2){
                for(Point p : pList){
                    int d = getDistanceFromPattern(p.x, p.y);
                    System.out.println("d : " + d);
                    if (d < tholdSize){
                        System.out.println("Deleted");
                        isChanged = true;
                        delPoint.addAll(getListDeleteFromEndPoint(p.x, p.y));
                        // setThinningList();
                    }
                }
            }
            if (delPoint.size() > 0){
                deletePointFromList(delPoint);
                deletePointFromList(getDeleteSisaPoint(pList, this.resultThinningList));
                deletePointFromList(getDeleteEndPointPattern(this.resultThinningList));
                setThinningList();
                getBoundPoints();
                printBoundedPoint();
            }
            System.out.println();
        }
        size = this.resultThinningList.size();
        System.out.println("size after post processing : " + size);  
        System.out.println();
    }

    public ArrayList<Point> getDeleteEndPointPattern(ArrayList<Point> pListThinning){
        ArrayList<Point> delPoint = new ArrayList<>();
        for(Point p : pListThinning){
            if(this.matrixBlackWhite[p.x][p.y] == 1){
                if(isValidEndPoint(p.x, p.y)){
                    delPoint.add(p);
                }
            }
        }
        return delPoint;
    }

    public ArrayList<Point> getDeleteSisaPoint(ArrayList<Point> pListEndPoint, ArrayList<Point> pListThinning){
        ArrayList<Point> delPoint = new ArrayList<>();
        for(Point p : pListThinning){
            if(this.matrixBlackWhite[p.x][p.y] == 1){
                if(!isFoundListOfPoint(pListEndPoint, p.x, p.y)){
                    if(numNeighbors(p.x, p.y) == 0 || isValidSisaPoint(p.x, p.y)){
                        delPoint.add(p);
                    }
                }
            }
        }
        return delPoint;
    }

    public void deletePointFromList(ArrayList<Point> delPoint){
        for(Point p : delPoint){
            this.matrixBlackWhite[p.x][p.y] = 0;
        }
    }

    public int getDistanceFromPattern(int x, int y){
        int distance = 0;
        int xBegin = x;
        int yBegin = y;
        int xPrev = xBegin;
        int yPrev = yBegin;
        int dir = MAX_DIRECTION - 1;

        // int from;
        // ArrayList<int> chainCode = new ArrayList();
        while( !isNeighboorValidIntersect(xBegin, yBegin)){
            // System.out.println("masuk");
            int from = 0;
            xPrev = xBegin;
            yPrev = yBegin;
            if (dir % 2 == 0){
                from = (dir + 7) % MAX_DIRECTION;
            }else{
                from = (dir + 6) % MAX_DIRECTION;
            }
            boolean found = false;
            // System.out.println("LOOP");
            for(int i=0;i<MAX_DIRECTION;i++){
                // System.out.println( "dir = " + from);
                if (from == 0){
                    xBegin = xPrev;
                    yBegin = yPrev + 1;
                }else if (from == 1){
                    xBegin = xPrev - 1;
                    yBegin = yPrev + 1;
                }else if (from == 2){
                    xBegin = xPrev - 1;
                    yBegin = yPrev;
                }else if (from == 3){
                    xBegin = xPrev - 1;
                    yBegin = yPrev - 1;
                }else if (from == 4){
                    xBegin = xPrev;
                    yBegin = yPrev - 1;
                }else if (from == 5){
                    xBegin = xPrev + 1;
                    yBegin = yPrev - 1;
                }else if (from == 6){
                    xBegin = xPrev + 1;
                    yBegin = yPrev;
                }else if (from == 7){
                    xBegin = xPrev + 1;
                    yBegin = yPrev + 1;
                }

                if ((xBegin >= 0 && xBegin < this.height) && (yBegin >= 0 && yBegin < this.width)){
                    // System.out.println(xBegin + " " + yBegin);
                    if (this.matrixBlackWhite[xBegin][yBegin] == 1){
                        // System.out.println("masuk");
                        found = true;
                        // this.matrixBlackWhite[xBegin][yBegin] = -1;
                        // this.chainCodePoint.add(new Point(xBegin, yBegin));
                    }
                }

                if (found){
                    break;
                }else{
                    from = (from + 1) % MAX_DIRECTION;                    
                }
            
            }

            if(found){
                dir = from;
                // this.chainCode.add(dir);
                distance++;
            } 

            // buat test
            // if (isValidIntersectPoint(xBegin, yBegin)){
            //     // this.matrixBlackWhite[xBegin][yBegin] = -1;
            //     System.out.println("intersect = " + xBegin + " , " + yBegin);
            // }
            if(isValidEndPoint(xBegin, yBegin) || numNeighbors(xBegin, yBegin) == 1) break;
        }
        return distance;
    }

    public ArrayList<Integer> getChainCode(Point p){
        ArrayList<Integer> chainCode = new ArrayList<Integer>();

        int xBegin = p.x;
        int yBegin = p.y;
        int dir = MAX_DIRECTION - 1;

        int xPrev = xBegin;
        int yPrev = yBegin;
        // init
        int from = 0;
        if (dir % 2 == 0){
            from = (dir + 7) % MAX_DIRECTION;
        }else{
            from = (dir + 6) % MAX_DIRECTION;
        }
        boolean found = false;   
        // System.out.println("LOOP");
        for(int i=0;i<MAX_DIRECTION;i++){
            if (from == 0){
                xBegin = xPrev;
                yBegin = yPrev + 1;
            }else if (from == 1){
                xBegin = xPrev - 1;
                yBegin = yPrev + 1;
            }else if (from == 2){
                xBegin = xPrev - 1;
                yBegin = yPrev;
            }else if (from == 3){
                xBegin = xPrev - 1;
                yBegin = yPrev - 1;
            }else if (from == 4){
                xBegin = xPrev;
                yBegin = yPrev - 1;
            }else if (from == 5){
                xBegin = xPrev + 1;
                yBegin = yPrev - 1;
            }else if (from == 6){
                xBegin = xPrev + 1;
                yBegin = yPrev;
            }else if (from == 7){
                xBegin = xPrev + 1;
                yBegin = yPrev + 1;
            }

            if ((xBegin >= 0 && xBegin < this.height) && (yBegin >= 0 && yBegin < this.width)){
                // System.out.println(xBegin + " " + yBegin);
                if (this.matrixBlackWhite[xBegin][yBegin] == 1){
                    // System.out.println("masuk");
                    found = true;
                    // in case multiple object make it different method
                    // this.matrixBlackWhite[xBegin][yBegin] = -1;
                    // this.chainCodePoint.add(new Point(xBegin, yBegin));
                }
            }

            if (found){
                break;
            }else{
                from = (from + 1) % MAX_DIRECTION; // counter                   
            }
        }
        dir = from;
        chainCode.add(dir);            
        // loop
        while(xBegin != p.x || yBegin != p.y){
            from = 0;
            
            xPrev = xBegin;
            yPrev = yBegin;
            if (dir % 2 == 0){
                from = (dir + 7) % MAX_DIRECTION;
            }else{
                from = (dir + 6) % MAX_DIRECTION;
            }
            found = false;
            // System.out.println("LOOP");
            for(int i=0;i<MAX_DIRECTION;i++){
                // System.out.println( "dir = " + from);
                if (from == 0){
                    xBegin = xPrev;
                    yBegin = yPrev + 1;
                }else if (from == 1){
                    xBegin = xPrev - 1;
                    yBegin = yPrev + 1;
                }else if (from == 2){
                    xBegin = xPrev - 1;
                    yBegin = yPrev;
                }else if (from == 3){
                    xBegin = xPrev - 1;
                    yBegin = yPrev - 1;
                }else if (from == 4){
                    xBegin = xPrev;
                    yBegin = yPrev - 1;
                }else if (from == 5){
                    xBegin = xPrev + 1;
                    yBegin = yPrev - 1;
                }else if (from == 6){
                    xBegin = xPrev + 1;
                    yBegin = yPrev;
                }else if (from == 7){
                    xBegin = xPrev + 1;
                    yBegin = yPrev + 1;
                }

                if ((xBegin >= 0 && xBegin < this.height) && (yBegin >= 0 && yBegin < this.width)){
                    // System.out.println(xBegin + " " + yBegin);
                    if (this.matrixBlackWhite[xBegin][yBegin] == 1){
                        // System.out.println("masuk");
                        found = true;
                        // this.matrixBlackWhite[xBegin][yBegin] = -1;
                        // this.chainCodePoint.add(new Point(xBegin, yBegin));
                    }
                }

                if (found){
                    break;
                }else{
                    from = (from + 1) % MAX_DIRECTION;                    
                }
            }
            // gak mungkin not found
            int last = chainCode.size() - 1;
            if (!found || (Math.abs(chainCode.get(last) - from) == 4)){
                break; //stop
            }else{
                dir = from;
                chainCode.add(dir);
            }            
        }
        return chainCode;
    }

    public ArrayList<Point> getEndPoint(){
        ArrayList<Point> pList = new ArrayList<>();
        for (Point p: this.resultThinningList){
            // System.out.println("(" + p.x + ", " + p.y + ")");
            if (numNeighbors(p.x, p.y) == 1 || isValidEndPoint(p.x, p.y)){
                // System.out.println("(" + p.x + ", " + p.y + ")");
                pList.add(new Point(p));
            }
        }
        return pList;
    }

    public void printBoundedPoint(){
        System.out.println();
        System.out.println("Bounded Point");
        System.out.println("max : " + this.pointMax.x + ", " + this.pointMax.y);
        System.out.println("min : " + this.pointMin.x + ", " + this.pointMin.y);
        System.out.println();
    }

    public int getCircle(){
        // System.out.println("Circle Process");
        ArrayList<Point> pListResultSaved = new ArrayList<>();
        ArrayList<Point> pListEndPoint = getEndPoint();
        ArrayList<Point> delPoint = new ArrayList<>();
        
        pListResultSaved.addAll(this.resultThinningList);

        while(pListEndPoint.size() > 0){
            for (Point p : pListEndPoint){
                delPoint.addAll(getListDeleteFromEndPoint(p.x, p.y));
            }
            if (delPoint.size() > 0){
                deletePointFromList(delPoint);
                deletePointFromList(getDeleteSisaPoint(pListEndPoint, this.resultThinningList));
                deletePointFromList(getDeleteEndPointPattern(this.resultThinningList));
                setThinningList();
                getBoundPoints();
            }
            pListEndPoint = getEndPoint();
            delPoint.clear();
        }
        
        if (this.resultThinningList.size() == 0){
            initMatrixFromList(pListResultSaved);
            setThinningList();
            getBoundPoints();
            return 0;
        }

        // System.out.println("End Point sementara : " + pListEndPoint.size());
        // int hole = 0;
        ArrayList<Point> pListIntersect = getIntersectPoint();
        // System.out.println("Intersect Point sementara : " + pListIntersect.size());
        
        int count = 1;
        for(Point p : pListIntersect){
            // System.out.println("intersect process");
            if (isValidIntersectPoint(p.x, p.y)){
                delPoint = getListDeleteFromIntersectPoint(p.x, p.y);
                // System.out.println("Deleted sebanyak : " + delPoint.size()); 
                deletePointFromList(delPoint);
                deletePointFromList(getDeleteSisaPoint(pListEndPoint, this.resultThinningList));
                deletePointFromList(getDeleteEndPointPattern(this.resultThinningList));
                setThinningList();
                getBoundPoints();
                delPoint.clear();
                pListEndPoint = getEndPoint();
                
                
                
                // if (delPoint.size() > 0){
                // System.out.println("end point setelah deleted circle : " + pListEndPoint.size());
                while(pListEndPoint.size() > 0){
                    for (Point pp : pListEndPoint){
                        delPoint.addAll(getListDeleteFromEndPoint(pp.x, pp.y));
                    }
                    if (delPoint.size() > 0){
                        deletePointFromList(delPoint);
                        deletePointFromList(getDeleteSisaPoint(pListEndPoint, this.resultThinningList));
                        deletePointFromList(getDeleteEndPointPattern(this.resultThinningList));
                        setThinningList();
                        getBoundPoints();
                    }
                    pListEndPoint = getEndPoint();
                    // System.out.println("end point setelah deleted circle : " + pListEndPoint.size());

                    delPoint.clear();
                }
                // }
                // cek
                // toFileAfterThinning("processCircle" + count);
                count++;
            }
        }
        // while (this.resultThinningList.size() > 0){
        //     pListIntersect = getIntersectPoint();
        // }
        initMatrixFromList(pListResultSaved);
        setThinningList();
        getBoundPoints();
        return count;
    }

    public ArrayList<Point> getListDeleteFromIntersectPoint(int x, int y){
        ArrayList<Point> chainCodePoint = new ArrayList<Point>();
        int dir = MAX_DIRECTION - 1;
        // this.matrixBlackWhite[x][y] = 0;
        int xBegin = x;
        int yBegin = y;
        int xPrev = xBegin;
        int yPrev = yBegin;

        // chainCodePoint.add(new Point(xBegin, yBegin));
        // int from;
        // ArrayList<int> chainCode = new ArrayList();
        int iDis = 0;
        while(!isNeighboorValidIntersect(xBegin, yBegin) || iDis < 2){
            iDis++;
            int from = 0;
            xPrev = xBegin;
            yPrev = yBegin;
            if (dir % 2 == 0){
                from = (dir + 7) % MAX_DIRECTION;
            }else{
                from = (dir + 6) % MAX_DIRECTION;
            }
            boolean found = false;
            // System.out.println("LOOP");
            for(int i=0;i<MAX_DIRECTION;i++){
                // System.out.println( "dir = " + from);
                if (from == 0){
                    xBegin = xPrev;
                    yBegin = yPrev + 1;
                }else if (from == 1){
                    xBegin = xPrev - 1;
                    yBegin = yPrev + 1;
                }else if (from == 2){
                    xBegin = xPrev - 1;
                    yBegin = yPrev;
                }else if (from == 3){
                    xBegin = xPrev - 1;
                    yBegin = yPrev - 1;
                }else if (from == 4){
                    xBegin = xPrev;
                    yBegin = yPrev - 1;
                }else if (from == 5){
                    xBegin = xPrev + 1;
                    yBegin = yPrev - 1;
                }else if (from == 6){
                    xBegin = xPrev + 1;
                    yBegin = yPrev;
                }else if (from == 7){
                    xBegin = xPrev + 1;
                    yBegin = yPrev + 1;
                }

                if ((xBegin >= 0 && xBegin < this.height) && (yBegin >= 0 && yBegin < this.width)){
                   
                    if (this.matrixBlackWhite[xBegin][yBegin] == 1){
                        found = true;
                        chainCodePoint.add(new Point(xBegin, yBegin));
                    }
                }

                if (found){
                    break;
                }else{
                    from = (from + 1) % MAX_DIRECTION;                    
                }
            }

            if(found){
                dir = from;
            }
             
        }
        return chainCodePoint;
    }

    public int getValleyFromRight(){
        int minDistanceValley = 5;
        int i = 0;
        int count = 0;
        int distance = 0;
        int dir = 0;
        int xBegin = -1;
        int yBegin = -1;
        while(i < this.height){
            
            int j = this.width - 1;
            boolean isFound = false;
            while(j >= 0){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
                j--;
            }
            if (isFound){
                if (xBegin == -1 && yBegin == -1){
                    xBegin = i;
                    yBegin = j;
                    i++;
                }else{
                    if (yBegin == j && distance == 0){
                        i++;
                    }else if(yBegin == j && distance > 0){
                        distance++;
                        i++;
                    }else if(j < yBegin && dir == 0){
                        distance++;
                        i++;
                    }else if(j > yBegin && dir == 0){
                        dir = 1;
                        distance++;
                        i++;
                    }else if(j < yBegin && dir == 1){
                        dir = 0;
                        if (distance > minDistanceValley){
                            count++;
                        }
                        distance = 0;
                        i++;
                    }else if(j > yBegin && dir == 1){
                        i++;
                        distance++;
                    }
                    xBegin = i;
                    yBegin = j;
                    
                }  
            }else{
                i++;
                if (distance > minDistanceValley && dir == 1){
                    count++;
                }
                dir = 0;
                distance = 0;
                xBegin = -1;
                yBegin = -1;
            }
        }
        return count;
    }

    public int getValleyFromLeft(){
        int minDistanceValley = 5;
        int i = 0;
        int count = 0;
        int distance = 0;
        int dir = 1;
        int xBegin = -1;
        int yBegin = -1;
        while(i < this.height){
            
            int j = 0;
            boolean isFound = false;
            while(j < this.width){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
                j++;
            }
            if (isFound){
                if (xBegin == -1 && yBegin == -1){
                    xBegin = i;
                    yBegin = j;
                    i++;
                }else{
                    if (yBegin == j && distance == 0){
                        i++;
                    }else if(yBegin == j && distance > 0){
                        distance++;
                        i++;
                    }else if(j < yBegin && dir == 0){
                        distance++;
                        i++;
                    }else if(j > yBegin && dir == 0){
                        dir = 1;
                        if (distance > minDistanceValley){
                            count++;
                        }
                        distance = 0;
                        i++;
                    }else if(j > yBegin && dir == 1){
                        dir = 0;    
                        distance = 0;
                        i++;
                    }else if(j < yBegin && dir == 1){
                        i++;
                        distance++;
                    }
                    xBegin = i;
                    yBegin = j;
                    
                }  
            }else{
                i++;
                dir = 1;
                if (distance > minDistanceValley && dir == 0){
                    count++;
                }
                xBegin = -1;
                yBegin = -1;
                distance = 0;
                
            }
        }
        return count;
    }

    public int getValleyFromDown(){
        int minDistanceValley = 5;
        int j = 0;
        int count = 0;
        int distance = 0;
        int dir = 0;
        int xBegin = -1;
        int yBegin = -1;
        while(j < this.width){
            
            int i = this.height - 1;
            boolean isFound = false;
            while(i >= 0){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
                i--;
            }

            if (isFound){
                // System.out.print("i j -> ( " + i + ", " + j + " )");
                if (xBegin == -1 && yBegin == -1){
                    
                    j++;
                    // System.out.println("Begin");
                }else{
                    if (xBegin == i && distance == 0){
                        // System.out.println("-");
                        j++;
                    }else if(xBegin == i && distance > 0){
                        // System.out.println("-");
                        distance++;
                        j++;
                    }else if(i < xBegin && dir == 0){
                        // System.out.println("<");
                        distance++;
                        j++;
                    }else if(i > xBegin && dir == 0){
                        // System.out.println(">");
                        dir = 1;
                        distance++;
                        j++;
                    }else if(i < xBegin && dir == 1){
                        // System.out.println("<");
                        dir = 0;
                        if (distance > minDistanceValley){
                            count++;
                            // System.out.println("counted");
                        }
                        distance = 0;
                        j++;
                    }else if(i > xBegin && dir == 1){
                        // System.out.println(">");                        
                        j++;
                        distance++;
                    }
                    
                }  
                xBegin = i;
                yBegin = j;
            }else{
                j++;
                if (distance > minDistanceValley && dir == 1){
                    count++;
                }
                dir = 0;
                distance = 0;
                xBegin = -1;
                yBegin = -1;
            }
        }        
        return count;
    }

    public int getValleyFromUp(){
        int minDistanceValley = 5;
        int j = 0;
        int count = 0;
        int distance = 0;
        int dir = 1;
        int xBegin = -1;
        int yBegin = -1;
        while(j < this.width){
            
            int i = 0;
            boolean isFound = false;
            while(i < this.height){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
                i++;
            }

            if (isFound){
                if (xBegin == -1 && yBegin == -1){
                    xBegin = i;
                    yBegin = j;
                    j++;
                }else{
                    if (xBegin == i && distance == 0){
                        j++;
                    }else if(xBegin == i && distance > 0){
                        distance++;
                        j++;
                    }else if(i < xBegin && dir == 0){
                        distance++;
                        j++;
                    }else if(i > xBegin && dir == 0){
                        dir = 1;
                        if (distance > minDistanceValley){
                            count++;
                        }
                        distance = 0;
                        j++;
                    }else if(i < xBegin && dir == 1){
                        dir = 1;
                        distance++;
                        j++;
                    }else if(i > xBegin && dir == 1){
                        j++;
                        distance++;
                    }
                    xBegin = i;
                    yBegin = j;
                    
                }  
            }else{
                j++;
                if (distance > minDistanceValley && dir == 0){
                    count++;
                }
                dir = 1;
                distance = 0;
                xBegin = -1;
                yBegin = -1;
            }
        }
        return count;
    }

    public void printAllDistanceEndPoint(){
        ArrayList<Point> pList = getEndPoint();
        for(int i=0;i<pList.size();i++){
            System.out.print("(" + pList.get(i).x + ", " + pList.get(i).y + ") : ");
            Point p = pList.get(i);
            int n = getDistanceFromPattern(p.x, p.y);
            System.out.println(n);
        }
        
        // delete soon
        // this.matrixBlackWhite[64][205] = -1;
    }

    public void printAllEndPoint(){
        ArrayList<Point> pListEndPoint = getEndPoint();
        for(Point p:pListEndPoint){
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }

    public void printAllIntersectPoint(){
        ArrayList<Point> pListIntersect = getIntersectPoint();
        for(Point p:pListIntersect){
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }

    public void toFileAfterThinning(String file){
        try{
            Writer writer = new BufferedWriter(new FileWriter(file));
            for(int i =0;i<this.height;i++){
                for(int j=0;j<this.width;j++){
    //                Log.d("isi matrix : ", " " + matrix[i][j]);
                    // this.matrixBlackWhite[i][j] = matrix[i][j];
                    writer.write(this.matrixBlackWhite[i][j] + "");
                }
                writer.write("\n");
            }
            if (writer != null) {
                writer.close();
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}