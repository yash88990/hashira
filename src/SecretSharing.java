import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SecretSharing {

    // Decode Y value from given base
    public static BigInteger decodeY(String value, int base) {
        return new BigInteger(value, base);
    }

    // Lagrange Interpolation: Evaluate P(0)
    public static BigInteger lagrangeInterpolationAtZero(List<Integer> xVals, List<BigInteger> yVals) {
        BigInteger result = BigInteger.ZERO;
        int k = xVals.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = yVals.get(i);

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xi = xVals.get(i);
                    int xj = xVals.get(j);

                    BigInteger numerator = BigInteger.valueOf(-xj); // (0 - xj)
                    BigInteger denominator = BigInteger.valueOf(xi - xj); // (xi - xj)

                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            // ⚠️ Change filename here to "test1.json" or "test2.json"
            FileInputStream fis = new FileInputStream("test1.json");
            JSONObject obj = new JSONObject(new JSONTokener(fis));

            JSONObject keys = obj.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            List<Integer> xVals = new ArrayList<>();
            List<BigInteger> yVals = new ArrayList<>();

            int count = 0;
            for (String key : obj.keySet()) {
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key);
                    JSONObject root = obj.getJSONObject(key);

                    int base = Integer.parseInt(root.getString("base"));
                    String value = root.getString("value");

                    BigInteger y = decodeY(value, base);

                    if (count < k) { // take first k points only
                        xVals.add(x);
                        yVals.add(y);
                        count++;
                    }
                }
            }

            BigInteger secretC = lagrangeInterpolationAtZero(xVals, yVals);

            System.out.println("Recovered Secret (c) = " + secretC);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
