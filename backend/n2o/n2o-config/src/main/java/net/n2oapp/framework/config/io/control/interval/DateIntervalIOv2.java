package net.n2oapp.framework.config.io.control.interval;

import net.n2oapp.framework.api.metadata.control.N2oField;
import net.n2oapp.framework.api.metadata.control.interval.N2oDateInterval;
import net.n2oapp.framework.api.metadata.control.properties.PopupPlacement;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import org.jdom.Element;
import org.springframework.stereotype.Component;

@Component
public class DateIntervalIOv2 extends IntervalFieldIOv2<N2oDateInterval> {

    @Override
    public void io(Element e, N2oDateInterval m, IOProcessor p) {
        super.io(e, m, p);
        p.attribute(e, "begin-default-time", m::getBeginDefaultTime, m::setBeginDefaultTime);
        p.attribute(e, "end-default-time", m::getEndDefaultTime, m::setEndDefaultTime);
        p.attribute(e, "format", m::getDateFormat, m::setDateFormat);
        p.attributeEnum(e, "popup-placement", m::getPopupPlacement, m::setPopupPlacement, PopupPlacement.class);
        p.attributeBoolean(e, "utc", m::getUtc, m::setUtc);
    }

    @Override
    public Class getElementClass() {
        return N2oDateInterval.class;
    }

    @Override
    public String getElementName() {
        return "date-interval";
    }
}
