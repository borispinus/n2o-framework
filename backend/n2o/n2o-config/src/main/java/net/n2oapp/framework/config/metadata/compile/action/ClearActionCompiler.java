package net.n2oapp.framework.config.metadata.compile.action;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.event.action.N2oClearAction;
import net.n2oapp.framework.api.metadata.meta.action.clear.ClearAction;
import org.springframework.stereotype.Component;


import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Сборка действия очистки модели
 */
@Component
public class ClearActionCompiler extends AbstractActionCompiler<ClearAction, N2oClearAction>{
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oClearAction.class;
    }

    @Override
    public ClearAction compile(N2oClearAction source, CompileContext<?, ?> context, CompileProcessor p) {
        ClearAction clearAction = new ClearAction();
        compileAction(clearAction, source, p);
        clearAction.setId("clear");
        clearAction.getOptions().setType(p.resolve(property("n2o.api.action.clear.type"), String.class));
        clearAction.getOptions().getPayload().setPrefixes(p.cast(source.getModel(), new String[]{ReduxModel.EDIT.getId()}));
        clearAction.getOptions().getPayload().setKey(initTargetWidget(source, context, p));
        return clearAction;
    }
}
