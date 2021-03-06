package net.n2oapp.framework.access.metadata.transform;

import net.n2oapp.framework.access.integration.metadata.transform.HeaderAccessTransformer;
import net.n2oapp.framework.access.metadata.Security;
import net.n2oapp.framework.access.metadata.pack.AccessSchemaPack;
import net.n2oapp.framework.api.metadata.header.CompiledHeader;
import net.n2oapp.framework.api.metadata.header.HeaderItem;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileTerminalPipeline;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.HeaderContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oHeaderPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SimplePropertyResolver;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class HeaderAccessTransformerTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oHeaderPack(), new N2oAllPagesPack(), new AccessSchemaPack())
                .sources(new CompileInfo("net/n2oapp/framework/access/metadata/transform/testHeaderAccessTransformer.page.xml"))
                .transformers(new HeaderAccessTransformer());
    }


    @Test
    public void testHeaderTransform() {
        ((SimplePropertyResolver) builder.getEnvironment().getSystemProperties()).setProperty("n2o.access.schema.id", "testHeader");

        ReadCompileTerminalPipeline pipeline = compile("net/n2oapp/framework/access/metadata/schema/testHeader.access.xml",
                "net/n2oapp/framework/access/metadata/transform/testHeaderAccessTransformer.header.xml");

        CompiledHeader header = (CompiledHeader) ((ReadCompileTerminalPipeline) pipeline.transform()).get(new HeaderContext("testHeaderAccessTransformer"));

        HeaderItem item = header.getItems().get(0);
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getUsernames().size(), is(1));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getUsernames().contains("user"), is(true));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getRoles().size(), is(2));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getRoles().contains("role"), is(true));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getRoles().contains("admin"), is(true));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getPermissions(), nullValue());

        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getPermissions(), nullValue());
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getRoles(), nullValue());
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getUsernames().size(), is(1));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getUsernames().contains("user"), is(true));

        item = header.getExtraItems().get(0);
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getUsernames().size(), is(1));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getUsernames().contains("user"), is(true));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getRoles().size(), is(2));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getRoles().contains("role"), is(true));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getRoles().contains("admin"), is(true));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("page").getPermissions(), nullValue());

        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getPermissions(), nullValue());
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getRoles(), nullValue());
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getUsernames().size(), is(1));
        assertThat(((Security) item.getProperties().get("security")).getSecurityMap().get("object").getUsernames().contains("user"), is(true));

    }

    @Test
    public void testHeaderTransformV2() {
        ((SimplePropertyResolver) builder.getEnvironment().getSystemProperties()).setProperty("n2o.access.schema.id", "testHeaderV2");

        ReadCompileTerminalPipeline pipeline = compile("net/n2oapp/framework/access/metadata/schema/testHeaderV2.access.xml",
                "net/n2oapp/framework/access/metadata/transform/testHeaderAccessTransformer.header.xml");

        CompiledHeader header = (CompiledHeader) ((ReadCompileTerminalPipeline) pipeline.transform()).get(new HeaderContext("testHeaderAccessTransformer"));

        Map<String, Security.SecurityObject> secMap = ((Security) header.getItems().get(0).getProperties().get("security")).getSecurityMap();

        assertThat(secMap.get("page").getUsernames().size(), is(1));
        assertThat(secMap.get("page").getUsernames().contains("user"), is(true));
        assertThat(secMap.get("page").getRoles().size(), is(2));
        assertThat(secMap.get("page").getRoles().contains("role"), is(true));
        assertThat(secMap.get("page").getRoles().contains("admin"), is(true));
        assertThat(secMap.get("page").getPermissions(), nullValue());

        assertThat(secMap.get("object").getPermissions(), nullValue());
        assertThat(secMap.get("object").getRoles(), nullValue());
        assertThat(secMap.get("object").getUsernames().size(), is(1));
        assertThat(secMap.get("object").getUsernames().contains("user"), is(true));
        assertThat(secMap.get("object").getAnonymous(), is(true));

        secMap = ((Security) header.getExtraItems().get(0).getProperties().get("security")).getSecurityMap();
        assertThat(secMap.get("page").getUsernames().size(), is(1));
        assertThat(secMap.get("page").getUsernames().contains("user"), is(true));
        assertThat(secMap.get("page").getRoles().size(), is(2));
        assertThat(secMap.get("page").getRoles().contains("role"), is(true));
        assertThat(secMap.get("page").getRoles().contains("admin"), is(true));
        assertThat(secMap.get("page").getPermissions(), nullValue());

        assertThat(secMap.get("object").getPermissions(), nullValue());
        assertThat(secMap.get("object").getRoles(), nullValue());
        assertThat(secMap.get("object").getUsernames().size(), is(1));
        assertThat(secMap.get("object").getUsernames().contains("user"), is(true));
        assertThat(secMap.get("object").getAnonymous(), is(true));
    }
}