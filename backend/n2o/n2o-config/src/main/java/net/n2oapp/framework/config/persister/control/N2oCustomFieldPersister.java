package net.n2oapp.framework.config.persister.control;

import org.jdom.Element;
import net.n2oapp.framework.api.metadata.control.N2oCustomField;
import net.n2oapp.framework.config.persister.util.PersisterJdomUtil;
import org.springframework.stereotype.Component;
import org.jdom.Namespace;

/**
 * User: iryabov
 * Date: 19.11.13
 * Time: 16:51
 */
@Component
public class N2oCustomFieldPersister extends N2oControlXmlPersister<N2oCustomField> {
    @Override
    public Element persist(N2oCustomField n2o, Namespace namespace) {
        Element n2oHiddenElement = new Element(getElementName(), namespacePrefix, namespaceUri);
        setControl(n2oHiddenElement, n2o);
        setField(n2oHiddenElement, n2o);
        PersisterJdomUtil.setAttribute(n2oHiddenElement, "src", n2o.getSrc());
        return n2oHiddenElement;
    }


    @Override
    public Class<N2oCustomField> getElementClass() {
        return N2oCustomField.class;
    }

    @Override
    public String getElementName() {
        return "custom";
    }
}
