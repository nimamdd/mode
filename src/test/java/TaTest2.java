import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TaTest2 {

    private ByteArrayOutputStream outStream;

    public String ExpectedOutput(List<Integer> inputList) {
        StringBuilder sb = new StringBuilder();
        int count = inputList.get(0);

        int maxFrequency = 0;
        int num = -1;
        for (int i = 1; i <= count; i++) {
            int x = inputList.get(i);
            int freq = 0;
            for (int j = 1;j<=count;j++){
                if (x == inputList.get(j))
                    freq++;
            }
            if (freq > mode){
                num = x;
                maxFrequency = freq;
            }
        }

        sb.append(num);
        sb.append(" ");
        sb.append(mode);

        return sb.toString().trim();
    }

    public List<Integer> generateRandomTestNumbers() {
        Random random = new Random();
        int totalCount = 10 + random.nextInt(11);
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> freqMap = new HashMap<>();
        Set<Integer> used = new HashSet<>();

        boolean hasMode = random.nextDouble() < 0.7;

        int filled = 0;

        if (hasMode) {
            int modeCount = 1 + random.nextInt(3);
            int maxFrequency = 2 + random.nextInt(Math.max(1, totalCount / (2 * modeCount)));

            for (int i = 0; i < modeCount; i++) {
                int value;
                do {
                    value = 100_000_000 + random.nextInt(2_047_483_647);
                } while (used.contains(value));
                used.add(value);
                freqMap.put(value, maxFrequency);
            }

            filled = maxFrequency * modeCount;
        }

        while (filled < totalCount) {
            int value;
            do {
                value = 100_000_000 + random.nextInt(2_047_483_647);
            } while (used.contains(value));
            used.add(value);

            int freq = hasMode ? 1 + random.nextInt(Math.min(2, totalCount - filled)) : 1;
            freqMap.put(value, freq);
            filled += freq;
        }

        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                result.add(entry.getKey());
            }
        }

        Collections.shuffle(result);
        result.add(0, result.size());
        return result;
    }

    public void baseTest(List<Integer> inputList, String correctResult) {
        Process p;

        try {
            StringBuilder inputBuilder = new StringBuilder();
            for (int num : inputList) {
                inputBuilder.append(num).append("\\n");
            }

            if (inputBuilder.length() >= 2) {
                inputBuilder.setLength(inputBuilder.length() - 2);
            }

            String echoInput = "echo -e \"" + inputBuilder.toString() + "\"";
            String fullCmd = echoInput + " | java -jar lib/rars.jar nc src/main/java/solution.s";

            String[] cmd = {"/bin/bash", "-c", fullCmd};

            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String result = br.readLine().trim();
            br.close();

            assertEquals(correctResult, result);
            p.destroy();

        } catch (Exception e) {
            System.err.println("Execution error: " + e.getMessage());
            fail();
        }
    }

    @Before
    public void initStreams() {
        outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));
    }

    @Test
    public void test2() {
        List<Integer> input = generateRandomTestNumbers();
        String correctOutput = ExpectedOutput(input);
        baseTest(input, correctOutput);
    }
}
