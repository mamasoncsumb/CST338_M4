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
      this();
      Scan(image);
   }

   //sets the text but leaves the image at its default value
   public DataMatrix(String text)
   {
      this();
      this.readText(text);
   }

   //Takes in a string variable and sets the data member "text"
   public boolean readText(String text)
   {
      this.text = text;
      return true;
   }

   //Sets image and calls helper methods to determine data members actualWidth and actualHeight
   public boolean Scan(BarcodeImage image)
   {
      try
      {
         this.image = (BarcodeImage)image.clone();
         this.cleanImage();
         this.actualWidth = this.computeSignalWidth();
         this.actualHeight = this.computeSignalHeight();
         return true;
      }
      catch(Exception e)
      {
         return false;
      }
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
      int signalStartColumn = 0;
      int signalStartRow = 0;
      boolean spineFound = false;
      
      while(!spineFound)
      {
         for (int i = BarcodeImage.MAX_HEIGHT; i > 0; i--)
         {
            for(int j = 0; j < (BarcodeImage.MAX_WIDTH - 1); j++)
            {
               if(image.getPixel(i, j))
               {
                  signalStartRow = i;
                  signalStartColumn = j;
                  spineFound = true;
                  break;
               }
            }
            
            if(spineFound)
            {
               break;
            }
         }
      }

      int row = BarcodeImage.MAX_HEIGHT - 1;
      for (int i = signalStartRow; i > 0; i--)
      {
         int col = 0;
         for(int j = signalStartColumn; j < BarcodeImage.MAX_WIDTH; j++)
         {
            boolean tempValue = image.getPixel(i, j);
            image.setPixel(row, col, tempValue);
            col++;
         }
         row--;
      }
   }

   //displays a cropped version of the original image leaving out whitespace
   public void displayImageToConsole()
   {
      for(int i = actualWidth + 2; i > 0; i--)
      {
         System.out.print("-");
      }
      
      System.out.print("\n");
      for (int i = BarcodeImage.MAX_HEIGHT - actualHeight; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         System.out.print("|");
         for (int j = 0; j < actualWidth; j++)
         {
            if(image.getPixel(i, j))
            {
               System.out.print(BLACK_CHAR);
            }
            else
            {
               System.out.print(WHITE_CHAR);
            }
         }
         System.out.print("|" + "\n");
      }
   }

   public void displayTextToConsole()
   { 
      System.out.printf("\n***[%s]***\n", this.text);
   }
   
   public void displayRawImage()
   {
      for(int i = BarcodeImage.MAX_WIDTH + 2; i > 0; i--)
      {
         System.out.print("-");
      }
      
      System.out.print("\n");
      for (int i = 0; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         System.out.print("|");
         for (int j = 0; j < BarcodeImage.MAX_WIDTH; j++)
         {
            if(image.getPixel(i, j))
            {
               System.out.print(BLACK_CHAR);
            }
            else
            {
               System.out.print(WHITE_CHAR);
            }
         }
         System.out.print("|" + "\n");
      }
   }

   private void clearImage()
   {
      for (int i = 0; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         for (int j = 0; j < BarcodeImage.MAX_WIDTH; j++)
         {
            image.setPixel(i, j, false);
         }
      }
   }
   
   public boolean translateImageToText()
   {
      char colChar[] = new char[actualWidth - 1];
      
      for (int i = 1; i < actualWidth - 1; i++)
      {
         colChar[i] = this.readCharFromCol(i);
      }
      String colString = new String(colChar);
      this.text = colString;
      return true;
   }
   
   private char readCharFromCol(int col)
   {
      int charValue = 0;
      int counter = 0;
      
      for(int i = BarcodeImage.MAX_HEIGHT - 2; i > BarcodeImage.MAX_HEIGHT - actualHeight; i--)
      {
         if(this.image.getPixel(i, col))
         {
            charValue = charValue + (int)Math.pow(2, counter);
         }
         counter++;
      }
      return (char)(charValue);
   }
   
   public boolean generateImageFromText()
   {
      char[] textArray = this.text.toCharArray();
      int MAX_ACTUAL_HEIGHT = 10;
      int textLength = textArray.length;
      
      //Clear current image
      this.clearImage();

      //Write left Closed Limitation Line and right Open Borderline
      for(int i = BarcodeImage.MAX_HEIGHT - 1; i > BarcodeImage.MAX_HEIGHT - 1 - MAX_ACTUAL_HEIGHT; i--)
      {
         this.image.setPixel(i, 0, true);
         if(i % 2 == 1)
         {
            this.image.setPixel(i, textLength + 1, true);
         }
         else
         {
            this.image.setPixel(i, textLength + 1, false);
         }
      }
      //Write text to image
      for(int i = 0; i < textLength; i++)
      {
         int charValue = (int)textArray[i];
         this.writeCharToCol(i + 1, charValue);
      }
      //Write top Open Borderline
      for(int i = 0; i < textLength + 2; i++)
      {
         if(i % 2 == 0)
         {
            this.image.setPixel(BarcodeImage.MAX_HEIGHT - MAX_ACTUAL_HEIGHT, i, true);
         }
         else
         {
            this.image.setPixel(BarcodeImage.MAX_HEIGHT - MAX_ACTUAL_HEIGHT, i, false);
         }
      }
      //Set image width and height
      this.actualWidth = this.computeSignalWidth();
      this.actualHeight = this.computeSignalHeight();
      
      return false;
   }
   
   public boolean writeCharToCol(int col, int code)
   {
      //Convert code to binary string
      String binary = Integer.toBinaryString(code);
      
      //Convert binary string to char array
      char colChar[] = new char[binary.length()];
      colChar = binary.toCharArray();
      
      //Set characer array iterator to initial value
      int counter = colChar.length - 1;
      
      //Write bottom Closed Limitation Line pixel
      this.image.setPixel(BarcodeImage.MAX_HEIGHT - 1, col, true);
      
      //Begin writing character to column
      for(int i = BarcodeImage.MAX_HEIGHT - 2; i > 0; i--)
      {
         //Write binary code image
         if(counter >= 0 || i > BarcodeImage.MAX_HEIGHT - 1)
         {
            if (colChar[counter] == '1')
            {
               image.setPixel(i, col, true);
            }
            else
            {
               image.setPixel(i, col, false);
            }
         }
         //Fill the rest of the column with blanks
         else
         {
            image.setPixel(i, col, false);
         }
         //Increase colChar iterator
         counter--;
      }
      return true;
   }
}
