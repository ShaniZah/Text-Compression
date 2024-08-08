package CompressionProject;


import java.util.ArrayList;
import java.util.List;


//lz77 class that uses Optimized window and buffer size for better compression (Flexible for more text files) 
public class LZ77Dynamic {

    private int windowSize;
    private int bufferSize;

    //constructor that adjusts parameters based on strings input
    public LZ77Dynamic(String input) {
        adjustParameters(input);
    }

    //adjusts the window and buffer sizes based on input entropy and redundancy (The dynamic part of the regular lz77 that we know from class)
    private void adjustParameters(String input) {
        double entropy = LZ77Optimizer.calculateEntropy(input);
        double redundancy = LZ77Optimizer.estimateRedundancy(input);

        //adjust window size based on redundancy with refined threshold
        if (redundancy > 0.3) {
            windowSize = Math.min(4096, input.length() / 2); //increase window size
        } else {
            windowSize = 512; //smaller window size for less redundancy
        }

        //adjust buffer size based on entropy with refined threshold
        if (entropy < 3.5) {
            bufferSize = 512; //larger buffer for lower entropy
        } else {
            bufferSize = 128; //default buffer size (same as the regular LZ77)
        }
        //print in console to see the changes for different text files (examples in src folder of the project)
        System.out.println("Adjusted Window Size: " + windowSize);
        System.out.println("Adjusted Buffer Size: " + bufferSize);
    }

    
    //compression method using adjusted window and buffer sizes
    public List<LZ77CompressedData> compress(String input) {
        List<LZ77CompressedData> compressedData = new ArrayList<>();
        int cursor = 0;

        while (cursor < input.length()) {
            int matchLength = 0;
            int matchDistance = 0;

            //search for matches in the current size of sliding window(after adjustments)
            for (int j = Math.max(cursor - windowSize, 0); j < cursor; j++) {
                int k = 0;
                while (k < bufferSize && cursor + k < input.length() && input.charAt(j + k) == input.charAt(cursor + k)) {
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
    //public inner class to represent compressed data (same as in the regular lz77 method)
    public static class LZ77CompressedData {
        public int offset;
        public int length;
        public char nextChar;

        //constructor
        public LZ77CompressedData(int offset, int length, char nextChar) {
            this.offset = offset;
            this.length = length;
            this.nextChar = nextChar;
        }
        //to debug to code lettter 
        @Override
        public String toString() {
            return String.format("Offset: %d, Length: %d, NextChar: %c", offset, length, nextChar);
        }
    }
}

