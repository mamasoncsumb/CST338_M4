public class BarcodeImage implements Cloneable 
{
   public static final int MAX_HEIGHT = 30;
   public static final int MAX_WIDTH = 65;
   private boolean[][] image_data;
   
   public BarcodeImage()
   {
      this.image_data = new boolean[MAX_HEIGHT][MAX_WIDTH];
   }
   
   public BarcodeImage (String[] str_data)
   {   
      this.image_data = new boolean[MAX_HEIGHT][MAX_WIDTH];
      
      int barStopRow = 0, barStopCol = 0, barStartRow = 0, barStartCol = 0;
      boolean breakflag = false;
      
      if(checkSize(str_data))
      {
         //Find str_data barcode end point
         for(int i = str_data.length -1; i >= 0; i--)
         {
            for (int j = str_data[0].length() - 1; j >=0; j--)
            
               if(str_data[i].charAt(j) == '*')
               {
                  barStopRow = i;
                  barStopCol = j;
                  breakflag = true;
                  break;
               }
            if(breakflag == true)
            {
               break;
            }
         }
      
         breakflag = false;
         //find str_data barcode start point
         for(int i = 0 ; i < str_data.length; i++)
         {
            for (int j = 0; j < str_data[0].length() ; j++)
            
               if(str_data[i].charAt(j) == '*')
               {
                  barStartRow = i;
                  barStartCol = j;
                  breakflag = true;
                  break;
               }
            if(breakflag == true)
            {
               break;
            }
         }

         int colIndex;
         for(int m = barStartRow; m <= barStopRow; m++)
         {
            colIndex = 0;
            for(int n = barStartCol; n <= barStopCol; n++)
            {
               this.setPixel(MAX_HEIGHT -(barStopRow - m) - 1, colIndex++, 
                  (str_data[m].charAt(n) == '*') ? true : false);
            }
         }
      }
   }
   
   public Object clone()
   {
      try 
      {
         BarcodeImage copy = (BarcodeImage)super.clone();
         
         //clone performs a shallow copy on the array and requires looping through outer array and cloning inner contents
         copy.image_data = this.image_data.clone();
         for(int i = 0; i < this.image_data.length; i++)
         {
            copy.image_data[i] = this.image_data[i].clone();
         }

            return copy;
        } 
      catch (CloneNotSupportedException e) 
      {
           e.printStackTrace();
           throw new RuntimeException();
       }
   }
   
   //validate parameters and return pixel value if good, if not return false
   public boolean getPixel(int row, int col)
   {
      return  (row >= 0 && row < MAX_HEIGHT && col >= 0 
         && col < MAX_WIDTH ) ? this.image_data[row][col] : false;
   }
   
   //validates parameters and sets pixel value if good and returns false if not
   public boolean setPixel(int row, int col, boolean value)
   {
      if (row >= 0 && row < MAX_HEIGHT && col >= 0 && col < MAX_WIDTH )
      {
         this.image_data[row][col] = value;
         return true;
      }
      else
      {
         return false;
      }
   }
   
   //verifies string data does not exceed max value data members
   boolean checkSize(String[] data)
   {
      return (data.length > MAX_HEIGHT || data[0].length() > 
         MAX_WIDTH || data == null) ? false : true;
   }
   
   //display contents of image_data in 0s and 1s
   void displayToConsole()
   {
      for(int i = 0; i < MAX_HEIGHT; i++)
      {
         for (int j = 0; j < MAX_WIDTH; j++)
         {
            System.out.print((this.image_data[i][j] == true) ? "[1]" : "[0]");
         }
         System.out.println();
      }
   }
}