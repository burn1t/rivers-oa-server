package com.ocean.controller;

import com.ocean.query.DefinitionQuery;
import com.ocean.service.ProcessDefinitionService;
import com.ocean.utils.ResultModel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@RequestMapping("/definition")
@RestController
@Slf4j
public class ProcessDefinitionController {

    private final ProcessDefinitionService definitionService;

    public ProcessDefinitionController(ProcessDefinitionService definitionService) {
        this.definitionService = definitionService;
    }

    @PostMapping("/list")
    public ResultModel getDefinitionList(@RequestBody DefinitionQuery definitionQuery) {
        try {
            Map<String, Object> definitionList = definitionService.getDefinitionList(definitionQuery);
            return ResultModel.success(definitionList);
        } catch (Exception e) {
            log.error("获取流程定义列表失败: " + e.getMessage());
            return ResultModel.error("获取流程定义列表失败，" + e.getMessage());
        }

    }

    @PutMapping("/state/{definitionId}")
    public ResultModel updateProcessDefinitionState(@PathVariable String definitionId) {
        try {
            Boolean result = definitionService.updateProcessDefinitionState(definitionId);
            if (result)
                return ResultModel.success("激活流程定义成功");
            else
                return ResultModel.success("挂起流程定义成功");
        } catch (Exception e) {
            log.error("挂起或激活流程定义失败：" + e.getMessage());
            return ResultModel.error("挂起或激活流程定义失败，" + e.getMessage());
        }
    }

    @DeleteMapping("/{deploymentId}/{processKey}")
    public ResultModel deleteProcessDefinition(@PathVariable String deploymentId,
                                               @PathVariable String processKey) {
        try {
            Integer result = definitionService.deleteDeployment(deploymentId, processKey);
            if (result > 0)
                return ResultModel.success("删除已部署的流程定义成功");
            else
                return ResultModel.success("删除已部署的流程定义失败");
        } catch (Exception e) {
            log.error("删除已部署的流程定义失败：" + e.getMessage());
            return ResultModel.error("删除已部署的流程定义失败，" + e.getMessage());
        }
    }

    @GetMapping("/export/{type}/{definitionId}")
    public void exportFile(@PathVariable String type,
                           @PathVariable String definitionId,
                           HttpServletResponse response) {
        try {
            definitionService.exportFile(type, definitionId, response);
        } catch (Exception e) {
            log.error("导出文件失败：" + e.getMessage());
        }
    }

    @PostMapping("/deploy/file")
    public ResultModel deployByFile(@RequestParam("file") MultipartFile file) {
        try {
            definitionService.deployByFile(file);
            return ResultModel.success("部署文件成功");
        } catch (Exception e) {
            log.error("部署文件失败：" + e.getMessage());
            return ResultModel.success("部署文件失败，" + e.getMessage());
        }
    }
}
