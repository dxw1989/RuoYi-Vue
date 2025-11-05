package com.ruoyi.web.controller.flowable;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.flowable.domain.FlowProcessDefinition;
import com.ruoyi.flowable.service.IFlowProcessDefinitionService;

/**
 * Flowable 流程定义接口
 */
@RestController
@RequestMapping("/flowable/definition")
public class FlowDefinitionController extends BaseController
{
    @Autowired
    private IFlowProcessDefinitionService flowProcessDefinitionService;

    @PreAuthorize("@ss.hasPermi('flowable:definition:list')")
    @GetMapping("/list")
    public TableDataInfo list(FlowProcessDefinition query)
    {
        startPage();
        List<FlowProcessDefinition> list = flowProcessDefinitionService.selectProcessDefinitionList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('flowable:definition:query')")
    @GetMapping(value = "/{definitionId}")
    public AjaxResult getInfo(@PathVariable String definitionId)
    {
        return AjaxResult.success(flowProcessDefinitionService.selectProcessDefinitionById(definitionId));
    }

    @PreAuthorize("@ss.hasPermi('flowable:definition:query')")
    @GetMapping(value = "/{definitionId}/xml")
    public AjaxResult getXml(@PathVariable String definitionId)
    {
        return AjaxResult.success(flowProcessDefinitionService.selectProcessDefinitionXml(definitionId));
    }

    @PreAuthorize("@ss.hasPermi('flowable:definition:suspend')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{definitionId}/suspend")
    public AjaxResult suspend(@PathVariable String definitionId)
    {
        flowProcessDefinitionService.suspendProcessDefinition(definitionId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:definition:activate')")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{definitionId}/activate")
    public AjaxResult activate(@PathVariable String definitionId)
    {
        flowProcessDefinitionService.activateProcessDefinition(definitionId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:definition:remove')")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @DeleteMapping(value = "/deployment/{deploymentId}")
    public AjaxResult remove(@PathVariable String deploymentId,
            @RequestParam(value = "cascade", defaultValue = "true") boolean cascade)
    {
        flowProcessDefinitionService.deleteDeployment(deploymentId, cascade);
        return AjaxResult.success();
    }
}
