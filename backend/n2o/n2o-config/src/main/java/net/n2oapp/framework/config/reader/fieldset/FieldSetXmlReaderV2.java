package net.n2oapp.framework.config.reader.fieldset;

import net.n2oapp.framework.api.metadata.aware.NamespaceUriAware;
import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldSet;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldsetRow;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oSetFieldSet;
import net.n2oapp.framework.api.metadata.reader.AbstractFactoredReader;
import net.n2oapp.framework.api.metadata.reader.NamespaceReader;
import net.n2oapp.framework.config.reader.tools.CssClassAwareReader;
import net.n2oapp.framework.config.reader.util.ReaderJdomUtil;
import org.springframework.stereotype.Component;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.List;

import static net.n2oapp.framework.config.reader.util.ReaderJdomUtil.getAttributeString;

/**
 * User: iryabov
 * Date: 16.01.14
 * Time: 9:26
 */
@Component
public class FieldSetXmlReaderV2 extends AbstractFactoredReader<N2oFieldSet> implements NamespaceReader<N2oFieldSet> {
    @Override
    public N2oFieldSet read(Element element, Namespace namespace) {
        N2oFieldSet n2oFieldSet = new N2oSetFieldSet();
        String refId = ReaderJdomUtil.getAttributeString(element, "ref-id");
        if (refId != null) {
            n2oFieldSet.setRefId(refId);
        }
        n2oFieldSet.setLabel(ReaderJdomUtil.getAttributeString(element, "label"));
        FieldSetReaderUtil.getType(element, "header", n2oFieldSet);
        n2oFieldSet.setDependencyCondition(ReaderJdomUtil.getAttributeString(element, "dependency-condition"));
        n2oFieldSet.setFieldLabelLocation(ReaderJdomUtil.getAttributeEnum(element,
                "field-label-location", N2oFieldSet.FieldLabelLocation.class));
        n2oFieldSet.setCssClass(getAttributeString(element, "css-class"));
        n2oFieldSet.setStyle(getAttributeString(element, "style"));

        if (refId != null) return n2oFieldSet;
        List<Element> children = element.getChildren();
        n2oFieldSet.setItems(new NamespaceUriAware[children.size()]);
        int i = 0;
        for (Element child : children) {
            if (child.getNamespace().equals(namespace) && child.getName().equals("row")) {
                N2oFieldsetRow row = new N2oFieldsetRow();
                row.setItems(new NamespaceUriAware[child.getChildren().size()]);
                int j = 0;
                for (Object field : child.getChildren()) {
                    row.getItems()[j] = (N2oField) readerFactory.produce((Element) field)
                            .read((Element) field);
                    j++;
                }
                n2oFieldSet.getItems()[i] = row;
            } else {
                n2oFieldSet.getItems()[i] = (N2oField) readerFactory.produce(child)
                        .read(child);
            }
            i++;
        }
        CssClassAwareReader.getInstance().read(n2oFieldSet, element);
        return n2oFieldSet;
    }


    @Override
    public Class<N2oFieldSet> getElementClass() {
        return N2oFieldSet.class;
    }

    @Override
    public String getNamespaceUri() {
        return "http://n2oapp.net/framework/config/schema/fieldset-2.0";
    }

    @Override
    public String getElementName() {
        return "field-set";
    }
}
