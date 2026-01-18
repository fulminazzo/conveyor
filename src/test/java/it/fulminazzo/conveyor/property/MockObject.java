package it.fulminazzo.conveyor.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
final class MockObject {

    String field1 = "Hello, world!";
    int field2 = 10;
    boolean field3 = true;
    Object field4 = null;

    List<String> indexed1 = Arrays.asList("Hello", "world");
    Set<Integer> indexed2 = Set.of(1, 2, 3);
    Collection<Boolean> indexed3 = List.of(true, false);
    String[] indexed4 = new String[]{"Hello", "world"};

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

    final class MockSubObject {

        String field1 = "Hello, world";

    }

}
