package com.ruoyi.flowable.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 动态表单定义
 */
public class FlowFormInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表单主键 */
    private Long formId;

    /** 表单标识 */
    @Excel(name = "表单标识")
    private String formKey;

    /** 表单名称 */
    @Excel(name = "表单名称")
    private String formName;

    /** 表单类型 */
    @Excel(name = "表单类型")
    private String formType;

    /** 表单描述 */
    private String description;

    /** 表单配置（JSON） */
    private String formConfig;

    /** 是否启用 */
    private String status;

    /** 删除标记 */
    private String delFlag;

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

    public String getFormName()
    {
        return formName;
    }

    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    public String getFormType()
    {
        return formType;
    }

    public void setFormType(String formType)
    {
        this.formType = formType;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getFormConfig()
    {
        return formConfig;
    }

    public void setFormConfig(String formConfig)
    {
        this.formConfig = formConfig;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
