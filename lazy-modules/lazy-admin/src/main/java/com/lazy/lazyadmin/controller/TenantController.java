package com.lazy.lazyadmin.controller;

import com.lazy.c.core.domain.R;
import com.lazy.lazyadmin.domain.entity.SysTenant;
import com.lazy.lazyadmin.service.SysTenantService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

/**
 * 租户管理接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/tenant")
class TenantController {

    private final SysTenantService sysTenantService;

    /**
     * 新增租户
     *
     * @param sysTenant 租户信息
     * @return R
     */
    @PostMapping("/add/tenant")
    public R<Void> addTenant(@RequestBody SysTenant sysTenant) {
        sysTenantService.save(sysTenant);
        return R.ok();
    }

    /**
     * 查询租户
     *
     * @param tenantId 租户ID
     * @return R
     */
    @GetMapping("/query/tenant/{tenantId}")
    public R<SysTenant> queryTenant(@PathVariable Long tenantId) {

        System.out.println(Thread.currentThread());

        SysTenant one = sysTenantService.getById(tenantId);
        return R.ok(one);
    }


}
