package net.n2oapp.framework.api.metadata.meta.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.Compiled;

import java.util.ArrayList;
import java.util.List;

/**
 * Клиентская модель элемента ввода
 */
@Getter
@Setter
public abstract class Control implements Compiled {
    private String id;
    @JsonProperty("src")
    private String controlSrc;
    @JsonProperty
    private String className;

    public boolean containsHimself(DataSet dataSet) {
        Object value = dataSet.get(getId());
        return value != null && !value.toString().isEmpty();
    }

}
