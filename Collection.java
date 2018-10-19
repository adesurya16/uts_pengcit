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

class Collection{
    // private static final int MAX_DIRECTION = 8;
    private int matrixBlackWhite[][];
    private int width;
    private int height;
    private ArrayList<Point> resultThinningList;

    private final int THRESHOLD_COMMON = 10;
    // private ArrayList<Integer> chainCode;
    private Point pointMax;
    private Point pointMin;
    private ArrayList<Skeleton> objectsSkeletons;
    
    public static Color pix[][];
    public static int pix01[][];

    private final int iterationDirections[][] = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    private final  int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};

    private ArrayList<Point> toZerolist;

    public Collection(int matrixBlackWhite[][], int height, int width){
        this.width = width;
        this.height = height;

        this.matrixBlackWhite = new int[height][];
        for (int i=0;i<height;i++){
            this.matrixBlackWhite[i] = new int[width];
        }

//        bacaFile();
        for(int i =0;i<height;i++){
            for(int j=0;j<width;j++){
//                Log.d("isi matrix : ", " " + matrix[i][j]);
                this.matrixBlackWhite[i][j] = matrix[i][j];
            }
        }
        this.toZerolist = new ArrayList<>();
        // this.resultThinningList = new ArrayList<>();
        this.pointMax = new Point();
        this.pointMin = new Point();
        this.objectsSkeletons = new ArrayList<>();
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

    public void deletePointFromList(ArrayList<Point> delPoint){
        for(Point p : delPoint){
            this.matrixBlackWhite[p.x][p.y] = 0;
        }
    }

    public int[][] copyToMatrix(int matrix[][], int height, int width){
        int[][] matrix2 = new int[height][];
        for (int i=0;i<height;i++){
            matrix2[i] = new int[width];
            for(int j=0;j<width;j++){
                matrix2[i][j] = matrix[i][j];
            }
        }
        return matrix2;
    }

    public void thinImage(){
        boolean firstStep = true;
        boolean changed = false;
        // int ii = 0;
        do{
            // ii++;
            // System.out.println("LOOP - " + ii);
            changed = false;
            firstStep = !firstStep;

            for(int i = 1;i < height - 1; i++){
                for(int j = 1;j < width - 1;j++){

                    if (this.matrixBlackWhite[i][j] == 0){
                        continue;
                    }

                    int nn = numNeighbors(i, j);
                    if (nn < 2 || nn > 6){
                        continue;
                    }

                    if (numTransitions(i, j) != 1){
                        continue;
                    }

                    if (!atLeastOneIsZero(i, j, firstStep ? 0 : 1)){
                        continue;
                    }

                    this.toZerolist.add(new Point(i, j));
                    changed = true;
                }
            }

            for(Point p: this.toZerolist){
                this.matrixBlackWhite[p.x][p.y] = 0;
            }

            this.toZerolist.clear();
            // printFile();
        }while(changed || firstStep);
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

    public boolean atLeastOneIsZero(int h, int w, int step){
        int count = 0;
        int[][] currentGroup = this.nbrGroups[step];
        for(int i=0;i<2;i++){
            for(int j=0;j< currentGroup[i].length;j++){
                int[] nbr = this.iterationDirections[currentGroup[i][j]];
                if (this.matrixBlackWhite[h + nbr[1]][w + nbr[0]] == 0){
                    count++;
                    break;
                }
            }
        }
        return count > 1;
    }

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
        int xmax = 0,xmin = 0,ymax = 0,ymin = 0;
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

    // parse object
    public void getSkeletons(){
        // after thinning JANGAN LUPA INI
        // after set thinning list JANGAN LUPA INI
        this.objectsSkeletons.clear();
        for(Point p : this.resultThinningList){
            int x = p.x;
            int y = p.y;
            ArrayList<Point> toDelete = new ArrayList<Point>();
            if (this.matrixBlackWhite[x][y] == 1){
                getDeletedPointSkeleton(toDelete, x, y);
                this.objectsSkeletons.add(new Skeleton(toDelete, this.height, this.width));
            }
            // deletePointFromList(toDelete);
        }
        // back to first condition matrix black and white
        initMatrixFromList();
    }

    public void getDeletedPointSkeleton(ArrayList<Point> p ,int x ,int y){
        if (this.matrixBlackWhite[x][y] == 1){
            p.add(new Point(x, y));
            this.matrixBlackWhite[x][y] = 0;
            for(int i=0;i < this.iterationDirections.length - 1 ;i++){
                int dx = x + iterationDirections[i][1];
                int dy = y + iterationDirections[i][0];
                if ((dx >= 0 && dx < this.height) && (dy >= 0 && dx < this.width)){
                    getDeletedPointSkeleton(p, dx, dy);
                }
            }
        }
    }

    // feature to recognize
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

    public void postProcessingThresholdAll(){
        for (Skeleton s: this.objectsSkeletons){
            s.postProcessingThreshold(THRESHOLD_COMMON);
        }
    }

    public static void readImage(BufferedImage img, Writer writer){
        
        int width = img.getHeight();
        int height = img.getWidth();

        pix = new Color[height][];
        for(int i = 0;i < height;i++){
            
            pix[i] = new Color[width];

            for(int j = 0;j < width;j++){
                Color c = new Color(img.getRGB(j, i));
                pix[i][j] = c;
            }
        }
        
        try{
            pix01 = new int[height][];
            for(int i = 0;i < height;i++){
                pix01[i] = new int[width];
                for(int j = 0;j < width;j++){
                    // Color c = new Color(img.getRGB(i, j));
                    // pix[i][j] = c;
                    Color c = pix[i][j];
                    
                    if (c.getRed() == 255 && c.getGreen() == 255 && c.getBlue() == 255){
                        // System.out.print(0);
                        writer.write("0");
                        pix01[i][j] = 0;
                    }else{
                        // System.out.print(1);
                        writer.write("1");
                        pix01[i][j] = 1;
                    }
                }
                writer.write("\n");
                // System.out.println();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        
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
            
            j = this.width - 1;
            boolean isFound = false;
            while(j >= 0){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
            }
            if (isFound){
                if (xBegin == -1 && yBegin == -1){
                    xBegin = i;
                    yBegin = j;
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
                dir = 0;
                distance = 0;
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
            
            j = 0;
            boolean isFound = false;
            while(j < this.width){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
            }
            if (isFound){
                if (xBegin == -1 && yBegin == -1){
                    xBegin = i;
                    yBegin = j;
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
            
            i = this.height - 1;
            boolean isFound = false;
            while(i >= 0){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
            }
            if (isFound){
                if (xBegin == -1 && yBegin == -1){
                    xBegin = i;
                    yBegin = j;
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
                        distance++;
                        j++;
                    }else if(i < xBegin && dir == 1){
                        dir = 0;
                        if (distance > minDistanceValley){
                            count++;
                        }
                        distance = 0;
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
                dir = 0;
                distance = 0;
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
            
            i = 0;
            boolean isFound = false;
            while(i < this.height){
                if(this.matrixBlackWhite[i][j] == 1){
                    isFound = true;
                    break;
                }
            }
            if (isFound){
                if (xBegin == -1 && yBegin == -1){
                    xBegin = i;
                    yBegin = j;
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
                        dir = 0;
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
                dir = 1;
                distance = 0;
            }
        }
        return count;
    }

    public int recognizeCharacterAscii(){
        
        // feature direction, kuadran, gradien, isOriginOffset tertentu, intersect point, endpoint. circle
        if (this.objectsSkeletons.size() == 1){
            // System.out.println("1 objek");
            Skeleton s = objectsSkeletons.get(0);
            ArrayList<Point> pListEndPoint = s.getEndPoint();
            ArrayList<Point> pListInterPoints = s.getIntersectPoint();
            // System.out.println("size of endpoint : " + pListEndPoint.size());

            // sementara 0 - 9 (ascii 48 - 57)
            if (pListEndPoint.size() == 0){
                int c = s.getCircle() == 1
                if(c == 1){
                    return 0;
                }else if (pListInterPoints.size() > 0 && c == 2){
                    return 8;
                }
            }else if(pListEndPoint.size() == 1){
                int q = getAreaQuadran(pListEndPoint.get(0));
                // System.out.println("q : " + q);
                if (q == 2){
                    return 6;
                }else if(q == 4){
                    return 9;
                }else if(q == 3){
                    return 4;
                }
            }else if(pListEndPoint.size() == 2){
                Point p1 = new Point();
                Point p2 = new Point();
                if(pListEndPoint.get(0).x <  pListEndPoint.get(1).x){
                    p1 = pListEndPoint.get(0);
                    p2 = pListEndPoint.get(1);
                }else{
                    p2 = pListEndPoint.get(0);
                    p1 = pListEndPoint.get(1);                
                }
                
                int q1 = getAreaQuadran(p1);
                int q2 = getAreaQuadran(p2);
                // System.out.println("q1 : " + q1 + " , " + "q2 : " + q2);
                // System.out.println("dir1 : " + getDirection(p1) + " , " + "dir2 : " + getDirection(p2));
                if (q1 == 1 && q2 == 4  && getDirection(p1) == 3 && (getDirection(p2) == 2 || getDirection(p2) == 1)){
                    return 7;
                }else if(q1 == 1 && q2 == 3 && getDirection(p2) == 1){
                    return 1;
                }else if(q1 == 2 && q2 == 4){
                    return 5;
                }else if(q1 == 1 && q2 == 3){
                    return 2;
                }else if(q1 == 3 && q2 == 3){
                    return 4;
                }else if(q1 == 1 && q2 == 4){
                    return 3;
                }
            }
            return -1;
        }else{
            return -1;
        }
    }
    public static void main(String[] args){
        for(int i = 65;i<66;i++){
            String file = i + ".png";
            String file2 = i + "Thinning.png";
            String out = i + "out.txt";
            String out2 = i + "outThinning.txt";
            Writer writer = null;
            System.out.println(file);
            try{
                BufferedImage img = ImageIO.read(new File(file));
                writer = new BufferedWriter(new FileWriter(out));
                Collection.readImage(img, writer);
                if (writer != null) {
                    writer.close();
                }
                Collection cs = new Collection(Collection.pix01, img.getWidth(), img.getHeight());
                cs.thinImage();
                cs.setThinningList();
                cs.getBoundPoints();
                cs.getSkeletons();
                cs.postProcessingThresholdAll();
                int c = zs.recognizeCharacterAscii();
                System.out.println("Character : " + c);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}