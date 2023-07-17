package mx.kenzie.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * MapFormat is a utility class for formatting text by replacing placeholders with values from a given map.
 * It supports custom opening and closing characters for placeholders, as well as an optional empty placeholder value.
 */
public class MapFormat {

    protected final char open, close;
    protected final String empty;

    public MapFormat() {
        this('{', '}');
    }

    public MapFormat(char open, char close) {
        this(open, close, "null");
    }

    public MapFormat(char open, char close, String emptyPlaceholder) {
        this.open = open;
        this.close = close;
        this.empty = emptyPlaceholder;
    }

    public static String interpolate(String text, Map<String, ?> values) {
        return new MapFormat().format(text, values);
    }

    protected Part[] tokenize(String text, int start) {
        int begin = start;
        final List<Part> list = new ArrayList<>();
        do {
            final int open = text.indexOf(this.open, begin), close;
            if (open == -1) break;
            else if (open > 0 && text.charAt(open - 1) == '\\') continue;
            final String part = text.substring(begin, open);
            int from = open + 1;
            do {
                int found = text.indexOf(this.close, from);
                if (found < open) {
                    close = -1;
                    break;
                } else if (text.charAt(found - 1) == '\\' || found == open + 1) {
                    from = found + 1;
                    continue;
                }
                close = found;
                break;
            } while (true);
            if (close == -1) break;
            final String key = text.substring(open + 1, close);
            if (!part.isEmpty()) list.add(new Literal(part));
            list.add(new Token(this.sanitise(key)));
            begin = close + 1;
        } while (begin < text.length());
        if (begin == start || list.isEmpty()) return new Part[]{new Literal(text)};
        if (begin < text.length()) list.add(new Literal(text.substring(begin)));
        return list.toArray(new Part[0]);
    }

    private String sanitise(String key) {
        if (key.contains("\\" + open)) key = key.replace("\\" + open, String.valueOf(open));
        if (key.contains("\\" + close)) key = key.replace("\\" + close, String.valueOf(close));
        return key;
    }

    public String format(String text, Map<String, ?> inputs) {
        return this.format(text, 0, inputs);
    }

    public String format(String text, int start, Map<String, ?> inputs) {
        final Part[] parts = this.tokenize(text, start);
        final StringBuilder builder = new StringBuilder();
        for (final Part part : parts) {
            if (part instanceof Literal) builder.append(part.value());
            else builder.append(this.stringify(inputs.get(part.value())));
        }
        return builder.toString();
    }

    public String formatAny(String text, Map<?, ?> values) {
        return new MapFormat('{', '}').format(text, new LazyMap(values));
    }

    protected String stringify(Object value) {
        if (value == null) return empty;
        return Objects.toString(value);
    }

    protected interface Part {

        String value();

    }

    protected record Token(String value) implements Part {}

    protected record Literal(String value) implements Part {}

    protected class LazyMap implements Map<String, Object> {

        protected final Map<?, ?> backer;
        protected final Map<String, Object> linker;

        protected LazyMap(Map<?, ?> backer) {
            this.backer = backer;
            this.linker = new HashMap<>();
            for (final Object object : backer.keySet()) linker.put(MapFormat.this.stringify(object), object);
        }

        @Override
        public int size() {
            return backer.size();
        }

        @Override
        public boolean isEmpty() {
            return backer.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return linker.containsKey(key) || backer.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return backer.containsValue(value);
        }

        public Object get(String key) {
            final Object link = linker.get(key);
            if (link == null) return null;
            return backer.get(link);
        }

        @Override
        public Object get(Object key) {
            final Object link = linker.get(key);
            if (link == null) return null;
            return backer.get(link);
        }

        @Nullable
        @Override
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Set<String> keySet() {
            return linker.keySet();
        }

        @NotNull
        @Override
        public Collection<Object> values() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Set<Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }

    }

}
