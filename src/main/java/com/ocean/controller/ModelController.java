package com.ocean.controller;

import com.ocean.common.BaseResponse;
import com.ocean.query.ModelAddQuery;
import com.ocean.query.ModelQuery;
import com.ocean.service.ModelService;
import com.ocean.utils.ResponseUtil;
import com.ocean.utils.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/model")
@Slf4j
public class ModelController {
    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping
    public ResultModel addModel(@RequestBody ModelAddQuery modelAddQuery){
        try {
            modelService.addModel(modelAddQuery);
            return ResultModel.success("模型创建成功");
        } catch (Exception e) {
            log.error("模型创建失败: " + e.getMessage());
            return ResultModel.error("模型创建失败，" + e.getMessage());
        }
    }

    @PostMapping("/list")
    public ResultModel getModelList(@RequestBody ModelQuery model) {
        try {
            Map<String, Object> result = modelService.getModelList(model);
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("获取模型列表失败，" + e.getMessage());
            return ResultModel.error("获取模型列表失败，" + e.getMessage());
        }

    }

    @PostMapping("/deploy/{modelId}")
    public ResultModel deployModel(@PathVariable("modelId") String modelId) {
        try {
            modelService.deployModel(modelId);
            return ResultModel.success("部署流程定义成功");
        } catch (Exception e) {
            log.error("部署流程定义失败: " + e.getMessage());
            return ResultModel.error("部署流程定义失败，" + e.getMessage());
        }
    }

    @GetMapping("/export/zip/{modelId}")
    public void exportModelZip(@PathVariable("modelId") String modelId,
                               HttpServletResponse response) {
        try {
            modelService.exportZip(modelId, response);
        } catch (Exception e) {
            log.error("获取模型压缩包失败");
        }
    }

    @DeleteMapping("/{modelId}")
    public ResultModel deleteModel(@PathVariable("modelId") String modelId) {
        try {
            modelService.deleteModel(modelId);
            return ResultModel.success("删除流程模型成功");
        } catch (Exception e) {
            log.error("删除流程模型失败: " + e.getMessage());
            return ResultModel.error("删除流程模型失败，" + e.getMessage());
        }
    }

}
