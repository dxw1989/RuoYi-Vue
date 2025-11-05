package com.ruoyi.flowable.domain.dto;

import java.io.Serializable;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 流程启动请求
 */
public class FlowProcessStartRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 流程定义Key */
    private String definitionKey;

    /** 流程定义ID */
    private String definitionId;

    /** 业务主键 */
    private String businessKey;

    /** 表单ID */
    private Long formId;

    /** 表单标识 */
    private String formKey;

    /** 表单值 */
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, Object> formValues;

    /** 流程变量 */
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, Object> variables;

    /** 发起人 */
    private String startUserId;

    public String getDefinitionKey()
    {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey)
    {
        this.definitionKey = definitionKey;
    }

    public String getDefinitionId()
    {
        return definitionId;
    }

    public void setDefinitionId(String definitionId)
    {
        this.definitionId = definitionId;
    }

    public String getBusinessKey()
    {
        return businessKey;
    }

    public void setBusinessKey(String businessKey)
    {
        this.businessKey = businessKey;
    }

    public Long getFormId()
    {
        return formId;
    }

    public void setFormId(Long formId)
    {
        this.formId = formId;
    }

    public String getFormKey()
    {
        return formKey;
    }

    public void setFormKey(String formKey)
    {
        this.formKey = formKey;
    }

    public Map<String, Object> getFormValues()
    {
        return formValues;
    }

    public void setFormValues(Map<String, Object> formValues)
    {
        this.formValues = formValues;
    }

    public Map<String, Object> getVariables()
    {
        return variables;
    }

    public void setVariables(Map<String, Object> variables)
    {
        this.variables = variables;
    }

    public String getStartUserId()
    {
        return startUserId;
    }

    public void setStartUserId(String startUserId)
    {
        this.startUserId = startUserId;
    }
}
