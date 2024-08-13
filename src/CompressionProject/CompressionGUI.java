package CompressionProject;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//CompressionGUI class to show various compression algorithms by interface using (WindowBuilder)
public class CompressionGUI {

    public JFrame frame;
    private JTextArea textArea;
    private JComboBox<String> fileSelector; //ComboBox to select 1 if the 3 example text files in the project

    //file paths to the examples string in project
    private static final String file1Path = "src\\example1.txt";
    private static final String file2Path = "src\\example2.txt";
    private static final String file3Path = "src\\example3.txt";
    private static final String compressedFilePath = "src\\compressed.bin";
    private static final String decompressedFilePath = "src\\Dec.txt";
    
    //start the application(gui)
    public CompressionGUI() {
        initialize();
    }
    //initialize of the window and create all the buttons and text area for the user to see 
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        //add file selector (ComboBox object to select files)
        fileSelector = new JComboBox<>(new String[] { "Select File", "Example 1", "Example 2", "Example 3" });
        fileSelector.setBounds(300, 20, 150, 30);//adjust the size of the selector on screen
        frame.getContentPane().add(fileSelector);
        //add button for each compression method in project 
        JButton btnCombinedCompression = new JButton("Combined Compression");
        btnCombinedCompression.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runCombinedCompression();
            }
        });
        btnCombinedCompression.setBounds(20, 60, 250, 30);
        frame.getContentPane().add(btnCombinedCompression);

        //for huffman
        JButton btnHuffmanCompression = new JButton("Huffman Compression");
        btnHuffmanCompression.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runHuffmanCompression();
            }
        });
        btnHuffmanCompression.setBounds(20, 100, 250, 30);
        frame.getContentPane().add(btnHuffmanCompression);
        //for lz77
        JButton btnLZ77Compression = new JButton("LZ77 Compression");
        btnLZ77Compression.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runLZ77Compression();
            }
        });
        btnLZ77Compression.setBounds(20, 140, 250, 30);
        frame.getContentPane().add(btnLZ77Compression);
        //for the dynamic version of lz77
        JButton btnLZ77DynamicCompression = new JButton("LZ77 Dynamic Compression");
        btnLZ77DynamicCompression.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runLZ77DynamicCompression();
            }
        });
        btnLZ77DynamicCompression.setBounds(20, 180, 250, 30);
        frame.getContentPane().add(btnLZ77DynamicCompression);

     // Create a JTextArea
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Wrap the JTextArea in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 220, 550, 300);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Add the JScrollPane to the frame instead of the JTextArea directly
        frame.getContentPane().add(scrollPane);

        JLabel lblSelectCompressionMethod = new JLabel("Select Compression Method:");
        lblSelectCompressionMethod.setBounds(20, 40, 250, 20);
        frame.getContentPane().add(lblSelectCompressionMethod);
    }
    //read input from a file
    private String readInputFromFile(String filePath) {
        StringBuilder input = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line).append("\n");
            }
        } catch (IOException e) {
            textArea.setText("Error reading the file: " + e.getMessage());//error to be visible in the GUI 
        }
        return input.toString().trim();
    }
    
    //get the selected file paths from the JComboBox object created before
    private String getSelectedFilePath() {
        switch (fileSelector.getSelectedIndex()) {
            case 1:
                return file1Path;
            case 2:
                return file2Path;
            case 3:
                return file3Path;
            default:
                return null;
        }
    }

    //combined compression method
    private void runCombinedCompression() {
        String filePath = getSelectedFilePath();
        if (filePath == null) {
            textArea.setText("Please select a file to compress.\n");
            return;
        }

        String input = readInputFromFile(filePath);
        textArea.setText("Selected File: " + input + "\n\n");
        textArea.append("Running Combined Compression...\n");
        textArea.append("Original Size: " + input.getBytes(StandardCharsets.UTF_8).length + " bytes\n");

        byte[] compressedBytes = CombinedCompression.compress(input);
        int compressedSize = compressedBytes.length;

        textArea.append("Combined Compressed Size: " + compressedSize + " bytes\n");

        //Calc and display compression ratio in text area for the user to see
        double compressionRatio = (double) compressedSize / input.length();
        double compressionSavings = (1 - compressionRatio) * 100;
        textArea.append("Compression Ratio: " + compressionRatio + "\n");
        textArea.append("Compression Savings: " + compressionSavings + "%\n");

        //check if decompress done correctly 
        String decompressed = CombinedCompression.decompress(compressedBytes);
        if (input.equals(decompressed)) {
            textArea.append("Combined Decompression successful!\n");
        } else {
            textArea.append("Combined Decompression failed.\n");
        }
    }
    //huffman compression method
    private void runHuffmanCompression() {
        String filePath = getSelectedFilePath();
        if (filePath == null) {
            textArea.setText("Please select a file to compress.\n");
            return;
        }

        String input = readInputFromFile(filePath);
        textArea.setText("Selected File: " + input + "\n\n");
        textArea.append("Running Huffman Compression...\n");
        textArea.append("Original Size: " + input.getBytes(StandardCharsets.UTF_8).length + " bytes\n");

        HuffmanCoding huffmanCoding = new HuffmanCoding();
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

        //make frequency map and Huffman tree struct 
        Map<Byte, Integer> frequencyMap = CombinedCompression.calculateFrequency(inputBytes);
        huffmanCoding.buildHuffmanTree(frequencyMap);

        //encode input
        String encoded = huffmanCoding.encode(inputBytes);
        int compressedSize = encoded.length() / 8;
        //for the user to see in text area
        textArea.append("Huffman Compressed Size: " + compressedSize + " bytes\n");

        //calc and display compression ratio of the method
        double compressionRatio = (double) compressedSize / inputBytes.length;
        double compressionSavings = (1 - compressionRatio) * 100;
        textArea.append("Compression Ratio: " + compressionRatio + "\n");
        textArea.append("Compression Savings: " + compressionSavings + "%\n");

        //check if decode is correctly done 
        byte[] decoded = huffmanCoding.decode(encoded);
        String decompressed = new String(decoded, StandardCharsets.UTF_8);
        if (input.equals(decompressed)) {
            textArea.append("Huffman Decompression successful!\n");
        } else {
            textArea.append("Huffman Decompression failed.\n");
        }
    }
    //regular LZ77 compression method
    private void runLZ77Compression() {
        String filePath = getSelectedFilePath();
        if (filePath == null) {
            textArea.setText("Please select a file to compress.\n");
            return;
        }

        String input = readInputFromFile(filePath);
        textArea.setText("Selected File: " + input + "\n\n");
        textArea.append("Running LZ77 Compression...\n");
        textArea.append("Original Size: " + input.getBytes(StandardCharsets.UTF_8).length + " bytes\n");

        // Compress using LZ77
        List<LZ77CompressedData> compressedData = LZ77.compress(input);
        int compressedSize = compressedData.size() * 4; //offset 2 bytes-1 byte length-1 byte next character
        //for the user to see in the text area of the window
        textArea.append("LZ77 Compressed Size: " + compressedSize + " bytes\n");

        //calc and display compression ratio
        double compressionRatio = (double) compressedSize / input.getBytes(StandardCharsets.UTF_8).length;
        double compressionSavings = (1 - compressionRatio) * 100;
        textArea.append("Compression Ratio: " + compressionRatio + "\n");
        textArea.append("Compression Savings: " + compressionSavings + "%\n");

        //check if correctly done 
        String decompressed = LZ77.decompress(compressedData);
        if (input.equals(decompressed)) {
            textArea.append("LZ77 Decompression successful!\n");
        } else {
            textArea.append("LZ77 Decompression failed.\n");
        }
    }

    
    //LZ77 Dynamic version compression method
    private void runLZ77DynamicCompression() {
        String filePath = getSelectedFilePath();
        if (filePath == null) {
            textArea.setText("Please select a file to compress.\n");
            return;
        }

        String input = readInputFromFile(filePath);
        textArea.setText("Selected File: " + input + "\n\n");
        textArea.append("Running LZ77 Dynamic Compression...\n");
        textArea.append("Original Size: " + input.getBytes(StandardCharsets.UTF_8).length + " bytes\n");

        //compress using LZ77Dynamic
        LZ77Dynamic dynamicCompressor = new LZ77Dynamic(input);
        List<LZ77Dynamic.LZ77CompressedData> dynamicCompressedData = dynamicCompressor.compress(input);
        int compressedSize = dynamicCompressedData.size() * 4; // 2 bytes for offset, 1 byte for length, 1 byte for nextChar

        textArea.append("LZ77 Dynamic Compressed Size: " + compressedSize + " bytes\n");

        //convert to standard LZ77CompressedData for decompression
        List<LZ77CompressedData> standardCompressedData = convertToStandardCompressedData(dynamicCompressedData);

        //calc and display compression ratio
        double compressionRatio = (double) compressedSize / input.getBytes(StandardCharsets.UTF_8).length;
        double compressionSavings = (1 - compressionRatio) * 100;
        textArea.append("Compression Ratio: " + compressionRatio + "\n");
        textArea.append("Compression Savings: " + compressionSavings + "%\n");

        // check if done correctly
        String decompressed = LZ77.decompress(standardCompressedData);
        if (input.equals(decompressed)) {
            textArea.append("LZ77 Dynamic Decompression successful!\n");
        } else {
            textArea.append("LZ77 Dynamic Decompression failed.\n");
        }
    }
    //Converts from LZ77Dynamic(LZ77CompressedData) to  regular LZ77CompressedData
    private List<LZ77CompressedData> convertToStandardCompressedData(
            List<LZ77Dynamic.LZ77CompressedData> dynamicData) {
        List<LZ77CompressedData> standardData = new ArrayList<>();
        for (LZ77Dynamic.LZ77CompressedData data : dynamicData) {
            standardData.add(new LZ77CompressedData(data.offset, data.length, data.nextChar));
        }
        return standardData;
    }
}
