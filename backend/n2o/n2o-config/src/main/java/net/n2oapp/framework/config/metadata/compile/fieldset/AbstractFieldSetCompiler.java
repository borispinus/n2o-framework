package net.n2oapp.framework.config.metadata.compile.fieldset;

import net.n2oapp.framework.api.metadata.aware.NamespaceUriAware;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldsetRow;
import net.n2oapp.framework.api.metadata.meta.control.ControlDependency;
import net.n2oapp.framework.api.metadata.meta.control.ValidationType;
import net.n2oapp.framework.api.metadata.meta.fieldset.FieldSet;
import net.n2oapp.framework.api.script.ScriptProcessor;
import net.n2oapp.framework.config.metadata.compile.BaseSourceCompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Абстрактная реализация компиляции филдсета
 */
public abstract class AbstractFieldSetCompiler<D extends FieldSet, S extends N2oFieldSet>
        implements BaseSourceCompiler<D, S, CompileContext<?, ?>> {

    protected void compileFieldSet(D compiled, S source, CompileContext<?, ?> context, CompileProcessor p) {
        if (source.getVisible() != null)
            compiled.getVisibilityConditions().add(source.getVisible());
        if (source.getFieldLabelLocation() != null) {
            compiled.setLabelPosition(FieldSet.LabelPosition.map(source.getFieldLabelLocation(), source.getFieldLabelAlign()));
        }
        if (source.getFieldLabelAlign() != null) {
            compiled.setLabelAlignment(FieldSet.LabelAlignment.map(source.getFieldLabelAlign()));
        }
        compiled.setLabelWidth(source.getLabelWidth());

        compiled.setVisible(ScriptProcessor.resolveLinks(source.getVisible()));
        compiled.setEnabled(ScriptProcessor.resolveLinks(source.getEnabled()));

        if (source.getDependsOn() != null) {
            ControlDependency[] dependency = new ControlDependency[1];
            ControlDependency dep = new ControlDependency();
            dep.setType(ValidationType.reRender);
            dep.setOn(Arrays.asList(source.getDependsOn()));
            dependency[0] = dep;
            compiled.setDependency(dependency);
        }

        if (source.getItems() == null)
            return;
        FieldSetVisibilityScope scope = initVisibilityScope(source, p);
        List<FieldSet.Row> rows = new ArrayList<>();
        for (int i = 0; i < source.getItems().length; i++) {
            if (source.getItems()[i] instanceof N2oFieldsetRow) {
                rows.add(p.compile(source.getItems()[i], context));
            } else {
                N2oFieldsetRow newRow = new N2oFieldsetRow();
                NamespaceUriAware[] items = new NamespaceUriAware[1];
                items[0] = source.getItems()[i];
                newRow.setItems(items);
                rows.add(p.compile(newRow, context, scope));
            }
        }
        compiled.setRows(rows);
    }

    private FieldSetVisibilityScope initVisibilityScope(S source, CompileProcessor p) {
        FieldSetVisibilityScope scope = new FieldSetVisibilityScope(p.getScope(FieldSetVisibilityScope.class));
        if (source.getVisible() != null)
            scope.add(source.getVisible());
        return scope;
    }
}
