package net.n2oapp.framework.config.io.widget.table.cell;

import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oBadgeCell;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.Position;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import org.jdom.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение\запись ячейки с текстом со стилем или без.
 */
@Component
public class BadgeCellElementIOv2 extends AbstractCellElementIOv2<N2oBadgeCell> {
    @Override
    public void io(Element e, N2oBadgeCell c, IOProcessor p) {
        super.io(e, c, p);
        p.attributeEnum(e, "position", c::getPosition, c::setPosition, Position.class);
        p.attribute(e,"text",c::getText,c::setText);
        p.attribute(e,"color",c::getColor,c::setColor);
        p.child(e, null, "switch", c::getN2oSwitch, c::setN2oSwitch, new SwitchIO());
    }


    @Override
    public String getElementName() {
        return "badge";
    }

    @Override
    public Class<N2oBadgeCell> getElementClass() {
        return N2oBadgeCell.class;
    }
}
