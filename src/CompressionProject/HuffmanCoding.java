package CompressionProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.FileOutputStream;

//node class used in Huffmantreee in huffmancoding.
class HuffmanNode {
    byte data; //this stores the byte val
    int frequency; //frequency of occurrence of byte
    HuffmanNode left; //left child in the huffman tree
    HuffmanNode right; // right child in the Huffman tree

    // constructor for huffmanNode class.
    public HuffmanNode(byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }
}


//huffman code class for compressing and decompressing data of bytes.
public class HuffmanCoding {

    private Map<Byte, String> huffmanCodeMap = new HashMap<>(); // map to store the huffman code for each byte
    private Map<String, Byte> reverseHuffmanCodeMap = new HashMap<>(); // map to store the reverss for decoding

   
   //Builds the huffman tree using the frequency map in code.
    public void buildHuffmanTree(Map<Byte, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(a -> a.frequency) // Comparator to order nodes by frequency
        );

        //add all nodes to the priority queue Structure
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        //build the huffman tree
        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll(); //remove node with lowest frequency
            HuffmanNode right = priorityQueue.poll(); //remove node with next lowest frequency

            //make a new internal node with these two nodes as childrens 
            HuffmanNode combined = new HuffmanNode((byte) 0, left.frequency + right.frequency);
            combined.left = left;
            combined.right = right;

            //add the new node back into the priority queue structure
            priorityQueue.offer(combined);
        }

        //the remaining node is the root of the huffman tree struct
        HuffmanNode root = priorityQueue.poll();
        buildCodeMap(root, "");
    }

    
    //recursively makes the hffman code map from the tree.
    private void buildCodeMap(HuffmanNode node, String code) {
        if (node == null) {
            return;
        }

        //if its a leaf node, store the code in the map struct
        if (node.left == null && node.right == null) {
            huffmanCodeMap.put(node.data, code);
            reverseHuffmanCodeMap.put(code, node.data);
        }

        //recursively build the code map for left and right subtrees in Data structure above
        buildCodeMap(node.left, code + "0");
        buildCodeMap(node.right, code + "1");
    }

   
    //encodes the input byte array using the huffman code map.
    public String encode(byte[] input) {
        StringBuilder encoded = new StringBuilder();

        for (byte b : input) {
            encoded.append(huffmanCodeMap.get(b)); //add the Huffman code for each byte next
        }

        return encoded.toString();
    }

    
    //decodes a Huffman encoded string back to a byte array.
    public byte[] decode(String encoded) {
        StringBuilder currentCode = new StringBuilder();
        List<Byte> decodedBytes = new ArrayList<>();

        for (char c : encoded.toCharArray()) {
            currentCode.append(c); //makes the current huffman code
            if (reverseHuffmanCodeMap.containsKey(currentCode.toString())) {
                decodedBytes.add(reverseHuffmanCodeMap.get(currentCode.toString())); //finds the byte for this code
                currentCode.setLength(0); // reset the current code
            }
        }
        byte[] byteArray = new byte[decodedBytes.size()];
        for (int i = 0; i < decodedBytes.size(); i++) {
            byteArray[i] = decodedBytes.get(i); //converts the lists to a byte array
        }

        return byteArray;
    }

    
     
    //saves all the compressed data to a file
    private static void saveCompressedData(String encodedData, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] bytes = new byte[(encodedData.length() + 7) / 8];
            for (int i = 0; i < encodedData.length(); i++) {
                if (encodedData.charAt(i) == '1') {
                    bytes[i / 8] |= 1 << (7 - (i % 8));
                }
            }
            fos.write(bytes);
            System.out.println("compressed data in the file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing compressed data to file: " + e.getMessage());
        }
    }

    
    //main to test huffman coding alone without other algoritems
    public static void main(String[] args) {
        // Read input from file
        String inputFilePath = "src\\text.txt";
        String input = readInputFromFile(inputFilePath);
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

        //build a frequency map of the bytes in the input string
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        for (byte b : inputBytes) {
            frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1);
        }

        HuffmanCoding huffmanCoding = new HuffmanCoding();
        huffmanCoding.buildHuffmanTree(frequencyMap); // make the huffman tree

        //Encode using huffman
        String encoded = huffmanCoding.encode(inputBytes);
        System.out.println("Huffman Encoded: " + encoded);

        // Calc huffman compressed size file
        int compressedSize = encoded.length() / 8; //size of bytes for test only
        System.out.println("Huffman Compressed Size: " + compressedSize + " bytes");

        //saves compressed data to file "compressed/bin" to see manually the file in folder
        String compressedFilePath = "C:\\Eclipse\\final_Project\\src\\compressed.bin";
        saveCompressedData(encoded, compressedFilePath);

        //decode the encoded string
        byte[] decoded = huffmanCoding.decode(encoded);
        String decompressed = new String(decoded, StandardCharsets.UTF_8);
        System.out.println("Decompressed Size: " + decompressed.length() + " characters");

        // Verify correctness
        if (input.equals(decompressed)) {
            System.out.println("huffman Decompression successful!");
        } else {
            System.out.println("Huffman Decompression failed.");
        }

        //calc and print compression ratio
        int originalSize = inputBytes.length; //original size in bytes for test only
        double compressionRatio = (double) compressedSize / originalSize;
        double compressionSavings = (1 - compressionRatio) * 100;

        System.out.println("Original Size: " + originalSize + " bytes");
        System.out.println("Compression Ratio: " + compressionRatio);
        System.out.println("Compression Savings: " + compressionSavings + "%");
     } 
    //reads input from a file.
    public static String readInputFromFile(String filePath) {
        StringBuilder input = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n"); // add a new character for each line
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        return input.toString().trim(); //remove the last newline char
    }
}

