package CompressionProject;


import java.util.HashMap;
import java.util.Map;


//LZ77Optimizer class to calc the entropy and estimating redundancy
public class LZ77Optimizer {

	//calc entropy of given strings
    public static double calculateEntropy(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        double entropy = 0.0;
        int length = input.length();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            double frequency = (double) entry.getValue() / length;
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }

        return entropy;
    }

    
    //estimates redundancy as the proportion of repeated sequences (Patterned text files in src folder)
    public static double estimateRedundancy(String input) {
        int uniqueChars = (int) input.chars().distinct().count();
        return 1.0 - ((double) uniqueChars / input.length());
    }
}

