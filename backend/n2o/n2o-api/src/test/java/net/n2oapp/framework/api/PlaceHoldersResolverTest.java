package net.n2oapp.framework.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.context.Context;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static net.n2oapp.framework.api.PlaceHoldersResolver.replaceOptional;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Тест шаблонизатора текста
 */
public class PlaceHoldersResolverTest {

    @Test
    public void resolve() {
        String prefix = "{";
        String suffix = "}";

        PlaceHoldersResolver resolver = new PlaceHoldersResolver(prefix, suffix);
        DataSet data = new DataSet();
        data.put("name", "Foo");
        data.put("surname", "Bar");
        Assert.assertEquals("bla {noname} bla", resolver.resolve("bla {noname} bla", data));
        Assert.assertEquals("bla Foo bla Bar bla", resolver.resolve("bla {name} bla {surname} bla", data));
        Assert.assertEquals("bla test bla test bla", resolver.resolve("bla {name} bla {surname} bla", p -> "test"));
    }

    @Test
    public void extract() {
        String prefix = "{";
        String suffix = "}";

        PlaceHoldersResolver placeHoldersResolver = new PlaceHoldersResolver(prefix, suffix);
        Set result;

        result = placeHoldersResolver.extractPlaceHolders("{test}");
        assert result.size() == 1;
        assert result.contains("test");

        result = placeHoldersResolver.extractPlaceHolders("aaa{test1}bb{test2}cc");
        assert result.size() == 2;
        assert result.contains("test1");
        assert result.contains("test2");

        result = placeHoldersResolver.extractPlaceHolders("{test1}bb{test2}");
        assert result.size() == 2;
        assert result.contains("test1");
        assert result.contains("test2");

        result = placeHoldersResolver.extractPlaceHolders("{test1}{test2}");
        assert result.size() == 2;
        assert result.contains("test1");
        assert result.contains("test2");
    }

    @Test
    public void resolveValue() {
        PlaceHoldersResolver resolver = new PlaceHoldersResolver("#{", "}");
        Map<String, Object> data = new HashMap<>();
        data.put("name", "test");
        Assert.assertNull(resolver.resolveValue(null, data));
        Assert.assertEquals(1, resolver.resolveValue(1, data));
        Assert.assertEquals("", resolver.resolveValue("", data));
        Assert.assertEquals("{name}", resolver.resolveValue("{name}", data));
        Assert.assertNull(resolver.resolveValue("#{noname}", data));
        Assert.assertEquals("test", resolver.resolveValue("#{name}", data));
        Assert.assertEquals("test", resolver.resolveValue("#{name}", (k) -> "test"));
    }

    @Test
    public void resolveWithFunctions() {
        PlaceHoldersResolver resolver = new PlaceHoldersResolver("{", "}");
        DataSet data = new DataSet();
        data.put("name", "Foo");
        Assert.assertEquals("bla Foo bla  bla",
                resolver.resolve("bla {name} bla {noname} bla",
                        PlaceHoldersResolver.replaceNullByEmpty(data::get)));
        try {
            resolver.resolve("bla {name} bla {noname} bla",
                    PlaceHoldersResolver.replaceRequired(data::get));
            Assert.fail();
        } catch (NotFoundPlaceholderException e) {
        }
        Assert.assertEquals("bla Foo bla test bla",
                resolver.resolve("bla {name} bla {noname?test} bla",
                        PlaceHoldersResolver.replaceOptional(data::get)));
        Assert.assertEquals("bla Foo bla {noname?} bla",
                resolver.resolve("bla {name} bla {noname?} bla",
                        PlaceHoldersResolver.replaceOptional(data::get)));
        Assert.assertEquals("bla Foo bla {noname} bla",
                resolver.resolve("bla {name} bla {noname} bla",
                        PlaceHoldersResolver.replaceOptional(data::get)));
        try {
            resolver.resolve("bla {name} bla {noname!} bla",
                    PlaceHoldersResolver.replaceOptional(data::get));
            Assert.fail();
        } catch (NotFoundPlaceholderException e) {
        }
        Assert.assertEquals("bla Foo bla  bla",
                resolver.resolve("bla {name} bla {noname} bla",
                        PlaceHoldersResolver.replaceNullByEmpty(
                                PlaceHoldersResolver.replaceOptional(data::get))));
    }

    @Test
    public void resolveJson() {
        Context context = mock(Context.class);
        List<String> roles = Arrays.asList("user", "looser");
        when(context.get("testContext")).thenReturn("testValue");
        when(context.get("roles")).thenReturn(roles);
        when(context.get("username")).thenReturn("testUsername");
        when(context.get("age")).thenReturn(99);
        when(context.get("isActive")).thenReturn(true);

        ObjectMapper mapper = new ObjectMapper();
        Assert.assertEquals("testValue", PlaceHoldersResolver.replaceByJson(replaceOptional(context::get), mapper).apply("testContext"));
        Assert.assertEquals("testUsername", PlaceHoldersResolver.replaceByJson(replaceOptional(context::get), mapper).apply("username"));
        Assert.assertEquals("99", PlaceHoldersResolver.replaceByJson(replaceOptional(context::get), mapper).apply("age"));
        Assert.assertEquals("[\"user\",\"looser\"]", PlaceHoldersResolver.replaceByJson(replaceOptional(context::get), mapper).apply("roles"));
        Assert.assertEquals("true", PlaceHoldersResolver.replaceByJson(replaceOptional(context::get), mapper).apply("isActive"));
    }
}
