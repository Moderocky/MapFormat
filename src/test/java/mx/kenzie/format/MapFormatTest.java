package mx.kenzie.format;

import org.junit.Test;

import java.util.Map;
import java.util.Objects;

public class MapFormatTest {

    @Test
    public void interpolateNoResult() {
        final String result = MapFormat.interpolate("hello there", Map.of("hello", "bean"));
        assert result != null;
        assert result.equals("hello there");
    }

    @Test
    public void interpolateSimple() {
        final String result = MapFormat.interpolate("hello {test}", Map.of("test", "there"));
        assert result != null;
        assert result.equals("hello there");
    }

    @Test
    public void interpolateMissing() {
        final String result = MapFormat.interpolate("{hello} {test}", Map.of("test", "there"));
        assert result != null;
        assert result.equals("null there");
    }

    @Test
    public void interpolateMulti() {
        final String result = MapFormat.interpolate("{test} {test}", Map.of("test", "there"));
        assert result != null;
        assert result.equals("there there");
    }

    @Test
    public void tokenize() {
        final MapFormat format = new MapFormat();
        assert format.tokenize("hello there", 0).length == 1;
        assert format.tokenize("hello there", 0)[0] instanceof MapFormat.Literal literal
            && literal.value().equals("hello there");
        assert format.tokenize("hello {there}", 0).length == 2;
        assert format.tokenize("hello {there}", 0)[0] instanceof MapFormat.Literal literal
            && literal.value().equals("hello ");
        assert format.tokenize("hello {there}", 0)[1] instanceof MapFormat.Token token
            && token.value().equals("there");
    }

    @Test
    public void format() {
        final MapFormat format = new MapFormat();
        assert this.equals(format.format("hello {test} there", Map.of()), "hello null there");
        assert this.equals(format.format("hello {test} there", Map.of("test", "bean")), "hello bean there");
        assert this.equals(format.format("hello {test} there", Map.of("test", 10)), "hello 10 there");
        assert this.equals(format.format("hello there", Map.of("test", "bean")), "hello there");
        assert this.equals(format.format("hello there", Map.of()), "hello there");
        assert this.equals(format.format("{hello} there", Map.of("hello", "hello")), "hello there");
        assert this.equals(format.format("{hello}", Map.of("hello", "hello")), "hello");
        assert this.equals(format.format("{hello} there}", Map.of("hello", "hello")), "hello there}");
        assert this.equals(format.format("{he\\}llo} there", Map.of("he}llo", "hello")), "hello there");
    }

    private boolean equals(String x, String y) {
        assert Objects.equals(x, y) : x;
        return true;
    }

}
