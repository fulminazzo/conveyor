package it.fulminazzo.conveyor.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
final class MockObject {

    String field1 = "Hello, world!";
    int field2 = 10;
    boolean field3 = true;
    Object field4 = null;

    List<String> indexed1 = Arrays.asList("Hello", "world");
    Set<Integer> indexed2 = new HashSet<>(Arrays.asList(1, 2, 3));
    Collection<Boolean> indexed3 = Arrays.asList(true, false);
    String[] indexed4 = new String[]{"Hello", "world"};

    String[][][] indexedMatrix1 = new String[][][]{
            new String[][] {
                    new String[]{"Hello", "world!"}
            },
            new String[][] {
                    new String[]{"Hello", "foe!", "or", "not?"},
                    new String[]{"Hey", "friend!"}
            }
    };

    MockSubObject sub1 = new MockSubObject();

    public String method1() {
        return method1("world");
    }

    public String method1(String who) {
        return String.format("Hello, %s!", who);
    }

    int method2() {
        return 10;
    }

    private boolean method3() {
        return true;
    }

    Object method4() {
        return null;
    }

    Object method5(String what) {
        return what;
    }

    @Data
    static final class MockSubObject {

        String field1 = "Hello, world";

    }

}
