# TurnBMPNegative

### Garrett Peuse

# Table of Contents
1. [Introduction](#introduction)
2. [Build Instructions](#build-to-run-instructions)
3. [Documentation](#documentation)
4. [Considerations](#considerations)
5. [Third Example](#third-example)
6. [Demonstration](#Demonstration)




## Introduction 
This program is written in Scala with the main objective of transforming a BMP file to its RGB negative or grey scale. This is done by the user inputting the file name of the  desired file to be transformed in the console then the program verifies if it is indeed a valid file that can be transformed and produces a new BMP file if it is valid.


## Build Instructions <a name="build-instructions" />
###
Multiple build options to run, but listed are the two easiest.
1. Most easily from replit.com: https://replit.com/@mrgarrettp/Main
2.  easiest is from command line by compiling main.scala then running, but this will require user to check requirements(JAVA 11+ and Scala 2.13+)

### Option 1. Most easily from replit.com
 You can simply go to https://replit.com/@mrgarrettp/Main . Then hit play or the green arrow. No replit account needed. It should be noted though this program was built with Java 11, but replit uses a Java version less 11 for its Scala IDE.  Therefore some of the code needed to be changed in replit (IE replace JAVA 11 compatible functions with older functions).



### Option 2. Command line
This is relatively easy. The only consideration here is the use of Java 11 and Scala 2.13+.
To attain Main.scala go to: https://github.com/enigmatized/TurnBMPNegative
To check java version type in the console: `java -version`
 To check scala version type in the console: `scala`

If the compatible Java and Scala version are obtained then in the terminal/command line/powershell simply be in the file directory of where the Main.scala file is located run the command: 
`scalac Main.scala`
Note there may be a few warnings depending on the version of Scala, but this shouldn’t be a concern.
Then simply run the command:
        `Scala Main`

A prompt in the command line will follow created by the program.



## Documentation
ScalaDocs was used to document functions and classes.
To easily view this go to: https://github.com/enigmatized/TurnBMPNegative/tree/main/BMPTransformerScalaDocs

Download the ZIP file then enter the api folder. From there you can start at index.html.

## Considerations
#### Important Notes
This program can only work with specified BMP file formats for the hand crafted BMP converter to work. Specifically those specifications are
1. The file has proper BMP header file tag, i.e. first two bytes represent 66 and 77, in that order.
2. Uncompressed BMP
3. That it is 24 bit.
4. There is also assumption this is a Windows formatted BMP(other version are pretty outdated, such as OS/2)

Now if those assumptions are not met, an error will be thrown, but the program will continue.
The program continues with a non-hand crafted BMP input reader, i.e. Java’s BufferImage class for processing images. From there it does the appropriate action for either greyscale or negative photo formation.



## Example2
## Third Example
## [Demonstration]
I made this video showing the working product. Check it out here: 
Sorry, no video editing. One fast and dirty take.

