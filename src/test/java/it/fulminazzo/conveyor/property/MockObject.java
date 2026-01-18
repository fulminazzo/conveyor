package it.fulminazzo.conveyor.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
final class MockObject {

    String field1 = "Hello, world!";
    int field2 = 10;
    boolean field3 = true;
    Object field4 = null;

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

}
