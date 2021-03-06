package net.n2oapp.framework.config.io.widget.table.cell;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.event.action.N2oAction;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oCheckboxCell;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.config.io.action.ActionIOv1;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.stereotype.Component;

/**
 *Чтение\запись ячейки с чекбоксом
 */
@Component
public class CheckboxCellElementIOv2 extends AbstractCellElementIOv2<N2oCheckboxCell> {
    private Namespace actionDefaultNamespace = ActionIOv1.NAMESPACE;

    @Override
    public void io(Element e, N2oCheckboxCell c, IOProcessor p) {
        super.io(e, c, p);
        p.attribute(e, "action-id", c::getActionId, c::setActionId);
        p.attributeEnum(e, "model", c::getModel, c::setModel, ReduxModel.class);
        p.attributeBoolean(e, "enabled", c::getEnabled, c::setEnabled);
        p.anyChild(e,null,c::getAction,c::setAction,p.anyOf(N2oAction.class),actionDefaultNamespace);
    }

    @Override
    public String getElementName() {
        return "checkbox";
    }

    @Override
    public Class<N2oCheckboxCell> getElementClass() {
        return N2oCheckboxCell.class;
    }
}
