package com.ruoyi.web.controller.flowable;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.flowable.domain.FlowFormInfo;
import com.ruoyi.flowable.service.IFlowFormInfoService;

/**
 * Flowable 动态表单接口
 */
@RestController
@RequestMapping("/flowable/form")
public class FlowFormController extends BaseController
{
    @Autowired
    private IFlowFormInfoService flowFormInfoService;

    @PreAuthorize("@ss.hasPermi('flowable:form:list')")
    @GetMapping("/list")
    public TableDataInfo list(FlowFormInfo formInfo)
    {
        startPage();
        List<FlowFormInfo> list = flowFormInfoService.selectFlowFormInfoList(formInfo);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('flowable:form:query')")
    @GetMapping(value = "/{formId}")
    public AjaxResult getInfo(@PathVariable Long formId)
    {
        return AjaxResult.success(flowFormInfoService.selectFlowFormInfoById(formId));
    }

    @PreAuthorize("@ss.hasPermi('flowable:form:add')")
    @Log(title = "流程表单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FlowFormInfo formInfo)
    {
        formInfo.setCreateBy(getUsername());
        formInfo.setUpdateBy(getUsername());
        return toAjax(flowFormInfoService.insertFlowFormInfo(formInfo));
    }

    @PreAuthorize("@ss.hasPermi('flowable:form:edit')")
    @Log(title = "流程表单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FlowFormInfo formInfo)
    {
        formInfo.setUpdateBy(getUsername());
        return toAjax(flowFormInfoService.updateFlowFormInfo(formInfo));
    }

    @PreAuthorize("@ss.hasPermi('flowable:form:remove')")
    @Log(title = "流程表单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{formIds}")
    public AjaxResult remove(@PathVariable Long[] formIds)
    {
        return toAjax(flowFormInfoService.deleteFlowFormInfoByIds(formIds));
    }
}
