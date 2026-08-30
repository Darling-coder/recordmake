package com.itheima.recorddid.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.recorddid.common.Result;
import com.itheima.recorddid.common.UserContext;
import com.itheima.recorddid.entity.Record;
import com.itheima.recorddid.entity.User;
import com.itheima.recorddid.exception.BusinessException;
import com.itheima.recorddid.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    // 分页查询唱片列表接口
    @GetMapping("/page")
    public Result<IPage<Record>> pageRecord(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String recordName
    ) {
        IPage<Record> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        // 有传名称则模糊匹配
        if (recordName != null && !recordName.trim().isEmpty()) {
            wrapper.like(Record::getRecordName, recordName);
        }
        // 逻辑删除过滤
        wrapper.eq(Record::getDeleted, 0);

        // 获取当前登录用户，区分买家/卖家查询逻辑
        User loginUser = UserContext.getLoginUser();
        if ("seller".equals(loginUser.getRole())) {
            // 卖家：只查自己发布的所有唱片（上架/下架都能看）
            wrapper.eq(Record::getSellerId, loginUser.getId());
        } else {
            // 买家：只看上架商品
            wrapper.eq(Record::getStatus, 1);
        }

        IPage<Record> pageResult = recordService.page(page, wrapper);
        return Result.success(pageResult);
    }

    // 根据id查询单条唱片（编辑回显接口）
    @GetMapping("/{id}")
    public Result<Record> getOne(@PathVariable Long id) {
        Record record = recordService.getById(id);
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException(500, "该唱片不存在");
        }

        User loginUser = UserContext.getLoginUser();
        // 买家不能查看下架商品；卖家只能查看自己的唱片
        if ("buyer".equals(loginUser.getRole()) && record.getStatus() == 0) {
            throw new BusinessException(500, "商品已下架，无法查看");
        }
        if ("seller".equals(loginUser.getRole()) && !record.getSellerId().equals(loginUser.getId())) {
            throw new BusinessException(403, "无权查看他人唱片");
        }

        return Result.success(record);
    }

    // 删除唱片
    @DeleteMapping("/{id}")
    public Result<String> deleteRecord(@PathVariable Long id) {
        User loginUser = UserContext.getLoginUser();
        // 仅卖家允许删除
        if (!"seller".equals(loginUser.getRole())) {
            throw new BusinessException(403, "仅卖家可删除唱片");
        }

        Record record = recordService.getById(id);
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException(500, "该唱片不存在，删除失败");
        }
        // 只能删除自己发布的唱片
        if (!record.getSellerId().equals(loginUser.getId())) {
            throw new BusinessException(403, "无权删除他人发布的唱片");
        }

        recordService.removeById(id);
        return Result.success("删除唱片成功");
    }

    // 新增唱片
    @PostMapping
    public Result<String> addRecord(@Valid @RequestBody Record record) {
        User loginUser = UserContext.getLoginUser();
        // 校验角色：仅卖家发布商品
        if (!"seller".equals(loginUser.getRole())) {
            throw new BusinessException(403, "仅卖家可发布唱片");
        }
        // 强制赋值当前登录卖家ID，前端传的sellerId直接覆盖，防止伪造
        record.setSellerId(loginUser.getId());
        // 默认上架
        record.setStatus(1);

        // 交给service处理重名校验与保存
        recordService.addRecord(record);
        return Result.success("唱片新增成功");
    }

    // 修改唱片接口 PUT /record
    @PutMapping
    public Result<String> update(@Valid @RequestBody Record record) {
        User loginUser = UserContext.getLoginUser();
        if (!"seller".equals(loginUser.getRole())) {
            throw new BusinessException(403, "仅卖家可修改唱片");
        }

        // 交给service处理校验与更新
        recordService.updateRecord(record, loginUser.getId());
        return Result.success("唱片修改成功");
    }
}