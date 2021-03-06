package net.n2oapp.framework.access.metadata.compile;

import net.n2oapp.framework.access.metadata.Security;
import net.n2oapp.framework.api.metadata.header.CompiledHeader;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.local.CompiledQuery;
import net.n2oapp.framework.api.metadata.meta.Page;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.compile.context.HeaderContext;
import net.n2oapp.framework.config.metadata.compile.context.ObjectContext;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.context.QueryContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oHeaderPack;
import net.n2oapp.framework.config.metadata.pack.N2oObjectsPack;
import net.n2oapp.framework.config.metadata.pack.N2oQueriesPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SecurityExtAttributeMapperTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oAllPagesPack(), new N2oHeaderPack(),
                new N2oQueriesPack(), new N2oObjectsPack())
                .sources(new CompileInfo("net/n2oapp/framework/access/metadata/securityExtAttrMapperTest.page.xml"))
                .extensions(new SecurityExtensionAttributeMapper());
    }

    @Test
    public void inlineMenu() {
        CompiledHeader header = (CompiledHeader) compile("net/n2oapp/framework/access/metadata/securityExtAttrMapperTest.header.xml")
                .get(new HeaderContext("securityExtAttrMapperTest"));
        Security.SecurityObject securityObject = new Security.SecurityObject();
        securityObject.setAnonymous(false);
        securityObject.setAuthenticated(false);
        securityObject.setUsernames(Arrays.asList("user"));
        securityObject.setPermissions(Arrays.asList("admin", "user"));
        securityObject.setRoles(Arrays.asList("admin"));
        Map<String, Security.SecurityObject> securityObjectMap = new HashMap<>();
        securityObjectMap.put("custom", securityObject);
        Security security = new Security();
        security.setSecurityMap(securityObjectMap);
        assertThat(header.getItems().get(0).getProperties().get("security"), is(security));
        assertThat(((Security) header.getItems().get(0).getProperties().get("security")).getSecurityMap().get("custom"), is(securityObject));
    }

    @Test
    public void inlineToolbarAndRegion() {
        Page page = compile("net/n2oapp/framework/access/metadata/securityExtAttrMapperTest.page.xml")
                .get(new PageContext("securityExtAttrMapperTest"));
        Security.SecurityObject securityObject = new Security.SecurityObject();
        securityObject.setAnonymous(false);
        securityObject.setAuthenticated(false);
        securityObject.setUsernames(Arrays.asList("user"));
        securityObject.setPermissions(Arrays.asList("admin", "user"));
        securityObject.setRoles(Arrays.asList("admin"));
        Map<String, Security.SecurityObject> securityObjectMap = new HashMap<>();
        securityObjectMap.put("custom", securityObject);
        Security security = new Security();
        security.setSecurityMap(securityObjectMap);
        assertThat(page.getLayout().getRegions().get("single").get(0).getProperties().get("security"), is(security));
        assertThat(((Security) page.getLayout().getRegions().get("single").get(0).getProperties().get("security")).getSecurityMap().get("custom"), is(securityObject));
        assertThat(page.getLayout().getRegions().get("single").get(0).getItems().get(0).getProperties().get("security"), is(security));
        assertThat(((Security) page.getLayout().getRegions().get("single").get(0).getItems().get(0).getProperties().get("security")).getSecurityMap().get("custom"), is(securityObject));
        assertThat(page.getToolbar().get("bottomRight").get(0).getButtons().get(0).getProperties().get("security"), is(security));
        assertThat(((Security) page.getLayout().getRegions().get("single").get(0).getProperties().get("security")).getSecurityMap().get("custom"), is(securityObject));
    }

    @Test
    public void inlineObjectActions() {
        CompiledObject object = compile("net/n2oapp/framework/access/metadata/securityExtAttrMapperTest.object.xml")
                .get(new ObjectContext("securityExtAttrMapperTest"));
        CompiledObject.Operation operation = object.getOperations().get("create");
        Security.SecurityObject securityObject = new Security.SecurityObject();
        securityObject.setPermitAll(true);
        securityObject.setAuthenticated(true);
        securityObject.setDenied(false);
        securityObject.setAnonymous(false);
        securityObject.setRoles(Arrays.asList("role1", "role2"));
        securityObject.setUsernames(Arrays.asList("user1"));
        securityObject.setPermissions(Arrays.asList("p1", "p2", "p3"));
        Map<String, Security.SecurityObject> securityObjectMap = new HashMap<>();
        securityObjectMap.put("custom", securityObject);
        Security security = new Security();
        security.setSecurityMap(securityObjectMap);

        assertThat( operation.getProperties().get("security"), is(security));
        assertThat(((Security) operation.getProperties().get("security")).getSecurityMap().get("custom"), is(securityObject));
    }

    @Test
    public void inlineQuery() {
        CompiledQuery query = compile("net/n2oapp/framework/access/metadata/securityExtAttrMapperTest.query.xml")
                .get(new QueryContext("securityExtAttrMapperTest"));
        Security.SecurityObject securityObject = new Security.SecurityObject();
        securityObject.setPermitAll(true);
        securityObject.setAuthenticated(true);
        securityObject.setDenied(false);
        securityObject.setAnonymous(false);
        securityObject.setRoles(Arrays.asList("role1", "role2"));
        securityObject.setUsernames(Arrays.asList("user1"));
        securityObject.setPermissions(Arrays.asList("p1", "p2", "p3"));
        Map<String, Security.SecurityObject> securityObjectMap = new HashMap<>();
        securityObjectMap.put("custom", securityObject);
        Security security = new Security();
        security.setSecurityMap(securityObjectMap);

        assertThat(query.getProperties().get("security"), is(security));
        assertThat(((Security)query.getProperties().get("security")).getSecurityMap().get("custom"), is(securityObject));
    }
}
