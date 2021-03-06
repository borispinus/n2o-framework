package net.n2oapp.framework.config.metadata.compile.page;


import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.action.CustomActionIOv1;
import net.n2oapp.framework.config.io.page.SimplePageElementIOv2;
import net.n2oapp.framework.config.io.widget.CustomWidgetIOv4;
import net.n2oapp.framework.config.io.widget.HtmlWidgetElementIOv4;
import net.n2oapp.framework.config.metadata.compile.region.NoneRegionCompiler;
import net.n2oapp.framework.config.metadata.compile.page.SimplePageCompiler;
import net.n2oapp.framework.config.metadata.compile.widget.CustomWidgetCompiler;
import net.n2oapp.framework.config.metadata.compile.widget.HtmlWidgetCompiler;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.test.JsonMetadataTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Тестирвоание маппинга java модели в json простой старницы
 */
public class SimplePageJsonTest extends JsonMetadataTestBase {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack());
    }

    @Test
    public void simplePage() {
        check("net/n2oapp/framework/config/mapping/testSimplePageJson.page.xml",
                "components/core/Page.meta.json")
                .changeValue("id", "test")
                .changeValue("layout.regions.single[0].items[0].widgetId", "test_wireframe")
                .changeNode("widgets.Page_Wireframe", "widgets.test_wireframe")
                .exclude("breadcrumb", "routes", "page.modelLink",
                        "widgets.test_wireframe.actions",
                        "widgets.test_wireframe.name",
                        "widgets.test_wireframe.wireframe",
                        "widgets.test_wireframe.toolbar.topLeft[0].id",
                        "widgets.test_wireframe.toolbar.topLeft[0].buttons[0].conditions",
                        "widgets.test_wireframe.toolbar.topLeft[0].buttons[0].confirm",
                        "widgets.test_wireframe.toolbar.topLeft[0].buttons[0].hint",
                        "layout.regions.single[0].items[0].id",
                        "layout.regions.single[0].items[0].label")
                .assertEquals();
    }
}
