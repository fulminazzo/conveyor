package it.fulminazzo.conveyor.property;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
final class MockObject {

    String field1 = "Hello, world!";
    int field2 = 10;
    boolean field3 = true;
    Object field4 = null;

}
