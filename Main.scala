import Main.{BitCountException, CompressionException, NotBMPFileException}

import java.text.SimpleDateFormat
import java.awt.image.BufferedImage
import java.io.{BufferedOutputStream, DataInputStream, File, FileInputStream, FileNotFoundException, IOException}
import java.nio.file.{Files, InvalidPathException, Paths}
import java.util.{Date, Optional}
import javax.imageio.ImageIO
import scala.io.StdIn.readLine
import java.awt.Desktop
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.CompletableFuture




object Main extends App {

  class BitCountException(s: String) extends Exception(s) {}
  class CompressionException(s: String) extends Exception(s) {}
  class NotBMPFileException(s: String) extends Exception(s) {}


  //6. Add comments and explain code better
  //9. Put a video of using this and store on a s3 bucket and leave link in documentation
  //10. Add this to github
  //11. Cherry on the top, meanshift algorithem
  //13. TODO fix spelling every
  //14. Major refactor


  //TODO ASK ANDRE from level of importance
  // 1. Replit imports changes?
  // 2. how to use scalaDocs now that I have it... Should I wrote documentation in goolge.doc the link the html
  //    how does that work?
  // 3. (easy, quick) Showing stack trace to user or not?,
  //     ANSWER: REMOVE all stack straces
  // 4. (maybe some time) send show photo off on own thread so it doesn't end program
  //       This seem to fix itself?
  // 5. (medium time) Check if try and catch are proper
  // 6. (meh) other header sizes, should I only be consider for windows, I should write that in assumption
  // 7. fix warnings

  val inputHandler = new InputHandler

  inputHandler.introduction //Prints to console introduction

  def gameLoop(inputHandler: InputHandler, handCrafted :NegativeMake, negativeMaker:NegativeMaker, toContinue: Option[Boolean]):  Option[Boolean] = {
    val fileAndProccess: Tuple2[String, String] = inputHandler.userInputLoop()
    val success = NegativeMake().produceNegative(fileName = fileAndProccess._1, proccessType = fileAndProccess._2) // MAKE THIS RETURN TRUE OR FALSE
    //println("success result"+ success.getClass +"   "+ success)
    if (!success) {
      val nuImage = negativeMaker.producePhoto(fileName = fileAndProccess._1, proccessType = fileAndProccess._2)
      if(nuImage == null) {
        println("Try Again")
        return gameLoop(inputHandler, handCrafted , negativeMaker, toContinue)
      }
      negativeMaker.saveImage(fileName = fileAndProccess._1+fileAndProccess._2, nuImage.get)
      //return Some(true)
    }
      //TODO  ask User if want to view photo
      gameLoop(inputHandler, handCrafted , negativeMaker, toContinue)
  }

  gameLoop(inputHandler, handCrafted = new NegativeMake(), negativeMaker= new NegativeMaker, toContinue =Some(true))


  //REMINDER of things that have been tried, this worked well.
//  while(true) {
//
//    val fileAndProccess: Tuple2[String, String] = inputHandler.userInputLoop()
//    val success = NegativeMake().produceNegative(fileAndProccess._1)// MAKE THIS RETURN TRUE OR FALSE
//    if (!success){
//      val nuImage = negativeMaker.producePhoto(fileName = fileAndProccess._1, proccessType= fileAndProccess._2)
//      negativeMaker.saveImage(nuImage.get)
//     } else{
//      println()
//    }
//  }


}

//TODO make sure it takes in arguement correctly
/**
 * This class handles communication from console
 * Also check  if file path is correct
 */
class InputHandler{


  //TODO this is a great place for pattern matching
  //TODO note that this doesn't cover all cases, the if statements that is
  def userInputLoop(): Tuple2[String, String]={
    val userInput = getUserInput//TODO use pattern matching here
    if (userInput == "-h" || userInput == "-help") {helpCommand; return  userInputLoop()}
    if (userInput == "-q") System.exit(0)
    val userInputSplit = userInput.split(" ")
    if (userInputSplit.length >= 3) {
      println("Too many argument")
      return  userInputLoop()
    }
    if (!isPathValid(userInputSplit(0))) return  userInputLoop()
    if (userInputSplit.length == 2) return new Tuple2[String, String](userInputSplit(0), userInputSplit(1))
    new Tuple2[String, String](userInputSplit(0), "-n")
  }

  def getUserInput: String = {
      println("\nType the file path")
      println("confused? Type: -help")
      readLine("> ")
    }


  def isPathValid(path: String): Boolean = {
    try {
      if (Files.exists(Paths.get(path))) {
        println("Valid File Path")
        true
      } else {
        println("inValid File Path")
        false}
    }
    catch {
      case ex: InvalidPathException =>
        println("Invalid file path, please try again. Or try -help")
        false
      case _  =>
        println("Invalid file path, please try again. Or try -help")
        false
    }
  }
  def introduction: Unit = {
    println("Hello")
    println("This program takes in an BMP image and produces it's negative or grey scale, dependent on the user's input")
    println("A prompt will ask for the file path, relative or absolute path will be accepted from user.")
    println("If accepted user photo will automatically be turned into negative")
    println("To make grey scale, at the end of the file path type \'-g\'")
    println("Example to produce grey scale photo from relative path: ./image.BMP -g ")
    println("Anymore questions? type -h for help and -q to quit program")
  }

  def helpCommand: Unit = {
    println("This program takes in an image and produces it's negative or grey scale, dependent on the user's input")
    println("In case you are wondering working directory of initialized program = " + System.getProperty("user.dir"))
    println("You can put your BMP images there and set the relative path like: ./YOURIMAGENAME.BMP")
    println("Or you can copy the absolute path of the file and paste it into the console")
    println("The program will produce negative, but you can give it the arguement to produce greyscale")
  }



}

/**
 * reads file and converts file to image buffer
 * Changes to image to greyscale or negative
 * Can save file
 * Also show file
 */
class NegativeMaker {

  //var image :BufferedImage;
  val mapOFRGBfucntions = Map[String, (Int) => Int]("-g" -> getGreyedRGB _, "-n" -> getNegativeRGB _ )

  def producePhoto(fileName: String, proccessType: String): Option[BufferedImage] = {
    //val optionBufImage =
    try {
      getPhotoFromFile(fileName = fileName) match {
        case Some(x) => Some(iterativeGreyShade(x, proccessType))
        //keeps switching on me?
        case _ => null
      }
    } catch{
      e =>{
        println("Produced photo failed in midst of production")
        null
      }
    }
  }


  def getPhotoFromFile(fileName: String): Option[BufferedImage] = {
      try {
        //print(fileName)
        Some(ImageIO.read(new File(fileName)))
      } catch {
        case e: FileNotFoundException => {
          println("Couldn't find that file.");
          //e.printStackTrace;
          null
        }
        case e: IOException => {
          println(" Had an IOException trying to read that file");
          //e.printStackTrace;
          null
        }
        case _ =>{println("error...")
           null
        }
      }
    }



  /**
   *
   * @param rgbColor
   * @return Int => new greyed color
   */
  def getGreyedRGB(rgbColor: Int): Int = {
      val p: Int  = rgbColor //TODO remove P variable
      val a: Int  = (p >> 24) & 0xff
      val r: Int   = (p >> 16) & 0xff
      val g: Int   = (p >> 8) & 0xff
      val b: Int   = p & 0xff
      val avg = (r + g + b) / 3
      (a << 24) | (avg << 16) | (avg << 8) | avg
    }


  /**
   *
   * @param rgbColor
   * @return
   */
  def getNegativeRGB(rgbColor: Int): Int = {
      val p: Int = rgbColor
      val a: Int = (p >> 24) & 0xff
      var r: Int = (p >> 16) & 0xff
      var g: Int = (p >> 8) & 0xff
      var b: Int = p & 0xff

      // subtract RGB from 255
      r = 255 - r
      g = 255 - g
      b = 255 - b

      // set new RGB value
      (a << 24) | (r << 16) | (g << 8) | b
    }


  //TODO to make this functional would I make this recursive?
  //Or a better recursive way?
  def iterativeGreyShade(image: BufferedImage, proccessType: String): BufferedImage = {
    val result = image
    for (i <- 0 until result.getWidth; j <- 0 until result.getHeight) result.setRGB(i, j, mapOFRGBfucntions(proccessType)(result.getRGB(i, j)))
    //for (i<- 0 until width) for (j <- 0 until length ) result.setRGB(i, j, getNegativeRGB(result.getRGB(i,j)))
    result
  }

  def askUserIfTheyWouldLikeToSeePhoto: String = {
    println("\nWould you like to the photo generated")
    println("If yes type \'y\' otherwise type anything else")
    println("Note do select to see photo, program will terminate ")
    readLine("> ")
  }


  //ToDO slit this function up to save and show
  def saveImage(fileName:String, bufferedImage: BufferedImage): Boolean = {
    val timeStamp:String = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())
    val nuFileName = "./" +fileName + timeStamp+ ".BMP"
    val saveAs = new File(nuFileName)
    ImageIO.write(bufferedImage, "BMP", saveAs)

    //TODO Send this task off in a thread so it doesn't crash entire program
    if (askUserIfTheyWouldLikeToSeePhoto == "y") {
      CompletableFuture.runAsync(() => {
        try {
          Desktop.getDesktop().open(new File(nuFileName));
        }
        catch e => false
      });
      Thread.sleep(2000);
      //Desktop.getDesktop.open(new File(nuFileName))
    }
    true
  }

}

/**
 *
 */
class NegativeMake  {


  //Opens file, reads the data
  //if (! verifyBMPfile(targetStream)) println("Not a proper BMP file")//TODO implement another the other way to do this

  def byteToUnsignedInt(byte: Byte): Int = Integer.parseInt(String.format("%02X", byte), 16)


  def getInputStream(fileName: String): Array[Byte] = {
    val  initialFile = new File(fileName)
    new DataInputStream(new FileInputStream(initialFile)).readAllBytes()
  }

  /**
   * Handcrafted negative BMP producer for non compressed 24 bit BMP files
   * This does this in about 4 sets
   * step1: Open file input stream, returns Array of Bytes
   * step2: Parse header, check if this is a BMP file that meets conditions/assumptions to make BMP
   * step3: Make the pixels associated with pixel color negative
   * step4: Write to file
   * step5: If above was success return true otherwise throw error and return false
   * @param fileName  file path of BMP file to be transformed
   * @return returns true if success save and false otherwise
   */
  def produceNegative(fileName: String, proccessType: String): Boolean ={
    //Step 1: Open file, as inputstream and return as an Array of Bytes
    try {
      var byteArray = getInputStream(fileName) //TODO change to val?

      //step 2: Parse Header
      if ( byteArray(0) != 66 || byteArray(1) != 77) { //This checks if it has the BMP signature that all BMP files myst have
        println("Not a proper BMP, swithcing proccess, switching to Java image proccessing library to see if it can handle the job")
        throw new NotBMPFileException("Error with header, not BMP file!!!")
      }

      val bmpByteSize = byteArray.slice(2, 6).map(x => byteToUnsignedInt(x)  ).sum
      val startPixelData = byteArray.slice(10, 14).sum// """The offset, i.e. starting address, of the byte where the bitmap image data (pixel array) can be found.""" -wiki
      val sizeOfHeader = byteArray.slice(14, 18).sum//TODO this is import... This changes the code dependent on what this is
      val width  = byteArray.slice(18, 22).map(x => byteToUnsignedInt(x)  ).sum
      val length = byteArray.slice(22, 26).map(x => byteToUnsignedInt(x)  ).sum
      val colorPlanes = byteArray.slice(26, 28).map(x => byteToUnsignedInt(x)  ).sum//Must be 1
      val bitCount = byteArray.slice(28, 30).map(x => byteToUnsignedInt(x)  ).sum//must be 24 or throw error
      val compressionDepth = byteArray.slice(30, 34).map(x => byteToUnsignedInt(x)  ).sum// must be zero or throw error
      val bitmapSize = byteArray.slice(34, 38).map(x => byteToUnsignedInt(x)  ).sum//Bitmap size
      val horizantalResolutionSize = byteArray.slice(38, 42).map(x => byteToUnsignedInt(x)  ).sum
      val verticalResolutionSize = byteArray.slice(42, 46).map(x => byteToUnsignedInt(x)  ).sum
      val pixelRowLengthOfInfo = byteArray.slice(18, 22).map(x => byteToUnsignedInt(x)  ).sum * 3
      val pixelRowLengthWithPadding = if (pixelRowLengthOfInfo % 4 == 0) pixelRowLengthOfInfo  else pixelRowLengthOfInfo +(4- (pixelRowLengthOfInfo % 4))
      val pixelInfoAreaSize = pixelRowLengthWithPadding * length

      if( bitCount != 24){
        println("This program only handles 24 bit BMP files, this file is BMP, but not 24bit")
        println("We will use bufferedImage library in this case and not our hand crafted code")
        throw new BitCountException("Bitcount is not 24!")
      }


      println("______________BMP header info ______________________")
      println("bmpByteSize: " + bmpByteSize.toString+ " startPixelData: " + startPixelData.toString)
      println("colorPlanes: "+ colorPlanes.toString + " bitCount: " +bitCount.toString)
      println("width: "+ width.toString + " Height: " +length.toString)
      println("pixelRowLengthOfInfo: " + pixelRowLengthOfInfo.toString + " pixelRowLengthWithPadding: " + pixelRowLengthWithPadding )
      println("\n pixelInfoAreaSize  " + pixelInfoAreaSize.toString)
      println("____________________________________________________")


      //step 4: Check if passes conditions are to convert, ie: non compressed imaged, 24 bit
      if(compressionDepth != 0){
        println("Sorry, but our hand crafted negative BMP photo maker only does non compressed photos")
        println("We will use bufferedImage library in this case and not our hand crafted code")
        throw new CompressionException("This is a compressed file! We only handle non-compressed file")
      }



    //step 4: Tell each pixel to negative itself

      val endNegativePixel = startPixelData.toInt+ pixelInfoAreaSize

    /**
     * Takes in Array[Byte] then make pixel
     * @param k currentPixel
     * @param byteArrr Array of pixels to change
     * @return Returns the modified pixel array, negaitve
     */
      def makePixelAreaNegativeRecursion(k:Int, byteArrr:  Array[Byte]): Array[Byte] = {
        if( k >= endNegativePixel ) return byteArrr
        (k until k + pixelRowLengthOfInfo).foreach{ k =>  byteArrr(k) = (255 - byteToUnsignedInt(byteArrr(k))).toByte}
        makePixelAreaNegativeRecursion(k+ pixelRowLengthWithPadding, byteArrr)
      }

      def makeGreyScale(k:Int, byteArrr:  Array[Byte]): Array[Byte] = {
        if( k >= endNegativePixel ) return byteArrr
        for ( index <- Range(k, k + pixelRowLengthOfInfo, 3) ) {
          val slicedArr = byteArrr.slice(index, index+3)
          val sumPixelColor: Int = slicedArr.reduce( (x, y) => (x.toInt + y.toInt).toByte ).toInt
          val averagePixelColor = (sumPixelColor/3).toInt
          (index until index + 3).foreach{ saveIndex =>  byteArrr(saveIndex) = averagePixelColor.toByte}
        }
        makeGreyScale(k+ pixelRowLengthWithPadding, byteArrr)
      }

      val mapOfPixelAreaTransformationFunctions = Map[String, (Int, Array[Byte]) => Array[Byte]]("-g" -> makeGreyScale _ , "-n" -> makePixelAreaNegativeRecursion _ )

      val negativeByteArray =  mapOfPixelAreaTransformationFunctions(proccessType)(startPixelData.toInt, byteArray)


    //Write to file
      write(new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())+proccessType+".BMP", negativeByteArray)
      true
    }
     catch {
       case e: IOException => {
         println(" Had an IOException trying to read that file");
         e.printStackTrace
         println
         false
       }
       case e => {
         println("Error Happend while trying create BMP");
         println(e.printStackTrace)
         false
       }
     }
    //TODO MAKE FUNCTIONAL
    //var i = startPixelData.toInt
//    while( i < endNegativePixel ){
//      val end = i + pixelRowLengthOfInfo
//      while(i < endNegativePixel && i <  end) {
//        byteArray(i) =  (255 - Integer.parseInt(String.format("%02X", byteArray(i)), 16)).toByte
//        i += 1
//      }
//      i+= pixelRowLengthWithPadding - pixelRowLengthOfInfo
//    }

    //TODO take out of
    //write(new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())+".BMP", byteArray)

    //Remember flush your buffered stream.


    //step 5: Close file buffer
  }

  //output of file, output to specified file name
  def write(fileName: String, byteArray: Array[Byte]): Unit ={
    //step 1: open file name, as buffered output stream
    try {
      val out = new FileOutputStream(fileName)
      try {
        out.write(byteArray)
        out.flush()
        out.getFD.sync()
      }
      catch _ => println("Writing error")
      finally if (out != null) out.close()
    } catch _ => println("error creating file")
    println("Success file production")

  }


  def verifyBMPfile(inputStream: DataInputStream): Boolean = if (inputStream.read() == 66 && inputStream.read() ==7) true else false




}