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
import com.ruoyi.flowable.domain.FlowModelInfo;
import com.ruoyi.flowable.domain.dto.FlowModelEditorUpdateRequest;
import com.ruoyi.flowable.service.IFlowModelInfoService;

/**
 * Flowable 模型接口
 */
@RestController
@RequestMapping("/flowable/model")
public class FlowModelController extends BaseController
{
    @Autowired
    private IFlowModelInfoService flowModelInfoService;

    @PreAuthorize("@ss.hasPermi('flowable:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(FlowModelInfo flowModelInfo)
    {
        startPage();
        List<FlowModelInfo> list = flowModelInfoService.selectFlowModelInfoList(flowModelInfo);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:query')")
    @GetMapping(value = "/{modelId}")
    public AjaxResult getInfo(@PathVariable Long modelId)
    {
        return AjaxResult.success(flowModelInfoService.selectFlowModelInfoById(modelId));
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:query')")
    @GetMapping(value = "/key/{modelKey}")
    public AjaxResult getByKey(@PathVariable String modelKey)
    {
        return AjaxResult.success(flowModelInfoService.selectFlowModelInfoByKey(modelKey));
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:add')")
    @Log(title = "流程模型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FlowModelInfo flowModelInfo)
    {
        flowModelInfo.setCreateBy(getUsername());
        flowModelInfo.setUpdateBy(getUsername());
        return toAjax(flowModelInfoService.insertFlowModelInfo(flowModelInfo));
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:edit')")
    @Log(title = "流程模型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FlowModelInfo flowModelInfo)
    {
        flowModelInfo.setUpdateBy(getUsername());
        return toAjax(flowModelInfoService.updateFlowModelInfo(flowModelInfo));
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:design')")
    @Log(title = "流程模型", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{modelId}/editor")
    public AjaxResult saveEditor(@PathVariable Long modelId,
            @Validated @RequestBody FlowModelEditorUpdateRequest request)
    {
        FlowModelInfo flowModelInfo = flowModelInfoService.saveModelEditor(modelId, request.getModelContent(),
            request.getModelEditorJson(), request.getFormId(), request.getFormKey(), getUsername());
        return AjaxResult.success(flowModelInfo);
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:query')")
    @GetMapping(value = "/{modelId}/editor")
    public AjaxResult editor(@PathVariable Long modelId)
    {
        FlowModelInfo flowModelInfo = flowModelInfoService.selectFlowModelInfoById(modelId);
        return AjaxResult.success(flowModelInfo);
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:deploy')")
    @Log(title = "流程模型", businessType = BusinessType.UPDATE)
    @PutMapping("/{modelId}/deploy")
    public AjaxResult deploy(@PathVariable Long modelId)
    {
        flowModelInfoService.deployFlowModel(modelId, getUsername());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:model:remove')")
    @Log(title = "流程模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{modelIds}")
    public AjaxResult remove(@PathVariable Long[] modelIds)
    {
        return toAjax(flowModelInfoService.deleteFlowModelInfoByIds(modelIds));
    }
}
