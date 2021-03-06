package net.n2oapp.framework.api.metadata.global.view.widget.tree;

import net.n2oapp.framework.api.metadata.global.dao.N2oPreFilter;

import java.io.Serializable;
import java.util.List;

/**
 * @author dfirstov
 * @since 10.02.2015
 */
public class InheritanceNodes implements Serializable {
    private String parentFieldId;
    private String labelFieldId;
    private String hasChildrenFieldId;
    private String queryId;
    private String iconFieldId;
    private String valueFieldId;
    private String masterFieldId;
    private String detailFieldId;
    private String searchFieldId;
    private String enabledFieldId;
    private N2oPreFilter[] preFilters;

    public N2oPreFilter[] getPreFilters() {
        return preFilters;
    }

    public void setPreFilters(N2oPreFilter[] preFilters) {
        this.preFilters = preFilters;
    }

    public String getParentFieldId() {
        return parentFieldId;
    }

    public void setParentFieldId(String parentFieldId) {
        this.parentFieldId = parentFieldId;
    }

    public String getLabelFieldId() {
        return labelFieldId;
    }

    public void setLabelFieldId(String labelFieldId) {
        this.labelFieldId = labelFieldId;
    }

    public String getHasChildrenFieldId() {
        return hasChildrenFieldId;
    }

    public void setHasChildrenFieldId(String hasChildrenFieldId) {
        this.hasChildrenFieldId = hasChildrenFieldId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getIconFieldId() {
        return iconFieldId;
    }

    public void setIconFieldId(String iconFieldId) {
        this.iconFieldId = iconFieldId;
    }

    public String getValueFieldId() {
        return valueFieldId;
    }

    public void setValueFieldId(String valueFieldId) {
        this.valueFieldId = valueFieldId;
    }

    public String getMasterFieldId() {
        return masterFieldId;
    }

    public void setMasterFieldId(String masterFieldId) {
        this.masterFieldId = masterFieldId;
    }

    public String getDetailFieldId() {
        return detailFieldId;
    }

    public void setDetailFieldId(String detailFieldId) {
        this.detailFieldId = detailFieldId;
    }

    public String getSearchFieldId() {
        return searchFieldId;
    }

    public void setSearchFieldId(String searchFieldId) {
        this.searchFieldId = searchFieldId;
    }

    @Deprecated
    public String getCanResolvedFieldId() {
        return enabledFieldId;
    }

    @Deprecated
    public void setCanResolvedFieldId(String canResolvedFieldId) {
        this.enabledFieldId = canResolvedFieldId;
    }

    public String getEnabledFieldId() {
        return enabledFieldId;
    }

    public void setEnabledFieldId(String enabledFieldId) {
        this.enabledFieldId = enabledFieldId;
    }
}