package net.n2oapp.framework.config.metadata.merge;

import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.io.widget.form.FormElementIOV4;
import net.n2oapp.framework.config.metadata.compile.widget.FormCompiler;
import net.n2oapp.framework.config.metadata.compile.widget.N2oWidgetMerger;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oActionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oControlsPack;
import net.n2oapp.framework.config.metadata.pack.N2oFieldSetsPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.test.SourceMergerTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Тест слияния виджетов
 */
public class N2oWidgetMergerTest extends SourceMergerTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oActionsPack(), new N2oFieldSetsPack(), new N2oControlsPack())
                .ios(new FormElementIOV4())
                .compilers(new FormCompiler())
                .mergers(new N2oWidgetMerger<>());
    }

    @Test
    public void testMergeOverrideToSource() {
        N2oForm widget = merge("net/n2oapp/framework/config/metadata/local/merger/widget/parentWidgetForm.widget.xml",
                "net/n2oapp/framework/config/metadata/local/merger/widget/childWidgetForm.widget.xml")
                .get("childWidgetForm", N2oForm.class);
        assertThat(widget, notNullValue());
        assertThat(widget.getDependsOn(), is("child"));
        assertThat(widget.getName(), is("Child"));
        assertThat(widget.getQueryId(), is("parent"));
        assertThat(widget.getObjectId(), is("parent"));
        assertThat(widget.getPreFilters().length, is(2));
        assertThat(widget.getPreFields().length, is(2));
    }
}
