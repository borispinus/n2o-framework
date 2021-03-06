package net.n2oapp.criteria.filters;

import java.util.Collection;
import java.util.List;

/**
 * Checker for check value by filter
 */
public class FilterChecker {


    /**
     * Checks that the value satisfies the filter constraints
     * @param filter  filter
     * @param value value for checking
     * @return  <tt>true</tt> if the value satisfies the filter constraints, else <tt>false</tt>
     */
    @SuppressWarnings("unchecked")
    public static boolean check(Filter filter, Object value) {
        switch (filter.getType()) {
            case eq:
                return equals(filter.getValue(), value);
            case notEq:
                return !equals(filter.getValue(), value);
            case in:
                return contains((List) filter.getValue(), value);
            case notIn:
                return !contains((List) filter.getValue(), value);
            case more:
                return compare(filter.getValue(), value) < 0;
            case less:
                return compare(filter.getValue(), value) > 0;
            case isNull:
                return value == null;
            case isNotNull:
                return value != null;
            case eqOrIsNull:
                return equals(filter.getValue(), value) || value == null;
            case inOrIsNull:
                return contains((List) filter.getValue(), value) || value == null;
            case like:
                return like(filter.getValue(), value);
            case likeStart:
                return likeStart(filter.getValue(), value);
            case overlaps:
                return overlap((List) filter.getValue(), value);
            case contains:
                return contains((List) filter.getValue(), value);
        }
        throw new IllegalArgumentException(String.format("Unknown filter-type '%s'", filter.getType()));
    }

    private static boolean equals(Object filterValue, Object realValue) {
        if (filterValue instanceof Number && realValue instanceof Number) {
            return ((Number) filterValue).longValue() == ((Number) realValue).longValue();
        }
        return filterValue.equals(realValue);
    }

    private static boolean containsOne(List values, Object value) {
        if (value instanceof Number) {
            return values.stream().anyMatch(v -> equals(v, value));
        }
        return values.contains(value);
    }

    private static boolean contains(List values, Object value) {
        if (value instanceof Collection) {
            final boolean[] res = {true};
            ((Collection) value).stream().forEach(v -> {
                if (!containsOne(values, v)) {
                    res[0] = false;
                }
            });
            return res[0];
        }
        return containsOne(values, value);
    }

    private static boolean overlap(List values, Object value) {
        if (value instanceof Collection) {
            return ((Collection) value).stream().anyMatch(v -> contains(values, v));
        }
        return containsOne(values, value);
    }

    private static int compare(Object filterValue, Object realValue) {
        if (filterValue instanceof Number && realValue instanceof Number) {
            return (int) (((Number) filterValue).longValue() - ((Number) realValue).longValue());
        }
        return ((Comparable) filterValue).compareTo(realValue);
    }

    private static boolean like(Object filterValue, Object realValue) {
        if (!(filterValue instanceof String) || !(realValue instanceof String))
            return false;
        return ((String) filterValue).matches(".*" + realValue + ".*");
    }

    private static boolean likeStart(Object filterValue, Object realValue) {
        if (!(filterValue instanceof String) || !(realValue instanceof String))
            return false;
        return ((String) filterValue).matches(realValue + ".*");
    }
}
