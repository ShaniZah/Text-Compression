package CompressionProject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//class of data structure for LZ77 compressed data.
class LZ77CompressedData {
    int offset;
    int length;
    char nextChar;

    
    //constructor for LZ77CompressedData
    public LZ77CompressedData(int offset, int length, char nextChar) {
        this.offset = offset;
        this.length = length;
        this.nextChar = nextChar;
    }
    //override toString to debug the code 
    @Override
    public String toString() {
        return String.format("Offset: %d, Length: %d, NextChar: %c", offset, length, nextChar);
    }
}

//LZ77 compression and decompression class
public class LZ77 {
	//size of window and buffer (in the Dynamic LZ77 this not will be final variables to adjust the size to each file)
    private static final int WINDOW_SIZE = 1024; // Sliding window size
    private static final int BUFFER_SIZE = 256; // Buffer size for search

    
    //compresses the input string using LZ77 algorithm.
    public static List<LZ77CompressedData> compress(String input) {
        List<LZ77CompressedData> compressedData = new ArrayList<>();
        int cursor = 0;

        while (cursor < input.length()) {
            int matchLength = 0;
            int matchDistance = 0;

            for (int j = Math.max(cursor - WINDOW_SIZE, 0); j < cursor; j++) {
                int k = 0;
                while (k < BUFFER_SIZE && cursor + k < input.length()
                        && input.charAt(j + k) == input.charAt(cursor + k)) {
                    k++;
                }

                if (k > matchLength) {
                    matchLength = k;
                    matchDistance = cursor - j;
                }
            }

            char nextChar = cursor + matchLength < input.length() ? input.charAt(cursor + matchLength) : '\0';
            compressedData.add(new LZ77CompressedData(matchDistance, matchLength, nextChar));
            cursor += matchLength + 1;
        }

        return compressedData;
    }

   
    
    //decompresses the list of LZ77CompressedData back to the original string
    public static String decompress(List<LZ77CompressedData> compressedData) {
        StringBuilder decompressed = new StringBuilder();

        for (LZ77CompressedData data : compressedData) {
            int start = Math.max(decompressed.length() - data.offset, 0);

            for (int i = 0; i < data.length; i++) {
                decompressed.append(decompressed.charAt(start + i));
            }

            if (data.nextChar != '\0') {
                decompressed.append(data.nextChar);
            }
        }

        return decompressed.toString();
    }

    //main method for testing LZ77 compression and decompression without other methods (Debug to check the code)
    public static void main(String[] args) {
        //file paths
        String inputFilePath = "src\\text.txt";
        String outputFilePath = "src\\compressed.lz77";

        //read input from file
        String input = readInputFromFile(inputFilePath);
        System.out.println("Original Size: " + input.getBytes().length + " bytes");

        //compress using LZ77
        List<LZ77CompressedData> compressedData = compress(input);
        System.out.println("LZ77 Compressed Tuples:");
        for (LZ77CompressedData data : compressedData) {
            System.out.println(data);
        }

        //calc LZ77 compressed size in bytes
        int compressedSize = compressedData.size() * 4; //2 bytes for offset -1 byte for length- 1 byte for nextChar
        System.out.println("LZ77 Compressed Size: " + compressedSize + " bytes");

        //write compressed data to file
        writeCompressedToFile(compressedData, outputFilePath);
        System.out.println("Compressed data written to: " + outputFilePath);

        //decompress
        String decompressed = decompress(compressedData);
        System.out.println("Decompressed Size: " + decompressed.getBytes().length + " bytes");

        //check if decompression method is correct 
        if (input.equals(decompressed)) {
            System.out.println("LZ77 Decompression successful!");
        } else {
            System.out.println("LZ77 Decompression failed.");
        }
    }

    //writes the compressed data to a file
    public static void writeCompressedToFile(List<LZ77CompressedData> compressedData, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            for (LZ77CompressedData data : compressedData) {
                //write the offset (using '0xFF' to isolate the lower 8 bits of integer and get a single byte from the number)
                fos.write((data.offset >> 8) & 0xFF); //higher byte (shift the bits 8 positions to the right to ensure the convert correctly)
                fos.write(data.offset & 0xFF); //lower byte (get lower bytes)

                //write 1 byte)
                fos.write(data.length & 0xFF);

                //write next char byte
                fos.write(data.nextChar & 0xFF);
            }
        } catch (IOException e) {
            System.out.println("Error writing compressed data to file: " + e.getMessage());
        }
    }

    
    //reads input from a file (just for debug)
    public static String readInputFromFile(String filePath) {
        StringBuilder input = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n"); //add newline char for each line
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        return input.toString().trim(); //remove the last newline char
    }
}
