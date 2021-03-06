package net.n2oapp.framework.api.ui;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.ParametrizedMessagePlaceHoldersResolver;
import net.n2oapp.framework.api.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Объект ответа на запрос за данными
 */
public class ResponseInfo  {

    private List<ResponseMessage> messageList;
    private Map<String, Object> attributes;


    public void addMessage(ResponseMessage message) {
        if (messageList == null)
            messageList = new ArrayList<>();
        messageList.add(message);
    }

    public void addAttribute(String name, Object value) {
        if (attributes == null)
            attributes = new LinkedHashMap<>();
        attributes.put(name, value);
    }


    public List<ResponseMessage> getMessageList() {
        return messageList != null ? messageList : Collections.emptyList();
    }


    public void prepare(DataSet dataset) {
        getMessageList().forEach(m -> {
            String msg = StringUtils.resolveLinks(m.getText(), dataset);
            m.setText(msg);
        });
    }


    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
