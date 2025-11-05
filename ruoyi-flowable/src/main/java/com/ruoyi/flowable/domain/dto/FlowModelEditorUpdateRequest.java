package com.ruoyi.flowable.domain.dto;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 模型设计保存请求
 */
public class FlowModelEditorUpdateRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** BPMN XML 内容 */
    @NotBlank(message = "流程模型XML不能为空")
    private String modelContent;

    /** 模型设计器JSON */
    @NotBlank(message = "流程模型设计数据不能为空")
    private String modelEditorJson;

    /** 绑定表单ID */
    private Long formId;

    /** 绑定表单标识 */
    @Size(max = 64, message = "表单标识长度不能超过64个字符")
    private String formKey;

    public String getModelContent()
    {
        return modelContent;
    }

    public void setModelContent(String modelContent)
    {
        this.modelContent = modelContent;
    }

    public String getModelEditorJson()
    {
        return modelEditorJson;
    }

    public void setModelEditorJson(String modelEditorJson)
    {
        this.modelEditorJson = modelEditorJson;
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
}
