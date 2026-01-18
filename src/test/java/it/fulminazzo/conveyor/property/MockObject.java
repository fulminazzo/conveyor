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

}
