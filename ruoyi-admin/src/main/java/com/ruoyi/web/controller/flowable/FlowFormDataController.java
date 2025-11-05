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
import com.ruoyi.flowable.domain.FlowFormDataInfo;
import com.ruoyi.flowable.service.IFlowFormDataInfoService;

/**
 * Flowable 表单数据接口
 */
@RestController
@RequestMapping("/flowable/form/data")
public class FlowFormDataController extends BaseController
{
    @Autowired
    private IFlowFormDataInfoService flowFormDataInfoService;

    @PreAuthorize("@ss.hasPermi('flowable:formData:list')")
    @GetMapping("/list")
    public TableDataInfo list(FlowFormDataInfo dataInfo)
    {
        startPage();
        List<FlowFormDataInfo> list = flowFormDataInfoService.selectFlowFormDataInfoList(dataInfo);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('flowable:formData:query')")
    @GetMapping(value = "/{dataId}")
    public AjaxResult getInfo(@PathVariable Long dataId)
    {
        return AjaxResult.success(flowFormDataInfoService.selectFlowFormDataInfoById(dataId));
    }

    @PreAuthorize("@ss.hasPermi('flowable:formData:add')")
    @Log(title = "流程表单数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FlowFormDataInfo dataInfo)
    {
        dataInfo.setCreateBy(getUsername());
        dataInfo.setUpdateBy(getUsername());
        dataInfo.setSubmitBy(getUsername());
        return toAjax(flowFormDataInfoService.insertFlowFormDataInfo(dataInfo));
    }

    @PreAuthorize("@ss.hasPermi('flowable:formData:edit')")
    @Log(title = "流程表单数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FlowFormDataInfo dataInfo)
    {
        dataInfo.setUpdateBy(getUsername());
        return toAjax(flowFormDataInfoService.updateFlowFormDataInfo(dataInfo));
    }

    @PreAuthorize("@ss.hasPermi('flowable:formData:remove')")
    @Log(title = "流程表单数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dataIds}")
    public AjaxResult remove(@PathVariable Long[] dataIds)
    {
        return toAjax(flowFormDataInfoService.deleteFlowFormDataInfoByIds(dataIds));
    }
}
