package net.n2oapp.framework.config.metadata.compile.control;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.control.plain.N2oCheckbox;
import net.n2oapp.framework.api.metadata.meta.control.Checkbox;
import net.n2oapp.framework.api.metadata.meta.control.StandardField;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция чекбокса
 */

@Component
public class CheckboxCompiler extends StandardFieldCompiler<Checkbox, N2oCheckbox> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oCheckbox.class;
    }

    @Override
    public StandardField<Checkbox> compile(N2oCheckbox source, CompileContext<?,?> context, CompileProcessor p) {
        Checkbox checkbox = new Checkbox();
        checkbox.setControlSrc(p.cast(checkbox.getControlSrc(), p.resolve(property("n2o.api.control.checkbox.src"), String.class)));
        StandardField<Checkbox> field = compileStandardField(checkbox, source, context, p);
        checkbox.setLabel(p.resolveJS(field.getLabel()));
        field.setLabel(null);
        return field;
    }
}
