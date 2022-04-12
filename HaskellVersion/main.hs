import qualified Prelude as P
import Data.ByteString as B --TODO make this qualified
import System.Directory as SD
import System.Exit
import Control.Monad as CM


correctPath :: P.String -> P.IO P.Bool
correctPath = SD.doesFileExist

getRowPaddingLength :: P.Int -> P.Int
getRowPaddingLength pixelRowLengthOfInfo
  | (pixelRowLengthOfInfo `P.mod` 4) P.== 0 = pixelRowLengthOfInfo
  | P.otherwise = pixelRowLengthOfInfo P.+ (4 P.- (pixelRowLengthOfInfo `P.mod` 4))


helperSlice :: P.Int -> P.Int -> P.String -> P.String
helperSlice begin end string=  P.take end P.$ P.drop begin P.$ string


-- printMe xs = P.foldr (P.++) "" (P.map (\str -> str P.++ "\n") xs)


parseArugements :: P.Int -> P.String -> [P.String]
parseArugements index string
  | index P.>= (P.length string) = [string]
  | (string P.!! index) P.== ' ' = [(helperSlice 0 (index ) string)] P.++ (parseArugements (index P.+ 1)  (helperSlice (index P.+ 1) (P.length string) string))
  | P.otherwise = parseArugements (index P.+ 1) string


helperSliceInt :: P.Int -> P.Int -> [P.Int] -> [P.Int]
helperSliceInt begin end string=  P.take end P.$ P.drop begin P.$ string


make2dArray :: [P.Int] ->  P.Int -> [[P.Int]]
make2dArray xs rowsize
   | xs P.== [] = []
   | (P.length xs) P.< rowsize  = [xs]
   | P.otherwise = [(helperSliceInt 0 rowsize xs)] P.++ (make2dArray (helperSliceInt rowsize (P.length xs) xs) rowsize )


averageIndices:: [P.Int] -> P.Int -> [P.Int]
averageIndices arr index
   | index P.>= ((P.length arr) P.- 2) = (helperSliceInt index (P.length arr) arr)
   | (index `P.mod` 3) P.== 0 = (P.replicate 3 ( ( (arr P.!! index) P.+ (arr P.!! (index P.+1)) P.+(arr P.!! (index P.+2)))  `P.div` 3)) P.++ (averageIndices arr (3 P.+ index))
-- (P.sum (helperSliceInt index (index P.+ 3) arr) )
forEachArrayGrayScale:: P.Int -> [[P.Int]] -> [[P.Int]]
forEachArrayGrayScale index array
   | index P.== (P.length array) = []
   | P.otherwise = [(averageIndices (array P.!! index) 0)] P.++ (forEachArrayGrayScale (index P.+ 1) array)


-- Ask user file   
-- Check if file correct 
-- if user types "-h" print helper string, then call main again 
-- if user types "-q" exit program
-- 
-- wrong ask user top type file in again
-- "-g"
-- if grey scale, means parse string
-- 


main = do
  --let pleaseWork = test
  --P.putStrLn ((pleaseWork P.head) P.++ "I would enjoy this")
  P.putStrLn "Please provide file path. If you need help type -h. -q to quit."
  usertTypedData <- P.getLine
  --let userInput = B.unpack input  
  let parsedUserInput = parseArugements 0 usertTypedData
  let userInput = parsedUserInput P.!! 0
  --P.print userInput
  --P.print parsedUserInput
  CM.when ((P.length parsedUserInput)  P.>= 3) (P.putStrLn "incorrect input... too many arugments")
  correctPathResult <- correctPath userInput 
  -- (parsedUserInput P.!! 0)
  
  if userInput P.== "-h" then do
    P.putStrLn "Hello!"
    P.putStrLn "This program converts an uncompressed 24-bit BMP file"
    P.putStrLn "To its negative."
    P.putStrLn "type file path of BMP file in console\nProgram turns file into negative BMP"
    P.putStrLn "Example type ./FILENAME.bmp"
    P.putStrLn "-q to quit program"
    main
  else if userInput P.== "-q" then do
    exitFailure
  else if correctPathResult then do  
    fileAsByteStringArray <- B.readFile userInput
    
    let fileAsWord8Array = B.unpack fileAsByteStringArray
    let endOfArray       = P.length fileAsWord8Array
    
    let firstByteShouldBe66  = fileAsWord8Array P.!! 0
    let secondByteShouldBe77 = fileAsWord8Array P.!! 1
    
    let slice from to xs = P.take (to P.- from P.+ 1) (P.drop from xs)
    
    let startPixelData      = fileAsWord8Array P.!! 10
    let asIntStartPixelData = P.fromIntegral startPixelData
    -- let attempt x = P.fromIntegral ::P.Word8
    --TODO make checks for proper file requirements
    --   1) If BMP correct first two bytes
    --   1) 24 bit file
    --   3) Uncompressed file

    -- [255- byte ]
    -- Consider Padding
    -- average RGB 3 bytes average --- skip Padding
    -- TODO if grey scale
    -- Implement grey scale
    --P.print (slice 0 (asIntStartPixelData P.- 1)  nuContent) 
    let headPartOfFile = (slice 0 (asIntStartPixelData P.- 1)  fileAsWord8Array)
    let endPixelArray  = (slice asIntStartPixelData (endOfArray P.- 1) fileAsWord8Array)
    let need      = P.map P.fromIntegral endPixelArray
    let start     = P.map P.fromIntegral headPartOfFile
    let backend   = P.map (255 P.-) need    
    
    -- GRAYSCALE WORRK STARTS
    -- _________________________
    if ((P.length parsedUserInput) P.> 1) then do 
      let pixelRowLengthOfInfo      = (P.sum (slice 18 21  start)) P.* 3
      let pixelRowLengthWithPadding = getRowPaddingLength pixelRowLengthOfInfo
      let greyScaled           = P.concat (forEachArrayGrayScale 0 (make2dArray need pixelRowLengthWithPadding ))
      --P.print (helperSliceInt 0 100 need)
      --P.putStrLn "____________"
      --P.print (helperSliceInt 0 100 greyScaled)
      let greyScaledByteString = P.map P.fromIntegral greyScaled 
      let greyScaledResult = headPartOfFile P.++ greyScaledByteString
      B.writeFile "greyScaledResult.bmp" (B.pack greyScaledResult)
    -- GRAYSCALE WORRK STARTS
    -- Else negative photo
    else do
      let backBack             = P.map P.fromIntegral backend
    -- P.print backend
    -- P.print backBack
    -- P.print result --Printoutput of resultant ByteArray
      let result           = headPartOfFile P.++ backBack
    --P.print pixelRowLengthOfInfo
    --P.print pixelRowLengthWithPadding
      B.writeFile "output.bmp" (B.pack result)
  else do
    P.putStrLn "Wrong input type, try again"
    main