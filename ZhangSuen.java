// package com.alhudaghifari.bildghifar.tugas5Thinning;
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

class ZhangSuen {
    private static final int MAX_DIRECTION = 8;
    private int matrixBlackWhite[][];
    private int width;
    private int height;
    private ArrayList<Point> resultThinningList;
    private Point pointMax;
    private Point pointMin;
    public static Color pix[][];
    public static int pix01[][];
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
        {{ -1, 1, 0},
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

    private final  int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6}, {0, 4, 6}}};

    private ArrayList<Point> toZerolist;

    public ZhangSuen(int matrix[][], int height, int width){
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

    

    public static void readImage(BufferedImage img, Writer writer){
        // try{
        // // this.height = i;
        //     System.out.println(img.getHeight() + " " + img.getWidth());            
        //     pix = new Color[img.getHeight()][];
        //     for(int i = 0;i < img.getHeight();i++){
                
        //         pix[i] = new Color[img.getWidth()];

        //         for(int j = 0;j < img.getWidth();j++){
        //             Color c = new Color(img.getRGB(i, j));
        //             pix[i][j] = c;
        //         }
        //     }
        //     // Color c = new Color(img.getRGB(0, 0));
        //     // System.out.println(c.getRed() + " " + c.getGreen() + " " + c.getBlue());
        //     System.out.println(img.getHeight() + " " + img.getWidth());
        // }catch(IOException e){
        //     e.printStackTrace();
        // }

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
        // this.pix = new int[img.getWidth()][img.getHeight()];
        // try{
        //     Writer writer = new BufferedWriter("out.txt");
            
        // }catch(IOException e){
        //     e.printStackTrace();
        // }finally{
        //     if (writer != null) {
        //         writer.close();
        //       }
        // }
        
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

    public void bacaFile(String file){

        try{
            BufferedReader objReader = new BufferedReader(new FileReader("D:\\coder\\pengcit\\" + file));
            String s;
            int i = 0;

            while ((s = objReader.readLine()) != null) {
                // System.out.println(s);
                String[] sampleStringSplit = s.split(" ");
                // this.matrixBlackWhite = new int[this][];
                for(int j=0;j<sampleStringSplit.length;j++){
                    // System.out.println(Integer.parseInt(sampleStringSplit[j]));
                    this.matrixBlackWhite[i][j] = Integer.parseInt(sampleStringSplit[j]);
                }
                // for (int j=0;j<sampleStringSplit.length;j++){
                //     this.matrixBlackWhite[i][j] = Integer.parseInt(sampleStringSplit[j]);
                // }
                i++;
            }
            // this.height = i;
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void printFile(){
        for(int i = 0;i<this.height;i++){
            for(int j = 0;j < this.width;j++){
                System.out.print(this.matrixBlackWhite[i][j] + " ");
            }
            System.out.println();
        }
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

    public void printThinningList(){
        // for(int i=0;i<this.resultThinningList.size();i++){
        //     System.out.println("(" + this.resultThinningList[i].getX() + ", " + this.resultThinningList[i].getY() + ")");
        // }
        for (Point p: this.resultThinningList){
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
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



    public void printEndpoint(){
        ArrayList<Point> pList = getEndPoint();
        for (Point p: pList){
            System.out.println("(" + p.x + ", " + p.y + ")");
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

    public void printBoundedPoint(){
        System.out.println("max : " + this.pointMax.x + ", " + this.pointMax.y);
        System.out.println("min : " + this.pointMin.x + ", " + this.pointMin.y);
    }

    public int getAreaQuadran(int h, int w){
        // 1 .. 4
        int area = 0;
        if ((h > this.pointMin.x) && (h < ((this.pointMax.x + this.pointMin.x) / 2)) && (w > this.pointMin.y) && (w < (this.pointMax.y + this.pointMin.y) / 2)){
            area = 1;
        }else if(h > this.pointMin.x && h < ((this.pointMax.x + this.pointMin.x) / 2) && w > ((this.pointMax.y + this.pointMin.y) / 2) && w < this.pointMax.y){
            area = 2;
        }else if(h > ((this.pointMax.x + this.pointMin.x) / 2) && h < this.pointMax.x && w > ((this.pointMax.y + this.pointMin.y) / 2) && w < this.pointMax.y){
            area = 3;
        }else if(h > ((this.pointMax.x + this.pointMin.x) / 2) && h < this.pointMax.x && w > this.pointMin.y && (w < (this.pointMax.y + this.pointMin.y) / 2)){
            area = 4;
        }
        // else origin
        return area;
    }

    public int getAreaQuadran(Point p){
        int h = p.x;
        int w = p.y;
        // 1 .. 4
        int area = 0;
        if ((h > this.pointMin.x) && (h < ((this.pointMax.x + this.pointMin.x) / 2)) && (w > this.pointMin.y) && (w < (this.pointMax.y + this.pointMin.y) / 2)){
            area = 1;
        }else if(h > this.pointMin.x && h < ((this.pointMax.x + this.pointMin.x) / 2) && w > ((this.pointMax.y + this.pointMin.y) / 2) && w < this.pointMax.y){
            area = 2;
        }else if(h > ((this.pointMax.x + this.pointMin.x) / 2) && h < this.pointMax.x && w > ((this.pointMax.y + this.pointMin.y) / 2) && w < this.pointMax.y){
            area = 3;
        }else if(h > ((this.pointMax.x + this.pointMin.x) / 2) && h < this.pointMax.x && w > this.pointMin.y && (w < (this.pointMax.y + this.pointMin.y) / 2)){
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

    public void deleteLineToCenter(Point p, Point pCenter, ArrayList<Integer> dirL){
//        ArrayList<Point> p = getEndPoint();
        int xStart = p.x;
        int yStart = p.y;
        int i = 0;
        while(xStart != pCenter.x || yStart != pCenter.y){
            // Log.d("titik2 dari emdpoint paling pendek  : ", "" + i + " " + xStart + " " + yStart + " -> " + this.matrixBlackWhite[xStart][yStart]);
            this.matrixBlackWhite[xStart][yStart] = 0;
            if (i == dirL.size()){
                break;
            }
            xStart += this.iterationDirections[dirL.get(i) ][1];
            yStart += this.iterationDirections[dirL.get(i) ][0];

            i++;
        }
    }

    public void copyList(ArrayList<Integer> l1, ArrayList<Integer> l2){
        // copy isi l1 ke l2
        // l2 dalam keadaan kososng/empty l2
        for(int i: l1){
            l2.add(i);
        }
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
        while(!isNeighboorValidIntersect(xBegin, yBegin) && !isValidEndPoint(xBegin, yBegin) && numNeighbors(xBegin,yBegin) > 1){
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
                        // this.matrixBlackWhite[xBegin][yBegin] = 0;
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
                // this.chainCode.add(dir);
                // distance++;
            } 
        }

        // delete point
        
        // for(Point p: chainCodePoint){
        //     if(!isValidIntersectPoint(p.x, p.y)){
        //         System.out.println(this.matrixBlackWhite[p.x][p.y]);
        //         System.out.println(p.x + " , " + p.y);
        //         this.matrixBlackWhite[p.x][p.y] = 0;
        //     }
        // }
        // System.out.println("end of deleted");
        return chainCodePoint;
    }

    public void deleteLine(Point p){
        int xStart = p.x;
        int yStart = p.y;
        // Log.d("pointdelete  : ", " " + p.x + " " + p.y);
        while (numNeighbors(xStart, yStart) <= 1){
            int h = xStart;
            int w = yStart;
            int dir = -1;

            this.matrixBlackWhite[xStart][yStart] = 0;
            for(int i=0;i < this.iterationDirections.length - 1 ;i++){

                // System.out.println(this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]]);
                if (this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]] == 1 && dir == -1){
                    dir = i;
                }
            }
            // Log.d("direction deleteline : ", " " + dir);
            xStart = h + this.iterationDirections[dir][1];
            yStart = w + this.iterationDirections[dir][0];
        }
    }

    public void postProcessingThreshold(int tholdPrecentage){
        // thold percentage from longer width or height   
        getBoundPoints();
        int sizeFromBounded = (this.pointMax.x - this.pointMin.x) * (this.pointMax.x - this.pointMin.x) + (this.pointMax.y - this.pointMin.y) * (this.pointMax.y - this.pointMin.y);
        double sizeFromBounded2 = Math.sqrt((double) sizeFromBounded);
        int tholdSize = (int) sizeFromBounded2 * tholdPrecentage / 100;

        // if (this.height > this.width){
        //     tholdSize = (int)this.height*thold / (int) 100;
        // }else{
        //     tholdSize = (int)this.width*thold / (int) 100;
        // }
        ArrayList<Point> delPoint = new ArrayList<>();
        ArrayList<Point> pList = getEndPoint();
        boolean isChanged = true;
        while(isChanged){
            isChanged = false; 
            delPoint.clear();
            pList.clear();
            pList = getEndPoint();
            if (pList.size() > 2){
                for(Point p : pList){
                    if (getDistanceFromPattern(p.x, p.y) < tholdSize){
                    // System.out.println("Deleted");
                    isChanged = true;
                    delPoint.addAll(getListDeleteFromEndPoint(p.x, p.y));
                    // setThinningList();
                    }
                }
            }
            deletePointFromList(delPoint);
            deletePointFromList(getDeleteSisaPoint(pList, this.resultThinningList));
            deletePointFromList(getDeleteEndPointPattern(this.resultThinningList));
            setThinningList();
            getBoundPoints();
        }
        
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

    public void colorRedEndPoint(){
        ArrayList<Point> pList = getEndPoint();
        for(Point p: pList){
            this.matrixBlackWhite[p.x][p.y] = -1;
        }
    }

    public void postProcessing(int number){
        ArrayList<Point> pList = getEndPoint();
        int count = pList.size();
        if (count == 3){
            Point p1 = new Point(pList.get(0));
            Point p2 = new Point(pList.get(1));
            Point p3 = new Point(pList.get(2));
            if (p1.x > p2.x){
                Point tmp = new Point(p1);
                p1.setLocation(p2.x, p2.y);
                p2.setLocation(tmp.x, tmp.y);
            }
            if(p1.x > p3.x){
                Point tmp = new Point(p1);
                p1.setLocation(p3.x, p3.y);
                p3.setLocation(tmp.x, tmp.y);
            }
            if(p2.x > p3.x){
                Point tmp = new Point(p2);
                p2.setLocation(p3.x, p3.y);
                p3.setLocation(tmp.x, tmp.y);
            }
            if (number == 1 || number == 4 || number == 7){
//                deleteLine(p1)
            }else if(number == 5){
//                deleteLine(p2);
            }else if(number == 2){
//                deleteLine(p3);
            }

            if (number == 2 || number == 4 || number == 5 || number == 1 || number == 7){
                this.resultThinningList.clear();
                setThinningList();
                pList.clear();
                pList = getEndPoint();
                while (pList.size() > 2){
                    deleteLine(getMinEndPointDistance(pList));
                    this.resultThinningList.clear();
                    setThinningList();
                    pList.clear();
                    pList = getEndPoint();
                }
            }

        }

    }

    public void printAllDistanceEndPoint(){
        ArrayList<Point> pList = getEndPoint();
        for(int i=0;i<pList.size();i++){
            System.out.print("(" + pList.get(i).x + ", " + pList.get(i).y + ")");
            Point p = pList.get(i);
            int n = getDistanceFromPattern(p.x, p.y);
            System.out.println(n);
        }
        
        // delete soon
        // this.matrixBlackWhite[64][205] = -1;
    }

    public Point getMinEndPointDistance(ArrayList<Point> pList){
        int idx = 0;
        int minLen = getDistance(pList.get(0));
        for(int i=1;i<pList.size();i++){
            if (getDistance(pList.get(i)) > minLen){
                idx = i;
                minLen = getDistance(pList.get(i));
            }
        }
        return pList.get(idx);
    }


    public int getDistance(Point p){
        int xStart = p.x;
        int yStart = p.y;
        int currentX = -1;
        int currentY = -1;
        int len = 0;
        // Log.d("pointdistance  : ", " " + p.x + " " + p.y);
        while (numNeighbors(xStart, yStart) <= 2){
            int h = xStart;
            int w = yStart;
            int dir = -1;
            len++;
//            this.matrixBlackWhite[xStart][yStart] = 0;
            for(int i=0;i < this.iterationDirections.length - 1 ;i++){

                // System.out.println(this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]]);
                if (this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]] == 1 && dir == -1 && (h + this.iterationDirections[i][1]) != currentX && (w + this.iterationDirections[i][0]) != currentY){
                    dir = i;
                }   
            }
            if (dir == -1){
                break;
            }
            // Log.d("directiondistance  : ", " " + dir);
            xStart = h + this.iterationDirections[dir][1];
            yStart = w + this.iterationDirections[dir][0];
            currentX = h;
            currentY = w;
        }
        return len;
    }

    public int getDistance(int x, int y){
        int xStart = x;
        int yStart = y;
        int currentX = -1;
        int currentY = -1;
        int len = 0;
        // Log.d("pointdistance  : ", " " + p.x + " " + p.y);
        while (numNeighbors(xStart, yStart) <= 2){
            int h = xStart;
            int w = yStart;
            int dir = -1;
            len++;
//            this.matrixBlackWhite[xStart][yStart] = 0;
            for(int i=0;i < this.iterationDirections.length - 1 ;i++){

                // System.out.println(this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]]);
                if (this.matrixBlackWhite[h + this.iterationDirections[i][1]][w + this.iterationDirections[i][0]] == 1 && dir == -1 && (h + this.iterationDirections[i][1]) != currentX && (w + this.iterationDirections[i][0]) != currentY){
                    dir = i;
                }
            }
            if (dir == -1){
                break;
            }
            // Log.d("directiondistance  : ", " " + dir);
            xStart = h + this.iterationDirections[dir][1];
            yStart = w + this.iterationDirections[dir][0];
            currentX = h;
            currentY = w;
        }
        return len;
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
        while(!isNeighboorValidIntersect(xBegin, yBegin) && !isValidEndPoint(xBegin, yBegin) && numNeighbors(xBegin,yBegin) > 1){
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
        }
        return distance;
    }

    public int recognizeNumber(){
        // return number 0..9 , unknwon number -1
        ArrayList<Point> pList = getEndPoint();
        int count = pList.size();

        int number = -1;
        switch(count){
            case 0:
                // 0 atau 8
                // cari di line apakah ada garis
                int xmin = this.pointMin.x;
                int midy = (this.pointMax.y + this.pointMin.y) / 2;
                System.out.println(midy);
                for(int i=xmin+2;i<pointMax.x - 1;i++){
                    if (this.matrixBlackWhite[i][midy] == 1){
                        number = 8;
                        break;
                    }
                }
                if (number == -1){
                    number = 0;
                }
                break;
            case 1:
                // 6 atau 9
                int midx = (this.pointMin.x + this.pointMax.x) / 2;
                // System.out.println(midx);


                if (pList.get(0).x <= midx){
                    number = 6;
                }else{
                    number = 9;
                }
                break;
//            case -1:
//                 // preprocessing false endpoint
//
//                // calculate endpoint to branching
//                Point pCenter = new Point();
//                ArrayList<Integer> DirList = new ArrayList<>();
//                ArrayList<Integer> MinList = new ArrayList<>();
//                int jarakMin = 0;
//                int idx = 0;
//                for(int i=0;i<pList.size();i++){
//                    int nbx = pList.get(0).x; //neighboor
//                    int nby = pList.get(0).y; //neighboor
//                    int nbxstart = 0;
//                    int nbystart = 0;
//                    int start = 0;
//                    boolean found = false;
//
//                    int xBefore = 0;
//                    int yBefore = 0;
//                    DirList.clear();
//                    while(!found){
//                        int dir = 0;
//                        int numNeighboor = 0;
//                        for(int j=0;j<iterationDirections.length - 1;j++){
//                            if ((nbx + this.iterationDirections[j][1] != xBefore) || (nby + this.iterationDirections[j][1] != yBefore)) {
//                                if ( this.matrixBlackWhite[nbx + this.iterationDirections[j][1]][nby + this.iterationDirections[j][0]] == 1){
//                                    if (numNeighboor < 2) {
//                                        dir = j;
//                                        nbxstart = nbx + this.iterationDirections[j][1];
//                                        nbystart = nby + this.iterationDirections[j][0];
//
//                                    }else{
//                                        found = true;
//                                        pCenter.setLocation(nbx, nby);
//                                    }
//                                    numNeighboor++;
//                                }
//
//                            }
//
//                        }
//                        nbx = nbxstart;
//                        nby = nbystart;
//                        if (yBefore == nby && xBefore == nbx){
//                            break;
//                        }else if (!found){
//                            DirList.add(dir);
//                        }
//                        xBefore = nbx;
//                        yBefore = nby;
//                        start++;
//                    }
//
//                    if(i == 0){
//                        idx = i;
//                        jarakMin = start;
//                        MinList.clear();
//                        copyList(DirList, MinList);
//                    }else if(start < jarakMin){
//                        idx = i;
//                        jarakMin = start;
//                        MinList.clear();
//                        copyList(DirList, MinList);
//                    }
//                }
//                Log.d("direction 0  : ", " " + MinList.get(0));
//                Log.d("idx  : ", " " + idx);
//                Log.d("idxPoint  : ", " " + pList.get(idx));
//                Log.d("center  : ", " " + pCenter.x + " " + pCenter.y);
//                for(Point p: pList){
//                    Log.d("EndPoint  : ", " " + p);
//                }
//
//
//                deleteLineToCenter(pList.get(idx), pCenter, MinList);
//                pList = getEndPoint();
//                if (pList.size() > 2){
//                    Log.d("endpointbaru : ", " Kok Bisa Sih Endpointnya ada lebih dari 2 :( ");
//                }
//                setThinningList();
//                getBoundPoints();
//
//                // sama kayak kasus 2
//                Point p1 = new Point();
//                Point p2 = new Point();
//                pList = getEndPoint();
//                if (pList.get(0).x < pList.get(1).x){
//                    p1.setLocation(pList.get(0).x, pList.get(0).y);
//                    p2.setLocation(pList.get(1).x, pList.get(1).y);
//                }else{
//                    p2.setLocation(pList.get(0).x, pList.get(0).y);
//                    p1.setLocation(pList.get(1).x, pList.get(1).y);
//                }
//                int dir1 = getDirection(p1);
//                int area1 = getArea(p1.x, p1.y);
//                int dir2 = getDirection(p2);
//                int area2 = getArea(p2.x, p2.y);
//                if ((dir1 == 2 || dir1 == 3) && (dir2 == 2 || dir2 == 3)){
//                    number = 3;
//                }
//                int middlex = (this.pointMax.x + this.pointMin.x) / 2;
//                if( p1.x >= middlex && p2.x >= middlex){
//                    if ((dir2 == 8 || dir2 == 1 || dir2 == 2) && (dir1 == 6 || dir1 == 7 || dir1 == 8)){
//                        number = 4;
//                    }
//                }else if((dir2 == 2 || dir2 == 3) && dir1 == 7){
//                    number = 5;
//                }else if((dir2 == 7 || dir2 == 8) && (dir1 == 1 || dir1 == 2 || dir1 == 3)){
//                    number = 2;
//                }else if(dir1 == 3 || dir1 == 2){
//                    boolean found = false;
//                    for(int i = p2.x ;i>p1.x;i--){
//                        if(this.matrixBlackWhite[i][p2.y] == 0){
//                            found = true;
//                            break;
//                        }
//                    }
//                    if (found){
//                        number = 7;
//                    }else{
//            number = 1;
//        }
//    }
//
//                break;
                case 4:
                    number = 3;
                break;
                case 3:
                    Point p1 = new Point(pList.get(0));
                    Point p2 = new Point(pList.get(1));
                    Point p3 = new Point(pList.get(2));
                    if (p1.x > p2.x){
                        Point tmp = new Point(p1);
                        p1.setLocation(p2.x, p2.y);
                        p2.setLocation(tmp.x, tmp.y);
                    }
                    if(p1.x > p3.x){
                        Point tmp = new Point(p1);
                        p1.setLocation(p3.x, p3.y);
                        p3.setLocation(tmp.x, tmp.y);
                    }
                    if(p2.x > p3.x){
                        Point tmp = new Point(p2);
                        p2.setLocation(p3.x, p3.y);
                        p3.setLocation(tmp.x, tmp.y);
                    }
                    int dir1 = getDirection(p1);
                    int dir2 = getDirection(p2);
                    int dir3 = getDirection(p3);

                    int area1 = getAreaQuadran(p1.x, p1.y);
                    int area2 = getAreaQuadran(p2.x, p2.y);
                    int area3 = getAreaQuadran(p3.x, p3.y);

                    int middlex = (this.pointMax.x + this.pointMin.x) / 2;
                    int middley = (this.pointMax.y + this.pointMin.y) / 2;

                    if (area1 == 1 && area3 == 4 && (p2.x < (middlex + this.pointMax.x)/2) && (p2.x > (this.pointMin.x + middlex)/2)){
                        number = 3;
                    }else if(p2.x > middlex && p3.x > middlex && area2 != 4 && area3 != 4 && (dir3 == 8 || dir3 == 1 || dir3 == 2) && (dir2 == 8 || dir2 == 7 || dir2 == 6)){
                        number = 4;
                    }else if(dir1 == 7 && p1.y > middley && (dir3 == 2 || dir3 == 3 || dir3 == 4 || dir3 == 5) && area3 == 4 && p2.y < middley){
                        number = 5;
                    }else if((dir1 == 2 || dir1 == 3 || dir1 == 4 || dir1 == 1) && area1 == 1 && (dir2 == 6 || dir2 == 7 || dir2 == 8) && area2 == 3 && p3.x > middlex){
                        number = 2;
                    }else if((dir2 == 2 || dir2 == 3) && area2 == 1 && p1.y > middley){
                        if(dir3 == 1){
                            number = 1;
                        }else number = 7;
                    }
                    break;
            default:
                // unknown
                number = -1;
                break;
        }
        return number;
    }

    public int getAmountOfHole(){
        ArrayList<Point> pListIntersect = getIntersectPoint();
        
        if (pListIntersect.size() > 0){
            for(Point p: pListIntersect){

            }
            return 0;
        }else{

            int isCircle = 1;
            int x = this.resultThinningList.get(0).x;
            int y = this.resultThinningList.get(0).y;
            int xBegin = x;
            int yBegin = y;
            int xPrev = xBegin;
            int yPrev = yBegin;
            int dir = MAX_DIRECTION - 1;
            ArrayList<Integer> chainCode = new ArrayList<>(); 
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
                        // chainCodePoint.add(new Point(xBegin, yBegin));
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
                while(xBegin != x || yBegin != y){
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
                                // chainCodePoint.add(new Point(xBegin, yBegin));
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
                        isCircle = 0;
                        System.out.println(xBegin + " " + yBegin);
                        break; //stop
                    }else{
                        dir = from;
                        chainCode.add(dir);
                    }            
                }
            return isCircle;
        }
    }

    public int recognizeCharacterAscii(){
        // ambil semua feature yang mungkin
        // sementara masih 1 objek
        // return ascii number
        // feature direction, kuadran, gradien, isOriginOffset tertentu, intersect point, endpoint. circle
        ArrayList<Point> pListEndPoint = getEndPoint();
        ArrayList<Point> pListInterPoints = getIntersectPoint();
        System.out.println("size of endpoint : " + pListEndPoint.size());

        // sementara 0 - 9 (ascii 48 - 57)
        if (pListEndPoint.size() == 0){
            if (pListInterPoints.size() > 0){
                return 8;
            }else{
                return 0;
            }
        }else if(pListEndPoint.size() == 1){
            int q = getAreaQuadran(pListEndPoint.get(0));
            System.out.println("q : " + q);
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
            System.out.println("q1 : " + q1 + " , " + "q2 : " + q2);
            System.out.println("dir1 : " + getDirection(p1) + " , " + "dir2 : " + getDirection(p2));
            if (q1 == 1 && q2 == 4  && getDirection(p1) == 3 && getDirection(p2) == 2){
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
    }

    public static void main(String[] args){
        String data[] = new String[] {"nol", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan"};
        int threshold[] = new int[] {0, 20, 10, 0, 10, 10, 0, 5, 0, 0};
        for(int i = 0;i<10;i++){
            String file = data[i] + ".png";
            String file2 = data[i] + "Thinning.png";
            String out = data[i] + "out.txt";
            String out2 = data[i] + "outThinning.txt";
            Writer writer = null;
            try{
                BufferedImage img = ImageIO.read(new File(file));
                writer = new BufferedWriter(new FileWriter(out));
                ZhangSuen.readImage(img, writer);
                if (writer != null) {
                    writer.close();
                }
                ZhangSuen zs = new ZhangSuen(ZhangSuen.pix01, img.getWidth(), img.getHeight());
                zs.thinImage();
                zs.setThinningList();
                zs.getBoundPoints();
                // zs.printBoundedPoint();
                System.out.println(data[i] + " :");
                // zs.printEndpoint();
                // zs.printAllDistanceEndPoint();
                // System.out.println();
                zs.postProcessingThreshold(20);
                int c = zs.recognizeCharacterAscii();
                System.out.println("Character : " + c);
                // System.out.println();
                zs.colorRedEndPoint();
                zs.toFileAfterThinning(out2);
                zs.toImageAfterThinning(file2);
                System.out.println();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}