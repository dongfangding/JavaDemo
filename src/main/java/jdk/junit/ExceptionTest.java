package jdk.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExceptionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @SuppressWarnings("null")
    @Test(expected = NullPointerException.class)
    public void testAnotation() {
        String nullStr = null;
        if (nullStr.equals("nullStr")) {
            System.out.println("--------");
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testExceptionMessage() {
        try {
            String nullStr = null;
            if (nullStr.equals("nullStr")) {
                System.out.println("--------");
            }
        } catch (NullPointerException e) {
            // assertThat(e.getMessage(), Is<e>("Index: 0, Size: 0"));
            System.out.println(e.getMessage());
        }
    }
}
