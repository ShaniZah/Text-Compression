package CompressionProject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Class for combined compression using LZ77Dynamic and HuffmanCoding.
 */
public class CombinedCompression {
    private static HuffmanCoding huffmanCoding;

    /**
     * Compresses the input string using LZ77Dynamic followed by Huffman coding.
     * 
     * @param input the input string to compress
     * @return a byte array of the compressed data
     */
    public static byte[] compress(String input) {
        // Step 1: Dynamic LZ77 Compression
        LZ77Dynamic dynamicCompressor = new LZ77Dynamic(input);
        List<LZ77Dynamic.LZ77CompressedData> lz77Compressed = dynamicCompressor.compress(input);
        byte[] lz77ByteArray = lz77ToByteArray(lz77Compressed);

        // Debug: Print LZ77 byte array size
        System.out.println("LZ77 Byte Array Size: " + lz77ByteArray.length);

        // Step 2: Huffman Compression
        Map<Byte, Integer> frequencyMap = calculateFrequency(lz77ByteArray);
        huffmanCoding = new HuffmanCoding(); // Initialize HuffmanCoding instance
        huffmanCoding.buildHuffmanTree(frequencyMap);
        String huffmanEncoded = huffmanCoding.encode(lz77ByteArray);

        // Debug: Print Huffman encoded string length
        System.out.println("Huffman Encoded Length: " + huffmanEncoded.length());

        // Convert Huffman encoded binary string to byte array
        byte[] huffmanCompressedBytes = huffmanStringToBytes(huffmanEncoded);

        // Debug: Print Huffman compressed byte array size
        System.out.println("Huffman Compressed Byte Array Size: " + huffmanCompressedBytes.length);

        return huffmanCompressedBytes;
    }

    /**
     * Decompresses the given compressed byte array back to the original string.
     * 
     * @param compressedBytes the byte array to decompress
     * @return the decompressed string
     */
    public static String decompress(byte[] compressedBytes) {
        // Convert compressed bytes back to Huffman encoded string
        String huffmanEncoded = bytesToHuffmanString(compressedBytes);

        // Debug: Print Huffman encoded string (from bytes)
        System.out.println("Huffman Encoded (from bytes): " + huffmanEncoded);

        // Step 1: Huffman Decompression
        byte[] decodedBytes = huffmanCoding.decode(huffmanEncoded);

        // Debug: Print size of decoded bytes
        System.out.println("Decoded Bytes Size: " + decodedBytes.length);

        // Ensure that the decoded byte array length is a multiple of 4
        int padding = 4 - (decodedBytes.length % 4);
        if (padding != 4) { // If padding is needed
            byte[] paddedBytes = new byte[decodedBytes.length + padding];
            System.arraycopy(decodedBytes, 0, paddedBytes, 0, decodedBytes.length);
            decodedBytes = paddedBytes;
            System.out.println("Padded decoded bytes to length: " + decodedBytes.length);
        }

        // Step 2: Convert byte array back to LZ77CompressedData using the dynamic class
        List<LZ77Dynamic.LZ77CompressedData> lz77DecompressedDataDynamic = byteArrayToLz77Dynamic(decodedBytes);

        // Convert the dynamic compressed data to standard compressed data
        List<LZ77CompressedData> lz77DecompressedData = convertToStandardCompressedData(lz77DecompressedDataDynamic);

        // Step 3: LZ77 Decompression using the standard LZ77 class
        String decompressedString = LZ77.decompress(lz77DecompressedData);

        // Debug: Print the decompressed string
        System.out.println("Decompressed Content: " + decompressedString);

        return decompressedString;
    }

    /**
     * Converts Huffman encoded binary string to byte array.
     * 
     * @param huffmanEncoded the Huffman encoded string
     * @return the byte array representation
     */
    private static byte[] huffmanStringToBytes(String huffmanEncoded) {
        int length = (huffmanEncoded.length() + 7) / 8; // Calculate required byte array size
        byte[] huffmanCompressedBytes = new byte[length];
        for (int i = 0; i < huffmanEncoded.length(); i++) {
            if (huffmanEncoded.charAt(i) == '1') {
                huffmanCompressedBytes[i / 8] |= 1 << (7 - (i % 8)); // Set bit position
            }
        }
        return huffmanCompressedBytes;
    }

    /**
     * Converts byte array back to Huffman encoded string.
     * 
     * @param compressedBytes the byte array
     * @return the Huffman encoded string
     */
    private static String bytesToHuffmanString(byte[] compressedBytes) {
        StringBuilder huffmanEncoded = new StringBuilder();
        for (byte b : compressedBytes) {
            for (int i = 7; i >= 0; i--) {
                huffmanEncoded.append(((b >> i) & 1) == 1 ? '1' : '0'); // Convert each bit to character
            }
        }
        return huffmanEncoded.toString();
    }

    /**
     * Converts LZ77Dynamic.LZ77CompressedData list to byte array.
     * 
     * @param lz77Output the LZ77 compressed data
     * @return the byte array representation
     */
    public static byte[] lz77ToByteArray(List<LZ77Dynamic.LZ77CompressedData> lz77Output) {
        List<Byte> byteList = new ArrayList<>();
        for (LZ77Dynamic.LZ77CompressedData data : lz77Output) {
            byteList.add((byte) (data.offset >> 8)); // Higher byteoffset
            byteList.add((byte) (data.offset)); // Lower byteoffset
            byteList.add((byte) data.length); // Length of data 
            byteList.add((byte) data.nextChar); // Next char adding
        }
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }
        return byteArray;
    }

    /**
     * Converts byte array back to LZ77Dynamic.LZ77CompressedData list.
     * 
     * @param byteArray the byte array
     * @return the list of LZ77 compressed data
     */
    public static List<LZ77Dynamic.LZ77CompressedData> byteArrayToLz77Dynamic(byte[] byteArray) {
        List<LZ77Dynamic.LZ77CompressedData> lz77Data = new ArrayList<>();

        // Ensure the byte array is a multiple of 4
        if (byteArray.length % 4 != 0) {
            throw new IllegalArgumentException("Invalid LZ77 byte array length, must be a multiple of 4.");
        }

        for (int i = 0; i < byteArray.length; i += 4) {
            int offset = ((byteArray[i] & 0xFF) << 8) | (byteArray[i + 1] & 0xFF);
            int length = byteArray[i + 2] & 0xFF;
            char nextChar = (char) (byteArray[i + 3] & 0xFF);
            lz77Data.add(new LZ77Dynamic.LZ77CompressedData(offset, length, nextChar));
        }

        return lz77Data;
    }

    /**
     * Converts LZ77Dynamic.LZ77CompressedData to LZ77CompressedData.
     * 
     * @param dynamicData the dynamic compressed data
     * @return the standard compressed data
     */
    private static List<LZ77CompressedData> convertToStandardCompressedData(
            List<LZ77Dynamic.LZ77CompressedData> dynamicData) {
        List<LZ77CompressedData> standardData = new ArrayList<>();
        for (LZ77Dynamic.LZ77CompressedData data : dynamicData) {
            standardData.add(new LZ77CompressedData(data.offset, data.length, data.nextChar));
        }
        return standardData;
    }

    /**
     * Calculates the frequency map for Huffman coding.
     * 
     * @param inputBytes the input byte array
     * @return the frequency map
     */
    public static Map<Byte, Integer> calculateFrequency(byte[] inputBytes) {
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        for (byte b : inputBytes) {
            frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1);
        }
        return frequencyMap;
    }

    /**
     * Writes the decompressed data to a file.
     * 
     * @param decompressedData the decompressed data
     * @param filePath         the output file path
     */
    public static void writeDecompressedToFile(String decompressedData, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) { // Use UTF-8 encoding
            writer.write(decompressedData);
            System.out.println("Decompressed data written to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing decompressed data to file: " + e.getMessage());
        }
    }

    /**
     * Main method for testing combined compression.
     */
    public static void main(String[] args) {
        // File paths
        String inputFilePath = "src\\example1.txt";
        String compressedFilePath = "src\\compressed.bin";
        String decompressedFilePath = "src\\Dec.txt";

        // Read input from file
        String input = readInputFromFile(inputFilePath);
        System.out.println("Original Size: " + input.getBytes(StandardCharsets.UTF_8).length + " bytes");

        // Compress
        byte[] compressedBytes = compress(input);
        System.out.println("Compressed Size: " + compressedBytes.length + " bytes");

        // Save compressed data to file
        try (FileOutputStream fos = new FileOutputStream(compressedFilePath)) {
            fos.write(compressedBytes);
            System.out.println("Compressed data written to: " + compressedFilePath);
        } catch (IOException e) {
            System.out.println("Error writing compressed data to file: " + e.getMessage());
        }

        // Decompress
        String decompressed = decompress(compressedBytes);
        System.out.println("Decompressed Size: " + decompressed.getBytes(StandardCharsets.UTF_8).length + " bytes");

        // Write decompressed data to file
        writeDecompressedToFile(decompressed, decompressedFilePath);

        // Verify correctness
        if (input.equals(decompressed)) {
            System.out.println("Decompression successful!");
        } else {
            System.out.println("Decompression failed.");
        }
    }

    /**
     * Reads input from a file.
     * 
     * @param filePath the file path
     * @return the file content as a string
     */
    public static String readInputFromFile(String filePath) {
        StringBuilder input = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) { // Use UTF-8 encoding
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        return input.toString().trim();
    }
}
