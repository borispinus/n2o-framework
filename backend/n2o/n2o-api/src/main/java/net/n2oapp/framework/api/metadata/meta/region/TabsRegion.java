package net.n2oapp.framework.api.metadata.meta.region;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.meta.widget.Widget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Клиентская модель региона в виде вкладок.
 */
@Getter
@Setter
public class TabsRegion extends Region {

    @JsonProperty
    private List<Tab> tabs;

    @Override
    @JsonProperty("tabs")
    public List<? extends Item> getItems() {
        return super.getItems();
    }

    @Getter
    @Setter
    public static class Tab extends Item {
        @JsonProperty
        private String icon;
    }
}
