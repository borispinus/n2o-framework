package net.n2oapp.framework.config.metadata.compile.page;


import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.page.SimplePageElementIOv2;
import net.n2oapp.framework.config.io.region.NoneRegionIOv1;
import net.n2oapp.framework.config.io.widget.HtmlWidgetElementIOv4;
import net.n2oapp.framework.api.metadata.meta.Page;
import net.n2oapp.framework.api.metadata.meta.region.NoneRegion;
import net.n2oapp.framework.api.metadata.meta.widget.HtmlWidget;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.page.SimplePageCompiler;
import net.n2oapp.framework.config.metadata.compile.region.NoneRegionCompiler;
import net.n2oapp.framework.config.metadata.compile.widget.HtmlWidgetCompiler;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.hamcrest.core.IsNull;

/**
 * Тестирвоание компиляции простой страницы
 */

public class SimplePageCompileTest extends SourceCompileTestBase {


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.ios(new SimplePageElementIOv2(), new NoneRegionIOv1(), new HtmlWidgetElementIOv4())
                .compilers(new SimplePageCompiler(), new NoneRegionCompiler(), new HtmlWidgetCompiler());
    }


    @Test
    public void simplePage() throws Exception {
        Page page = compile("net/n2oapp/framework/config/metadata/compile/page/testSimplePage.page.xml").get(new PageContext("testSimplePage"));
        assertThat(page.getId(), is("test_route"));
        assertThat(page.getLayout().getSrc(), is("SingleLayout"));
        assertThat(page.getWidgets().get("test_route_main"), notNullValue());
        assertThat(page.getLayout().getRegions().size(), is(1));
        assertThat(page.getLayout().getRegions().get("single").get(0).getClass(), is(equalTo(NoneRegion.class)));
        assertThat(page.getLayout().getRegions().get("single").get(0).getSrc(), is("NoneRegion"));
        assertThat(page.getWidgets().size(), is(1));
        assertThat(page.getWidgets().values().iterator().next().getClass(), is(equalTo(HtmlWidget.class)));
        assertThat(page.getWidgets().size(), is(1));
        assertThat(page.getRoutes().getList().size(), is(3));
        assertThat(page.getRoutes().getList().get(0).getPath(), is("/test/route"));
        assertThat(page.getRoutes().getList().get(1).getPath(), is("/test/route/main"));
        assertThat(page.getRoutes().getList().get(2).getPath(), is("/test/route/main/:test_route_main_id"));
        assertThat(route("/test/route").getContext(Page.class), notNullValue());
    }
}
