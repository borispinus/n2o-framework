package net.n2oapp.framework.config.metadata.compile.control;

import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.JsonMetadataTestBase;
import org.junit.Before;
import org.junit.Test;

public class InputSelectTreeJsonTest extends JsonMetadataTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.sources(new CompileInfo("net/n2oapp/framework/config/metadata/compile/field/testSelect.query.xml"));
        builder.packs(new N2oPagesPack(), new N2oRegionsPack(), new N2oWidgetsPack(), new N2oFieldSetsPack(),
                new N2oActionsPack(), new N2oAllDataPack(), new N2oControlsV2IOPack());
        builder.compilers(new InputSelectTreeCompiler());
    }

    @Test
    public void json() {
        check("net/n2oapp/framework/config/metadata/compile/field/testInputSelectTreeJson.widget.xml",
                "components/controls/InputSelectTree/InputSelectTreeContainer.meta.json")
                .cutXml("form.fieldsets[0].rows[0].cols[0].fields[0].control")
                .exclude("src", "loading", "value", "disabled", "valueFieldId", "imageFieldId",
                        "badgeFieldId", "badgeColorFieldId", "filter", "groupFieldId", "format", "notFoundContent",
                        "showCheckedStrategy", "hasChildrenFieldId", "hasSearch", "readOnly", "allowClear", "showSearch")
                .assertEquals();
    }
}
