package net.n2oapp.framework.config.io.widget.table;

import net.n2oapp.framework.api.metadata.global.view.action.LabelType;
import net.n2oapp.framework.api.metadata.global.view.widget.table.N2oPagination;
import net.n2oapp.framework.api.metadata.global.view.widget.table.N2oRow;
import net.n2oapp.framework.api.metadata.global.view.widget.table.N2oTable;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.AbstractColumn;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.DirectionType;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.N2oSimpleColumn;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oCell;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.config.io.control.ControlIOv2;
import net.n2oapp.framework.config.io.fieldset.FieldsetIOv4;
import net.n2oapp.framework.config.io.widget.WidgetElementIOv4;
import net.n2oapp.framework.config.io.widget.table.cell.CellIOv2;
import net.n2oapp.framework.config.io.widget.table.cell.SwitchIO;
import org.jdom.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение\запись таблицы
 */
@Component
public class TableElementIOV4 extends WidgetElementIOv4<N2oTable> {

    @Override
    public String getElementName() {
        return "table";
    }


    @Override
    public Class<N2oTable> getElementClass() {
        return N2oTable.class;
    }

    @Override
    public void io(Element e, N2oTable t, IOProcessor p) {
        super.io(e, t, p);
        p.anyChildren(e, "columns", t::getColumns, t::setColumns,
                p.oneOf(AbstractColumn.class).add("column", N2oSimpleColumn.class, this::column));
        p.child(e, null, "rows", t::getRows, t::setRows, N2oRow::new, this::rows);
        p.child(e, null, "pagination", t::getPagination, t::setPagination, N2oPagination::new, this::pagination);
        p.childAttributeEnum(e, "filters", "place", t::getFilterPosition, t::setFilterPosition, N2oTable.FilterPosition.class);
        p.anyChildren(e, "filters", t::getFilters, t::setFilters, p.anyOf(), FieldsetIOv4.NAMESPACE, ControlIOv2.NAMESPACE);
    }

    private void abstractColumn(Element e, AbstractColumn c, IOProcessor p) {
        p.attribute(e, "id", c::getId, c::setId);
        p.attribute(e, "text-field-id", c::getTextFieldId, c::setTextFieldId);
        p.attribute(e, "tooltip-field-id", c::getTooltipFieldId, c::setTooltipFieldId);
        p.attributeBoolean(e, "visible", c::getVisible, c::setVisible);
        p.attribute(e, "label", c::getLabelName, c::setLabelName);
        p.attribute(e, "icon", c::getLabelIcon, c::setLabelIcon);
        p.attributeEnum(e, "type", c::getLabelType, c::setLabelType, LabelType.class);
        p.attribute(e, "sorting-field-id", c::getSortingFieldId, c::setSortingFieldId);
        p.attribute(e, "width", c::getWidth, c::setWidth);
        p.attributeEnum(e, "sorting-direction", c::getSortingDirection, c::setSortingDirection, DirectionType.class);
    }

    private void column(Element e, N2oSimpleColumn c, IOProcessor p) {
        abstractColumn(e, c, p);
        p.anyChild(e, null, c::getCell, c::setCell, p.anyOf(N2oCell.class), CellIOv2.NAMESPACE);
    }


    private void rows(Element e, N2oRow r, IOProcessor p) {
        p.attribute(e, "color-field-id", r::getColorFieldId, r::setColorFieldId);
        p.child(e, null, "switch", r::getColor, r::setColor, new SwitchIO());
    }


    private void pagination(Element e, N2oPagination page, IOProcessor p) {
        p.attribute(e, "src", page::getSrc, page::setSrc);
        p.attributeBoolean(e, "prev", page::getPrev, page::setPrev);
        p.attributeBoolean(e, "next", page::getNext, page::setNext);
        p.attributeBoolean(e, "last", page::getLast, page::setLast);
        p.attributeBoolean(e, "first", page::getFirst, page::setFirst);
        p.attributeBoolean(e, "show-count", page::getShowCount, page::setShowCount);
        p.attributeBoolean(e, "hide-single-page", page::getHideSinglePage, page::setHideSinglePage);
    }
}
