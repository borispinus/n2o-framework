package net.n2oapp.framework.access.metadata.transform;

import net.n2oapp.framework.access.integration.metadata.transform.ToolbarAccessTransformer;
import net.n2oapp.framework.access.integration.metadata.transform.ToolbarCellAccessTransformer;
import net.n2oapp.framework.access.integration.metadata.transform.action.InvokeActionAccessTransformer;
import net.n2oapp.framework.access.metadata.Security;
import net.n2oapp.framework.access.metadata.pack.AccessSchemaPack;
import net.n2oapp.framework.api.metadata.meta.Page;
import net.n2oapp.framework.api.metadata.meta.toolbar.ToolbarCell;
import net.n2oapp.framework.api.metadata.meta.widget.table.TableWidgetComponent;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileTerminalPipeline;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SimplePropertyResolver;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ToolbarCellAccessTransformerTest extends SourceCompileTestBase {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack(), new N2oAllDataPack(), new AccessSchemaPack())
                .sources(new CompileInfo("net/n2oapp/framework/access/metadata/transform/testToolbarAccessTransformer.object.xml"))
                .transformers(new ToolbarCellAccessTransformer(), new InvokeActionAccessTransformer());
    }

    @Test
    public void toolbarCellAccessTransformer() {
        ((SimplePropertyResolver) builder.getEnvironment().getSystemProperties()).setProperty("n2o.access.schema.id", "testToolbar");

        ReadCompileTerminalPipeline<?> pipeline = compile(
                "net/n2oapp/framework/access/metadata/schema/testToolbar.access.xml",
                "net/n2oapp/framework/access/metadata/transform/testToolbarCellAccessTransformer.page.xml"
        );
        Page page = pipeline.transform().get(new PageContext("testToolbarCellAccessTransformer"));
        Security.SecurityObject security = ((Security)((ToolbarCell)((TableWidgetComponent)page.getWidgets()
                .get("testToolbarCellAccessTransformer_main")
                .getComponent()).getCells().get(0)).getButtons().get(0)
                .getProperties().get("security")).getSecurityMap().get("object");

        assertThat(security.getRoles().size(), is(1));
        assertThat(security.getRoles().get(0), is("admin"));
        assertThat(security.getPermissions().size(), is(1));
        assertThat(security.getPermissions().get(0), is("permission"));
        assertThat(security.getUsernames().size(), is(1));
        assertThat(security.getUsernames().get(0), is("user"));

    }

}