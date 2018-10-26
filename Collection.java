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

    private final int THRESHOLD_COMMON = 5;
    // private ArrayList<Integer> chainCode;
    private Point pointMax;
    private Point pointMin;
    private ArrayList<Skeleton> objectsSkeletons;
    
    public static Color pix[][];
    public static int pix01[][];

    private final int iterationDirections[][] = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    private final  int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};

    private ArrayList<Point> toZerolist;

    public Collection(int matrix[][], int height, int width){
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
        this.resultThinningList = new ArrayList<>();
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

    public void toZeroMatrix(){
        for(int i =0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                this.matrixBlackWhite[i][j] = 0;
            }
        }
    }
    public void addListToMatrix(ArrayList<Point> pList){
        for(Point p: pList){
            this.matrixBlackWhite[p.x][p.y] = 1;
        }
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
        // if (this.resultThinningList.size() > 0)
        this.resultThinningList.clear();
        for(int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
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
            // System.out.println("call");
            if (s.getResultThinningList().size() > 2){
                s.postProcessingThreshold(THRESHOLD_COMMON);
            }
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

    
    public void printAllDistanceEndPointSkeletons(){
        for(Skeleton s:this.objectsSkeletons){
            s.printAllDistanceEndPoint();
            System.out.println();
        }
    }

    public void printAllIntersectPointSkeletons(){
        for(Skeleton s:this.objectsSkeletons){
            s.printAllIntersectPoint();
            System.out.println();
        }
    }

    public int recognizeCharacterAscii(){
        
        // feature direction, kuadran, gradien, isOriginOffset tertentu, intersect point, endpoint. circle
        if (this.objectsSkeletons.size() == 1){
            System.out.println("1 objek");
            Skeleton s = this.objectsSkeletons.get(0);
            ArrayList<Point> pListEndPoint = s.getEndPoint();
            ArrayList<Point> pListInterPoints = s.getIntersectPoint();
            int down = s.getValleyFromDown();
            int up = s.getValleyFromUp();
            int left = s.getValleyFromLeft();
            int right = s.getValleyFromRight();
            System.out.println("valley down : " + down); 
            System.out.println("valley up : " + up); 
            System.out.println("valley left : " + left);
            System.out.println("valley right : " + right); 
            System.out.println("jmlh end point : " + pListEndPoint.size());           
            // System.out.println("size of endpoint : " + pListEndPoint.size());

            // sementara 0 - 9 (ascii 48 - 57)
            int c = s.getCircle();
            System.out.println("Circle : " + c);
            if (pListEndPoint.size() == 0){
                
                if(c == 1){
                    if ( (this.pointMax.x - this.pointMin.x) - (this.pointMax.y - this.pointMin.y) < 3){
                        return 79;
                    }else return 48;
                }else if (pListInterPoints.size() == 2 && c == 2){
                    return 56;
                }
            }else if(pListEndPoint.size() == 1){
                if(this.resultThinningList.size() == 1){
                    return 46;
                }
                int q = s.getAreaQuadran(pListEndPoint.get(0));
                // System.out.println("q : " + q);
                if (q == 2 && c == 1){
                    return 54;
                }else if(q == 4 && c == 1){
                    return 57;
                }
                ArrayList<Point> pListIntersect= s.getIntersectPoint();
                if(q == 3 && c == 1 && pListIntersect.size() == 1 && pListIntersect.get(0).y == pListEndPoint.get(0).y){
                    return 52;
                }else if(q == 3 && c == 1 && pListIntersect.size() == 1 && s.getDistanceFromPattern(pListEndPoint.get(0).x, pListEndPoint.get(0).y) > s.getResultThinningList().size() / 2){
                    return 64;
                }else if(q == 3 && c == 1 && pListIntersect.size() == 1 && s.getDistanceFromPattern(pListEndPoint.get(0).x, pListEndPoint.get(0).y) > s.getResultThinningList().size() / 4){
                    return 81;
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
                
                int q1 = s.getAreaQuadran(p1);
                int q2 = s.getAreaQuadran(p2);
                // System.out.println("q1 : " + q1 + " , " + "q2 : " + q2);
                // System.out.println("dir1 : " + getDirection(p1) + " , " + "dir2 : " + getDirection(p2));
                if(p1.y == p2.y && p2.x < this.height/2){
                    return 39;
                }else if(p1.x > this.height/2 && p2.x < this.height/2 && left == 0 && right == 0 && p1.y == p2.y){
                    return 73;
                }else if(p1.x > this.height/2 && p2.x < this.height/2 && left == 1){
                    return 44;
                }else if(p1.x == p2.x && up == 0 && down == 0){
                    return 45;
                }else if(p1.y > this.width/2 && p2.y < this.width/2 && up == 1 && down == 1){
                    return 126;
                }else if(p2.x > this.height/2 && q1 == 1 && q2 == 3 && left == 0 && right == 0){
                    return 96;
                }else if(c == 2 && q1 == 2 && q2 == 4){
                    return 36;
                }else if (c == 2 && q1 == q2 && q1 == 3){
                    return 38;
                }else if(q1 == 3 && q2 == 3){
                    return 52;
                }else if(q1 == 1 && q2 == 3){
                    return 50;
                }else if( ((q1 == 1 && q2 == 2) || (q2 == 1 && q1 == 2)) && up == 1){
                    return 85;
                }

                // if (q1 == 1 && q2 == 4  && s.getDirection(p1) == 3 && (s.getDirection(p2) == 2 || s.getDirection(p2) == 1)){
                //     return 7;
                // }else if(q1 == 1 && q2 == 3 && s.getDirection(p2) == 1){
                //     return 1;
                // }else if(q1 == 2 && q2 == 4){
                //     return 5;
                // }else if(q1 == 1 && q2 == 3){
                //     return 2;
                // }else if(q1 == 3 && q2 == 3){
                //     return 4;
                // }else if(q1 == 1 && q2 == 4){
                //     return 3;
                // }
            }else{
                ArrayList<Point> pListIntersect = s.getIntersectPoint();
                if(c == 1 && pListEndPoint.size() == 8 && pListIntersect.size() == 4){
                    return 35;
                }else if(c == 2 && pListEndPoint.size() == 4 && pListIntersect.size() == 3){
                    return 36;
                }else if(c == 0 && pListEndPoint.size() == 5){
                    return 42;
                }
            }
            return -1;
        }else if (this.objectsSkeletons.size() == 2){
            // System.out.println("2 objek");
            System.out.println(this.objectsSkeletons.get(0).getResultThinningList().size());
            System.out.println(this.objectsSkeletons.get(1).getResultThinningList().size());
            if(this.objectsSkeletons.get(0).getResultThinningList().size() - this.objectsSkeletons.get(1).getResultThinningList().size() < 5){
                // System.out.println("2 objek sama");
                if(this.objectsSkeletons.get(0).getResultThinningList().size() < 3 && this.objectsSkeletons.get(1).getResultThinningList().size() < 3){
                    return 58;
                }else if(this.objectsSkeletons.get(0).getResultThinningList().get(this.objectsSkeletons.get(0).getResultThinningList().size() - 1).x < this.height/2 &&
                this.objectsSkeletons.get(1).getResultThinningList().get(this.objectsSkeletons.get(1).getResultThinningList().size() - 1).x < this.height/2
                ){
                    return 34;
                }
                
            }else{
                Skeleton s1 = null;
                Skeleton s2 = null;
                if(this.objectsSkeletons.get(0).getResultThinningList().size() > this.objectsSkeletons.get(0).getResultThinningList().size()){
                    s1 = this.objectsSkeletons.get(0);
                    s2 = this.objectsSkeletons.get(1);
                }else{
                    s2 = this.objectsSkeletons.get(0);
                    s1 = this.objectsSkeletons.get(1);
                }
                if(s1.getEndPoint().size() == 2 && s2.getResultThinningList().size() == 1){
                    Point p1 = new Point();
                    Point p2 = new Point();
                    
                    if(s1.getEndPoint().get(0).x < s1.getEndPoint().get(1).x){
                        p1 = s1.getEndPoint().get(0);
                        p2 = s1.getEndPoint().get(1);
                    }else{
                        p1 = s1.getEndPoint().get(1);
                        p2 = s1.getEndPoint().get(0);
                    }
                    int left = s1.getValleyFromLeft();
                    Point p3 = new Point();
                    p3 = s2.getResultThinningList().get(0);
                    if(p3.x > p2.x && p3.x > p1.x && p1.y == p2.y){
                        return 33;
                    }else if(p3.x < p2.x && p3.x < p1.x && p1.y == p2.y){
                        return 105;
                    }else if(p3.x < p2.x && p3.x < p1.x && p1.y > p2.y){
                        return 106;
                    }else if(p3.x > p2.x && p3.x > p1.x && p1.y < p2.y){
                        return 63;
                    }else if(p3.x < p2.x && p3.x < p1.x && p1.y == p2.y && left > 0){
                        return 59;
                    }
                }
            }
            return -1;
        }else if (this.objectsSkeletons.size() == 3){
            System.out.println("3 objek");
            Skeleton s1 = null;
            Skeleton s2 = null;
            Skeleton s3 = null;
            int l1 = this.objectsSkeletons.get(0).getResultThinningList().size();          
            int l2 = this.objectsSkeletons.get(1).getResultThinningList().size();
            int l3 = this.objectsSkeletons.get(2).getResultThinningList().size();          
            if(l1 >= l2 && l1>= l3){
                s1 = this.objectsSkeletons.get(0);
                if(l2 >= l3){
                    s2 = this.objectsSkeletons.get(1);
                    s3 = this.objectsSkeletons.get(2);
                }else{
                    s2 = this.objectsSkeletons.get(2);
                    s3 = this.objectsSkeletons.get(1);
                }
            }else if(l2 >= l1 && l2 >= l3){
                s1 = this.objectsSkeletons.get(1);
                if(l1 >= l3){
                    s2 = this.objectsSkeletons.get(0);
                    s3 = this.objectsSkeletons.get(2);
                }else{
                    s2 = this.objectsSkeletons.get(2);
                    s3 = this.objectsSkeletons.get(0);
                }
            }else if(l3 >= l1 && l3 >= l2){
                s1 = this.objectsSkeletons.get(2);
                if(l1 >= l2){
                    s2 = this.objectsSkeletons.get(0);
                    s3 = this.objectsSkeletons.get(1);
                }else{
                    s2 = this.objectsSkeletons.get(1);
                    s3 = this.objectsSkeletons.get(0);
                }
            }
            if(s1.getEndPoint().size() == 2 && s2.getCircle() == 1 && s3.getCircle() == 1){
                return 37;
            }
            return -1;
        }else return -1;
    }

    public void toImageAfterThinning(String file){
        try{
            BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            File f = new File(file);
            int px;
            for(int i=0;i<this.height;i++){
                for(int j=0;j<this.width;j++){
                    // R G B
                    if(this.matrixBlackWhite[i][j] == 1){ //B
                        px = 0;
                        int col = (px << 16) | (px << 8) | px;
                        img.setRGB(j, i, col);
                    }else if(this.matrixBlackWhite[i][j] == 0){ //W
                        px = 255;
                        int col = (px << 16) | (px << 8) | px;
                        img.setRGB(j, i, col);
                    }else{ //R
                        int col = (255 << 16) | (0 << 8) | 0;
                        img.setRGB(j, i, col);
                    }
                    
                }
            }
            ImageIO.write(img, "PNG", f);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void getUpdateMatrix(){
        toZeroMatrix();
        for(Skeleton s:this.objectsSkeletons){
            addListToMatrix(s.getResultThinningList());
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

    public void toFileSkeleton(String file){
        int i = 0;
        for(Skeleton s:this.objectsSkeletons){
            i++;
            s.toFileAfterThinning("skeletonKe" + i + "For" + file);
        }
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int caseAscii = sc.nextInt();

        for(int i = caseAscii;i<caseAscii+1;i++){
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
                
                cs.toFileSkeleton(out);
                // System.out.println("Distance : ");
                // cs.printAllDistanceEndPointSkeletons();
                // System.out.println("Intersect : ");                
                // cs.printAllIntersectPointSkeletons();
                
                cs.getUpdateMatrix();
                cs.toFileAfterThinning(out2);
                cs.toImageAfterThinning(file2);
                
                
                int c = cs.recognizeCharacterAscii();
                System.out.println("Character : " + c);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}