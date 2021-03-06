package net.n2oapp.framework.config.io.fieldset;

import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldsetColumn;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.io.NamespaceIO;
import net.n2oapp.framework.config.io.control.ControlIOv2;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class ColElementIO4 implements NamespaceIO<N2oFieldsetColumn> {
    private static final Namespace DEFAULT_NAMESPACE = FieldsetIOv4.NAMESPACE;
    private static final Namespace controlDefaultNamespace = ControlIOv2.NAMESPACE;

    @Override
    public void io(Element e, N2oFieldsetColumn col, IOProcessor p) {
        p.attribute(e, "class", col::getClassRow, col::setClassRow);
        p.attributeInteger(e, "size", col::getSize, col::setSize);
        p.anyChildren(e, null, col::getItems, col::setItems, p.anyOf(), DEFAULT_NAMESPACE, controlDefaultNamespace);
    }

    @Override
    public String getElementName() {
        return "col";
    }

    @Override
    public String getNamespaceUri() {
        return FieldsetIOv4.NAMESPACE.getURI();
    }

    @Override
    public Class<N2oFieldsetColumn> getElementClass() {
        return N2oFieldsetColumn.class;
    }
}