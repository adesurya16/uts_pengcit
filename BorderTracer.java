import java.lang.Math;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/* Asumsi hanya ada 1 objek yang akan di bordering */
class BorderTracer {
    private static final int MAX_DIRECTION = 8;
    private static final int MAX = 10;
    private int width;
    private int height;
    private int matrixBlackWhite[][]; // ada -1 untuk chain code point
    private ArrayList<Integer> chainCode;
    private ArrayList<Point> chainCodePoint;
    public static Color pix[][];
    public static int pix01[][];

    public BorderTracer(int matrix[][],int height ,int width){
        this.height = height;
        this.width = width;
        this.matrixBlackWhite = new int[height][];
        for (int i = 0;i < height;i++){
            this.matrixBlackWhite[i] = new int[width];
            for(int j = 0;j < width;j++){
                this.matrixBlackWhite[i][j] = matrix[i][j];
            }
        }
        // bacaFile();
        this.chainCode = new ArrayList<Integer>();
        this.chainCodePoint = new ArrayList<>();
    }

    public void toImageAfterThinning(String file){
        try{
            BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            File f = new File(file);
            int px;
            for(int i=0;i<this.height;i++){
                for(int j=0;j<this.width;j++){
                    int col;
                    if(this.matrixBlackWhite[i][j] == 1){
                        px = 0;
                        col = (px << 16) | (px << 8) | px;
                    }else if(this.matrixBlackWhite[i][j] == 0){
                        px = 255;
                        col = (px << 16) | (px << 8) | px;
                    }else{
                        // r g b
                        col = (255 << 16) | (0 << 8) | 0;
                    }
                    // int col = (px << 16) | (px << 8) | px;
                    img.setRGB(j, i, col);
                }
            }
            ImageIO.write(img, "PNG", f);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void readImage(BufferedImage img){
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
        pix01 = new int[height][];
            for(int i = 0;i < height;i++){
                pix01[i] = new int[width];
                for(int j = 0;j < width;j++){
                    // Color c = new Color(img.getRGB(i, j));
                    // pix[i][j] = c;
                    Color c = pix[i][j];
                    
                    if (c.getRed() == 255 && c.getGreen() == 255 && c.getBlue() == 255){
                        // System.out.print(0);
                        // writer.write("0");
                        pix01[i][j] = 0;
                    }else{
                        // System.out.print(1);
                        // writer.write("1");
                        pix01[i][j] = 1;
                    }
                }
                // writer.write("\n");
                // System.out.println();
            }
        
    }

    public void bacaFile(){
        
        try{
            BufferedReader objReader = new BufferedReader(new FileReader("D:\\coder\\pengcit\\nol.txt"));
            String s;
            int i = 0;
            
            while ((s = objReader.readLine()) != null) {
                // System.out.println(s);
                String[] sampleStringSplit = s.split(" ");
                for(int j=0;j<sampleStringSplit.length;j++){
                    // System.out.println(Integer.parseInt(sampleStringSplit[j]));
                    this.matrixBlackWhite[i][j] = Integer.parseInt(sampleStringSplit[j]);
                }
                // for (int j=0;j<sampleStringSplit.length;j++){
                //     this.matrixBlackWhite[i][j] = Integer.parseInt(sampleStringSplit[j]);
                // }    
                i++;
            }
            
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

    public void tracingImage(int x, int y){
        // isShape = true if tracing result make a shape 
        int xBegin = x;
        int yBegin = y;
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
                    this.matrixBlackWhite[xBegin][yBegin] = -1;
                    this.chainCodePoint.add(new Point(xBegin, yBegin));
                }
            }

            if (found){
                break;
            }else{
                from = (from + 1) % MAX_DIRECTION; // counter                   
            }
        }
        dir = from;
        this.chainCode.add(dir);            
        // loop
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
                        this.matrixBlackWhite[xBegin][yBegin] = -1;
                        this.chainCodePoint.add(new Point(xBegin, yBegin));
                    }
                }

                if (found){
                    break;
                }else{
                    from = (from + 1) % MAX_DIRECTION;                    
                }
            }
            // gak mungkin not found
            int last = this.chainCode.size() - 1;
            if (!found || (Math.abs(this.chainCode.get(last) - from) == 4)){
                break; //stop
            }else{
                dir = from;
                this.chainCode.add(dir);
            }            
        }
    }

    public void mainBorderTracing(){
        // red = -1 border mark
        // black = 1
        // white = 0

        // asumsi 1 object shape dulu
        boolean isFound = false;
        for (int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                if (this.matrixBlackWhite[i][j] > 0 && !isFound){
                    // call proses
                    tracingImage(i,j);
                    // add to List of shape (for multiple number image)
                    isFound = true;
                }
            }
        }
    }

    public void printChainCode(){
        for(int i=0;i<this.chainCode.size();i++){
            System.out.print(this.chainCode.get(i) + " ");
        }
        System.out.println();
    }

    public void printmatrixBlackWhite(){    
        for(int i=0;i<this.height;i++){
            for(int j=0;j<this.width;j++){
                if(j==0){
                    System.out.print(this.matrixBlackWhite[i][j]);
                }else{
                    System.out.print(" " + this.matrixBlackWhite[i][j]);
                } 
            }
            System.out.println();
        }
    }

    public static void main(String[] args){
        try{
            String file = "dua.png";
            String out = "duaBorder.png";
            BufferedImage img = ImageIO.read(new File(file));
            BorderTracer.readImage(img);
            BorderTracer obj = new BorderTracer(BorderTracer.pix01, img.getWidth(), img.getHeight());
            obj.mainBorderTracing();
            obj.printChainCode();
            obj.toImageAfterThinning(out);
        }catch(IOException e){
            e.printStackTrace();
            
        }
        
        // obj.printFile();
        // obj.mainBorderTracing();
        // obj.printChainCode();
        // obj.printmatrixBlackWhite();
        // obj.printFile();
    }
}