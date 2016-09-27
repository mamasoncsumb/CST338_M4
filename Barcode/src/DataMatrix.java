
public class DataMatrix implements BarcodeIO
{
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';
   private BarcodeImage image;
   private String text;
   private int actualWidth, actualHeight;

   //DefaultConstrucor - constructs an empty, but non-null, image and text value
   public DataMatrix()
   {
      this.text = "";
      this.image = new BarcodeImage();
      this.actualHeight = 0;
      this.actualWidth = 0;
   }

   //sets the image but leaves the text at its default value.
   public DataMatrix(BarcodeImage image)
   {
      scan(image);
   }

   //sets the text but leaves the image at its default value
   public DataMatrix(String text)
   {
      this.readText(text);
   }

   //Takes in a string variable and sets the data member "text"
   public boolean readText(String text)
   {
      this.text = text;
      return true;
   }

   //Sets image and calls helper methods to determine data members actualWidth and actualHeight
   public boolean scan(BarcodeImage image)
   {
      this.image = (BarcodeImage)image.clone();
      this.cleanImage();
      this.actualWidth = this.computeSignalWidth();
      this.actualHeight = this.computeSignalHeight();
      return true;
   }

   //returns actualWidth
   public int getActualWidth()
   {
      return this.actualWidth;
   }

   //returns actualHeight
   public int getActualHeight()
   {
      return this.actualHeight;
   }

   //returns width of image assuming it is in lower left corner
   private int computeSignalWidth()
   {
      int signalWidth = 0;
      for (int i = 0; i < BarcodeImage.MAX_WIDTH; i++) 
      {
         if (this.image.getPixel(BarcodeImage.MAX_HEIGHT - 1, i))
         {
            signalWidth++;
         } 
         else 
         {
            break;
         }
      }
      return signalWidth;
   }

   //returns height of image assuming it is in lower left corner
   private int computeSignalHeight()
   {
      int signalHeight = 0;

      for (int i = BarcodeImage.MAX_HEIGHT - 1; i >= 0; i--) 
      {
         if (this.image.getPixel(i, 0))
         {
            signalHeight++;
         } 
         else 
         {
            break;
         }
      }
      return signalHeight;
   }

   //moves the signal to the lower-left of the larger 2D array by calling
   //moveImage to LowerLeft
   private void cleanImage()
   {
      this.moveImageToLowerLeft();
   }

   //moves image to the lower left of the 2D array by calling
   //shiftImageDown and shiftImageLeft
   private void moveImageToLowerLeft()
   {

   }

   //shifts image down by a specified offset
   private void shiftImageDown(int offset)
   {
      for (int i = 0; i < BarcodeImage.MAX_WIDTH; i++) 
      {
         for (int p = BarcodeImage.MAX_HEIGHT; p >= 0; p--)
         {
            if (p - offset >= 0) 
            {
               Boolean oldPixel = this.image.getPixel(i, p-offset);
               this.image.setPixel(i, p, oldPixel);
               this.image.setPixel(i,  p-offset, false);
            }
         }
      }
   }

   //shift image to the left by a specified offset
   private void shiftImageLeft(int offset)
   {

   }

   //displays a cropped version of the original image leaving out whitespace
   public void displayImageToConsole()
   {
      for (int i = 0; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         for (int p = 0; p < BarcodeImage.MAX_WIDTH; p++)
         {
            Character character = this.image.getPixel(i, p) ? BLACK_CHAR : WHITE_CHAR;
            System.out.printf("%c", character);
         }

         if (i != (BarcodeImage.MAX_HEIGHT - 1))
         {
            System.out.print("\n");
         }
      }
   }

   public void displayTextToConsole()
   {
      this.translateImageToText(); 
      System.out.printf("***[%s]***\n", this.text);
   }

   public boolean generateImageFromText()
   {
      return true;
   }

   public boolean translateImageToText()
   {
      char colChar[] = new char[actualWidth];

      for (int i = 1; i < actualWidth - 1; i++)
      {
         colChar[i] = this.readCharFromCol(i);
      }

      String colString = new String(colChar);
      this.text = colString;

      return true;
   }

   public char readCharFromCol(int col)
   {
      int charValue = 0;
      int counter = 0;

      for (int i = BarcodeImage.MAX_HEIGHT - 2; i > BarcodeImage.MAX_HEIGHT - actualHeight; i--)
      {
         if (this.image.getPixel(i, col))
         {
            charValue = charValue + (int)Math.pow(2, counter);
         }

         counter++;
      }
      return (char)(charValue);
   }

   public static void main(String[] args) throws CloneNotSupportedException
   {
      String[] image =
         {
               "                                          ",
               "                                          ",
               "* * * * * * * * * * * * * * * * * * *     ",
               "*                                    *    ",
               "**** *** **   ***** ****   *********      ",
               "* ************ ************ **********    ",
               "** *      *    *  * * *         * *       ",
               "***   *  *           * **    *      **    ",
               "* ** * *  *   * * * **  *   ***   ***     ",
               "* *           **    *****  *   **   **    ",
               "****  *  * *  * **  ** *   ** *  * *      ",
               "**************************************    ",
               "                                          ",
               "                                          ",
               "                                          ",
               "                                          "

         };

      BarcodeImage myCode = new BarcodeImage(image);
      DataMatrix dataMatrix = new DataMatrix(myCode);
      dataMatrix.displayTextToConsole();
   }
}
