public class SpecificationTest {
    private static boolean failures = false;

    public static void main(String[] args) {
        failures = false;
        SpecTester<Circle> tester = new SpecTester<>(Circle.class, SpecificationTest::fail);
        tester.constructorSpec(SpecTester.Access.ACCESSIBLE);
        tester.constructorSpec(SpecTester.Access.ACCESSIBLE, int.class);
        tester.fieldSpec("radius", SpecTester.Access.PRIVATE, double.class);
        tester.methodSpec("getRadius", SpecTester.Access.ACCESSIBLE, double.class);
        tester.methodSpec("setRadius", SpecTester.Access.ACCESSIBLE, void.class, double.class);
        tester.methodSpec("getArea", SpecTester.Access.ACCESSIBLE, double.class);

        System.exit(failures ? 1 : 0);
    }

    public static void fail(String reason) {
        failures = true;
        System.err.println(reason);
    }
}
