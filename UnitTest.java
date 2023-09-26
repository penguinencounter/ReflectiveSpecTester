import java.util.ArrayList;

public class UnitTest {
    private static ArrayList<String> successes = new ArrayList<>();
    private static ArrayList<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        failures.clear();
        successes.clear();
        assertEqual("new Circle().getArea()", new Circle().getArea(), Math.PI);
        assertEqual("new Circle(2).getArea()", new Circle(2).getArea(), 4 * Math.PI);
        print();
        System.exit(failures.isEmpty() ? 0 : 1);
    }

    private static void print() {
        StringBuilder builder = new StringBuilder();
        if (!successes.isEmpty()) {
            builder.append("Successes\n").append("==========\n");
            int i = 0;
            for (String message : successes) {
                builder.append(i++).append(": ").append(message).append('\n');
            }
        }
        if (!failures.isEmpty()) {
            if (!builder.isEmpty()) builder.append('\n');
            builder.append("Failures\n").append("==========\n");
            int i = 0;
            for (String message : failures) {
                builder.append(i++).append(": ").append(message).append('\n');
            }
        }
        System.out.print(builder);
    }

    private static <T> void assertEqual(String label, T a, T b) {
        if (!a.equals(b)) {
            failures.add(label + " : " + a + " should be " + b);
        }
        successes.add(label + " => " + b);
    }
}
