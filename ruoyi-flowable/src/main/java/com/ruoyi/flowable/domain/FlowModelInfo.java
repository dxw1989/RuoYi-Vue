package com.ruoyi.flowable.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Flowable 模型信息
 */
public class FlowModelInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模型主键 */
    private Long modelId;

    /** 模型标识 */
    @Excel(name = "模型标识")
    private String modelKey;

    /** 模型名称 */
    @Excel(name = "模型名称")
    private String modelName;

    /** 模型分类 */
    @Excel(name = "模型分类")
    private String category;

    /** 模型状态 */
    @Excel(name = "模型状态")
    private Integer status;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 绑定的表单ID */
    @Excel(name = "表单ID")
    private Long formId;

    /** 绑定的表单Key */
    @Excel(name = "表单标识")
    private String formKey;

    /** Flowable 部署ID */
    private String deployId;

    /** 流程定义ID */
    private String definitionId;

    /** 流程定义Key */
    private String definitionKey;

    /** 流程定义名称 */
    private String definitionName;

    /** 模型设计内容（BPMN XML） */
    private String modelContent;

    /** 模型设计器原始JSON */
    private String modelEditorJson;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deployTime;

    /** 删除标识（0正常 1删除） */
    private String delFlag;

    public Long getModelId()
    {
        return modelId;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public String getModelKey()
    {
        return modelKey;
    }

    public void setModelKey(String modelKey)
    {
        this.modelKey = modelKey;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
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

    public String getDeployId()
    {
        return deployId;
    }

    public void setDeployId(String deployId)
    {
        this.deployId = deployId;
    }

    public String getDefinitionId()
    {
        return definitionId;
    }

    public void setDefinitionId(String definitionId)
    {
        this.definitionId = definitionId;
    }

    public String getDefinitionKey()
    {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey)
    {
        this.definitionKey = definitionKey;
    }

    public String getDefinitionName()
    {
        return definitionName;
    }

    public void setDefinitionName(String definitionName)
    {
        this.definitionName = definitionName;
    }

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

    public Date getDeployTime()
    {
        return deployTime;
    }

    public void setDeployTime(Date deployTime)
    {
        this.deployTime = deployTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
