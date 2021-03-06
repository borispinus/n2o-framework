package net.n2oapp.framework.config.metadata.compile.page;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.config.util.CompileUtil;

import java.io.Serializable;

/**
 * Информация о странице при сборке внутренних метаданных
 */
@Getter
@Setter
public class PageScope implements Serializable {
    private String pageId;

    public String getGlobalWidgetId(String localWidgetId) {
        return CompileUtil.generateWidgetId(pageId, localWidgetId);
    }
}
