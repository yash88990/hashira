import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.MathContext;

public class PolynomialConstantLagrange {

    // Lagrange interpolation with BigDecimal precision
    public static BigDecimal lagrangeInterpolation(int[] x, BigInteger[] y, int valueAt) {
        int n = x.length;
        BigDecimal result = BigDecimal.ZERO;
        MathContext mc = new MathContext(80); // high precision

        for (int i = 0; i < n; i++) {
            BigDecimal term = new BigDecimal(y[i], mc);
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    BigDecimal numerator = new BigDecimal(valueAt - x[j], mc);
                    BigDecimal denominator = new BigDecimal(x[i] - x[j], mc);
                    term = term.multiply(numerator.divide(denominator, mc), mc);
                }
            }
            result = result.add(term, mc);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java PolynomialConstantLagrange <input.json>");
            return;
        }

        // Read JSON file
        String content = new String(Files.readAllBytes(Paths.get(args[0])));
        JSONObject obj = new JSONObject(content);

        JSONObject keys = obj.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        int[] x = new int[k];
        BigInteger[] y = new BigInteger[k];

        int idx = 0;
        for (String key : obj.keySet()) {
            if (key.equals("keys"))
                continue;
            if (idx >= k)
                break; // only take k points

            JSONObject point = obj.getJSONObject(key);
            int xi = Integer.parseInt(key);
            int base = point.getInt("base");
            BigInteger yi = new BigInteger(point.getString("value"), base);

            x[idx] = xi;
            y[idx] = yi;
            idx++;
        }

        // Find constant term -> polynomial evaluated at x = 0
        BigDecimal constantC = lagrangeInterpolation(x, y, 0);

        System.out.println("Decoded constant term (c) = " + constantC.toPlainString());
    }
}
