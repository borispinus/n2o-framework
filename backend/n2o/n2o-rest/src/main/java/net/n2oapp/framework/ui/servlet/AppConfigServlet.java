package net.n2oapp.framework.ui.servlet;

import net.n2oapp.framework.api.metadata.header.Header;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileBindTerminalPipeline;
import net.n2oapp.framework.api.metadata.pipeline.ReadCompileTerminalPipeline;
import net.n2oapp.framework.config.metadata.compile.context.HeaderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * User: operhod
 * Date: 19.09.13
 * Time: 10:25
 */
public class AppConfigServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AppConfigServlet.class);
    private AppConfigJsonWriter appConfigJsonWriter;
    private ExposedResourceBundleMessageSource messageSource;
    private ReadCompileBindTerminalPipeline pipeline;
    private String headerSourceId;

    @Override
    public void init() {
        appConfigJsonWriter.loadValues();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse res) throws IOException {
        Map<String, Object> addedValues = new HashMap<>();
        addedValues.put("menu", getMenu());
        addedValues.put("messages", getMessages());

        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        try {
            appConfigJsonWriter.writeValues(out, addedValues);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(out);
            log.error(e.getMessage(), e);
        } finally {
            out.close();
        }

    }

    private Header getMenu() {
        if (headerSourceId != null && !headerSourceId.isEmpty())
            return pipeline.get(new HeaderContext(headerSourceId), null);
        else
            return null;
    }

    private Map<String, String> getMessages() {
        MessageSourceAccessor accessor = new MessageSourceAccessor(messageSource);
        Locale locale = LocaleContextHolder.getLocale();
        Set<String> messagesBaseNames = messageSource.getBasenameSet();
        Map<String, String> map = new TreeMap<>();
        for (String baseName : messagesBaseNames) {
            Set<String> keys = messageSource.getKeys(baseName, locale);
            for (String key : keys) {
                map.put(key, accessor.getMessage(key, locale));
            }
        }
        return map;
    }

    public void setAppConfigJsonWriter(AppConfigJsonWriter appConfigJsonWriter) {
        this.appConfigJsonWriter = appConfigJsonWriter;
    }

    public AppConfigJsonWriter getAppConfigJsonWriter() {
        return appConfigJsonWriter;
    }

    public void setMessageSource(ExposedResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setPipeline(ReadCompileBindTerminalPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setHeaderSourceId(String headerSourceId) {
        this.headerSourceId = headerSourceId;
    }
}
