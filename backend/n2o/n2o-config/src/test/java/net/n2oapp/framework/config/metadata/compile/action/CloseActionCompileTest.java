package net.n2oapp.framework.config.metadata.compile.action;

import net.n2oapp.framework.api.metadata.meta.Page;
import net.n2oapp.framework.api.metadata.meta.action.close.CloseAction;
import net.n2oapp.framework.api.metadata.meta.action.link.LinkAction;
import net.n2oapp.framework.api.metadata.meta.widget.table.Table;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.action.CloseActionElementIOV1;
import net.n2oapp.framework.config.metadata.compile.context.ModalPageContext;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.context.WidgetContext;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CloseActionCompileTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oRegionsPack(), new N2oWidgetsPack());
        builder.ios(new CloseActionElementIOV1());
        builder.compilers(new CloseActionCompiler());
    }

    @Test
    public void closeModal() throws Exception {
        Page page = compile("net/n2oapp/framework/config/metadata/compile/action/testCloseAction.page.xml")
                .get(new ModalPageContext("testCloseAction", "/p/w/a"));
        CloseAction testAction = (CloseAction) page.getWidgets().get("p_w_a_main").getActions().get("test");
        assertThat(testAction.getId(), is("test"));
        assertThat(testAction.getSrc(), is("perform"));
        assertThat(testAction.getOptions().getType(), is("n2o/modals/DESTROY"));

    }

    @Test
    public void back() {
        PageContext context = new PageContext("testCloseAction", "/p/w/a");
        context.setParentRoute("/p/w");
        Page page = compile("net/n2oapp/framework/config/metadata/compile/action/testCloseAction.page.xml")
                .get(context);
        LinkAction testAction = (LinkAction) page.getWidgets().get("p_w_a_main").getActions().get("test");
        assertThat(testAction.getId(), is("test"));
        assertThat(testAction.getSrc(), is("link"));
        assertThat(testAction.getOptions().getPath(), is("/p/w"));
    }
}