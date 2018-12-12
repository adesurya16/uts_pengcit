import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.util.*;
import java.io.IOException;
public class MainOperator{
    private int height;
    private int width;
    public int[][] redPixel2;
    public int[][] greenPixel2;
    public int[][] bluePixel2;
    public int[][] pixel;
    public int[][] matrixBw;
    public static Color[][] pix;

    final int redVal = (200 << 16) | (0 << 8) | 0;
    final int greenVal = (0 << 16) | (200 << 8) | 0;
    final int blueVal = (0 << 16) | (0 << 8) | 200;
    final int whiteVal = (255 << 16) | (255 << 8) | 255;
    final int blackVal = (0 << 16) | (0 << 8) | 0;

    public MainOperator(Color pix[][], int height, int width){
        this.height = height;
        this.width = width;
        
        this.redPixel2 = new int[height][];
        for(int i=0;i<height;i++){
            this.redPixel2[i] = new int[width];
        }

        this.greenPixel2 = new int[height][];
        for(int i=0;i<height;i++){
            this.greenPixel2[i] = new int[width];
        }

        this.bluePixel2 = new int[height][];
        for(int i=0;i<height;i++){
            this.bluePixel2[i] = new int[width];
        }

        this.matrixBw = new int[height][];
        for(int i=0;i<height;i++){
            this.matrixBw[i] = new int[width];
        }

        this.pixel = new int[height][];
        for(int i=0;i<height;i++){
            this.pixel[i] = new int[width];
        }

        // init

        for(int i = 0;i< height;i++){
            for(int j=0;j < width; j++){
                this.redPixel2[i][j] = pix[i][j].getRed();
            }
        }

        for(int i = 0;i< height;i++){
            for(int j=0;j < width; j++){
                this.greenPixel2[i][j] = pix[i][j].getGreen();
            }
        }

        for(int i = 0;i< height;i++){
            for(int j=0;j < width; j++){
                this.bluePixel2[i][j] = pix[i][j].getBlue();
            }
        }

        for(int i = 0;i< height;i++){
            for(int j=0;j < width; j++){
                int col = ( (redPixel2[i][j] << 16) | (greenPixel2[i][j] << 8) | bluePixel2[i][j] );
                this.pixel[i][j] = col;
            }
        }

        // init bw
        // imageSkinRgbOperation();
    }

    public void setMatrixBW(int matrix[][]){
        for(int i = 0; i < height;i++){
            for(int j = 0; j < width;j++){
                this.matrixBw[i][j] = matrix[i][j];
            }
        }
    }

    private void imageSkinRgbOperation(){
        // BitmapDrawable bd = (BitmapDrawable) photoView.getDrawable();
        
        int val;
        // sumOfRowColValue = 0;
        // sumHeight = new int[height];
        // sumWidth = new int[width];

        // matrixBw = new int[height][width];
        // output = bd.getBitmap().copy(Bitmap.Config.RGB_565, true);

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int r = redPixel2[i][j];
                int g = greenPixel2[i][j];
                int b = bluePixel2[i][j];

                // Under flashlight or daylight called lateral illumination:
                //if (r > 20 && g > 210 && b > 170 && Math.abs(r-g) < 15 &&
                //      r > g && g > b)

                // For uniform daylight illumination:
                if (r > 95 && g > 40 && b > 20 && r > g &&
                        r > b && Math.abs(r-g) > 15 &&
                        (Math.max(Math.max(r,g), b) - Math.min(Math.min(r,g), b)) > 15)
                    val = whiteVal;
                else
                    val = blackVal;

                // sumOfRowColValue += pixel[i][j];
                matrixBw[i][j] = val;
                // sumHeight[i] += pixel[i][j];
                // sumWidth[j] += pixel[i][j];
            }
        }
    }

    public static void readImage(BufferedImage img){
        
        int width = img.getHeight();
        int height = img.getWidth();
        System.out.println("masuk");
        pix = new Color[height][];
        for(int i = 0;i < height;i++){
            
            pix[i] = new Color[width];

            for(int j = 0;j < width;j++){
                // System.out.println(img.getRGB(i, j));
                Color c = new Color(img.getRGB(i, j));
                pix[i][j] = c;
            }
        }
        
        // try{
        //     pix01 = new int[height][];
        //     for(int i = 0;i < height;i++){
        //         pix01[i] = new int[width];
        //         for(int j = 0;j < width;j++){
        //             // Color c = new Color(img.getRGB(i, j));
        //             // pix[i][j] = c;
        //             Color c = pix[i][j];
                    
        //             if (c.getRed() == 255 && c.getGreen() == 255 && c.getBlue() == 255){
        //                 // System.out.print(0);
        //                 writer.write("0");
        //                 pix01[i][j] = 0;
        //             }else{
        //                 // System.out.print(1);
        //                 writer.write("1");
        //                 pix01[i][j] = 1;
        //             }
        //         }
        //         writer.write("\n");
        //         // System.out.println();
        //     }
        // }catch(IOException e){
        //     e.printStackTrace();
        // }
        
    }

    public void toImageBw(String file){
        try{
            BufferedImage img = new BufferedImage(this.height, this.width, BufferedImage.TYPE_INT_RGB);
            File f = new File(file);
            int px;
            for(int i=0;i<this.width;i++){
                for(int j=0;j<this.height;j++){
                    // R G B
                    int col = this.matrixBw[j][i];
                    img.setRGB(j, i, col);
                    // if(this.matrixBw[i][j] == whiteVal){ //B
                    //     px = 0;
                    //     int col = (px << 16) | (px << 8) | px;
                    //     img.setRGB(j, i, col);
                    // }else if(this.matrixBlackWhite[i][j] == 0){ //W
                    //     px = 255;
                    //     int col = (px << 16) | (px << 8) | px;
                    //     img.setRGB(j, i, col);
                    // }else{ //R
                    //     int col = (255 << 16) | (0 << 8) | 0;
                    //     img.setRGB(j, i, col);
                    // }
                    
                }
            }
            ImageIO.write(img, "PNG", f);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void imageToYCbCrOperation() {
        // BitmapDrawable bd = (BitmapDrawable) photoView.getDrawable();
        // int height = bd.getBitmap().getHeight();
        // int width = bd.getBitmap().getWidth();

        // output = bd.getBitmap().copy(Bitmap.Config.RGB_565, true);

        // dari paper https://ieeexplore.ieee.org/abstract/document/6982471
        double tetha = 2.53;
        double costetha = Math.cos(tetha);
        double sintetha = Math.sin(tetha);
        double Cx = 109.38;
        double Cy = 152.02;
        double ecx = 2.41;
        double ecy = 2.53;
        double a = 25.39;
        double b = 14.03;
        int val;

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {

                int r = redPixel2[i][j];
                int g = greenPixel2[i][j];
                int blue = bluePixel2[i][j];

                int Y = (int)(0.299*r+0.587*g+0.114*blue);
                int Cb=(int)(128-0.169*r-0.332*g+0.500*blue);
                int Cr =(int)(128+0.500*r-0.419*g-0.081*blue);

                double CbMinCx = Cb - Cx;
                double CrMinCy = Cr - Cy;
                double x = costetha * CbMinCx + sintetha * CrMinCy;
                double y = (-1*sintetha*CbMinCx) + costetha * CrMinCy;  
                double equation1 = ((x - ecx) * (x - ecx)) / (a * a);
                double equation2 = ((y - ecy) * (y - ecy)) / (b * b);

                // Log.d(TAG, "equation : " + (int)(equation1 + equation2));

                if ((int) (equation1 + equation2) == 1)
                    val = whiteVal;
                else
                    val = blackVal;
                    // val = (Y<<16) | (Cb<<8) | Cr;

                // output.setPixel(j, i, val);
                this.matrixBw[i][j] = val;
            }
        }
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();        
        String file = s + ".png";
        String file2 = s + "out.png";
        try{    
            BufferedImage img = ImageIO.read(new File(file));
            System.out.println("masuk");
        // writer = new BufferedWriter(new FileWriter(out));
            MainOperator.readImage(img);

            MainOperator mo = new MainOperator(MainOperator.pix, img.getWidth(), img.getHeight());
            // mo.imageToYCbCrOperation();
            mo.imageSkinRgbOperation();
           
            SkinningField sf = new SkinningField(mo.redPixel2, mo.greenPixel2, mo.bluePixel2, mo.matrixBw, img.getWidth(), img.getHeight());
            mo.setMatrixBW(sf.getMarkedObjectToBW());
            mo.toImageBw(file2);

        }catch(IOException e){
            e.printStackTrace();
        }
    }   
}