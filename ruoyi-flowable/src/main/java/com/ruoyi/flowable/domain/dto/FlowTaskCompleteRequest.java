package com.ruoyi.flowable.domain.dto;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 任务完成请求
 */
public class FlowTaskCompleteRequest
{
    /** 任务ID */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    /** 提交人 */
    private String submitBy;

    /** 业务主键 */
    private String businessKey;

    /** 表单ID */
    private Long formId;

    /** 表单标识 */
    private String formKey;

    /** 表单值 */
    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> formValues;

    /** 全局变量 */
    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> variables;

    /** 本地变量 */
    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> localVariables;

    /** 意见 */
    private String comment;

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getSubmitBy()
    {
        return submitBy;
    }

    public void setSubmitBy(String submitBy)
    {
        this.submitBy = submitBy;
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

    public Map<String, Object> getLocalVariables()
    {
        return localVariables;
    }

    public void setLocalVariables(Map<String, Object> localVariables)
    {
        this.localVariables = localVariables;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
