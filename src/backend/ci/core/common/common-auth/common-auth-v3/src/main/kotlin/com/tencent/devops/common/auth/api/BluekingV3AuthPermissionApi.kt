/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.common.auth.api

import com.tencent.bk.sdk.iam.config.IamConfiguration
import com.tencent.bk.sdk.iam.dto.InstanceDTO
import com.tencent.bk.sdk.iam.dto.action.ActionDTO
import com.tencent.bk.sdk.iam.helper.AuthHelper
import com.tencent.bk.sdk.iam.service.PolicyService
import com.tencent.devops.common.auth.code.AuthServiceCode
import com.tencent.devops.common.auth.utlis.ActionUtils
import com.tencent.devops.common.auth.utlis.AuthUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class BluekingV3AuthPermissionApi @Autowired constructor(
    private val authHelper: AuthHelper,
    private val policyService: PolicyService,
    private val iamConfiguration: IamConfiguration
) : AuthPermissionApi {
    override fun addResourcePermissionForUsers(
        userId: String,
        projectCode: String,
        serviceCode: AuthServiceCode,
        permission: AuthPermission,
        resourceType: AuthResourceType,
        resourceCode: String,
        userIdList: List<String>,
        supplier: (() -> List<String>)?
    ): Boolean {
        return true
    }

    // 判断用户是否有某个动作的权限。 该动作无需绑定实例。如：判断是否有创建权限，创建无需挂任何实例。 若要判断是否有某实例的权限不能用该接口。
    override fun validateUserResourcePermission(
        user: String,
        serviceCode: AuthServiceCode,
        resourceType: AuthResourceType,
        projectCode: String,
        permission: AuthPermission
    ): Boolean {
        logger.info("v3 validateUserResourcePermission user[$user] serviceCode[${serviceCode.id()}] resourceType[${resourceType.value}] permission[${permission.value}]")
        val actionType = ActionUtils.buildAction(resourceType, permission)
        return authHelper.isAllowed(user, actionType)
    }

    // 判断用户是否有某个动作某个实例的权限。
    override fun validateUserResourcePermission(
        user: String,
        serviceCode: AuthServiceCode,
        resourceType: AuthResourceType,
        projectCode: String,
        resourceCode: String,
        permission: AuthPermission,
        relationResourceType: AuthResourceType?
    ): Boolean {
        logger.info("v3 validateUserResourcePermission user[$user] serviceCode[${serviceCode.id()}] resourceType[${resourceType.value}] permission[${permission.value}]")
        val actionType = ActionUtils.buildAction(resourceType, permission)
        val instanceDTO = InstanceDTO()
        instanceDTO.id = resourceCode
        instanceDTO.system = iamConfiguration.systemId
        if(relationResourceType != null) {
            instanceDTO.type = relationResourceType!!.value
        } else {
            instanceDTO.type = resourceType.value
        }
        logger.info("v3 validateUserResourcePermission actionType[$actionType], resourceCode[$resourceCode] resourceType[$resourceType]")
        return authHelper.isAllowed(user, actionType, instanceDTO)
    }

    // 获取用户某动作下的所有有权限的实例。 如 获取A项目下的所有有查看权限的流水线
    override fun getUserResourceByPermission(
        user: String,
        serviceCode: AuthServiceCode,
        resourceType: AuthResourceType,
        projectCode: String,
        permission: AuthPermission,
        supplier: (() -> List<String>)?
    ): List<String> {
        logger.info("v3 getUserResourceByPermission user[$user] serviceCode[${serviceCode.id()}] resourceType[${resourceType.value}] projectCode[$projectCode] permission[${permission.value}] supplier[$supplier]")
        val actionType = ActionUtils.buildAction(resourceType, permission)
        val actionDto = ActionDTO()
        actionDto.id = actionType
        val expression = policyService.getPolicyByAction(user, actionDto, null) ?: return emptyList()
        logger.info("getUserResourceByPermission expression:$expression")
        if(resourceType == AuthResourceType.PROJECT) {
            return AuthUtils.getProjects(expression)
        } else {

        }

        return supplier?.invoke() ?: emptyList()
    }

    override fun getUserResourcesByPermissions(
        user: String,
        serviceCode: AuthServiceCode, // 对应新版的systemId
        resourceType: AuthResourceType,
        projectCode: String,
        permissions: Set<AuthPermission>,
        supplier: (() -> List<String>)?
    ): Map<AuthPermission, List<String>> {
        logger.info("v3 getUserResourcesByPermissions user[$user] serviceCode[$serviceCode] resourceType[$resourceType] projectCode[$projectCode] permission[$permissions] supplier[$supplier]")
        return getUserResourcesByPermissions(
            userId = user,
            scopeType = "Project",
            scopeId = projectCode,
            resourceType = resourceType,
            permissions = permissions,
            systemId = serviceCode,
            supplier = supplier
        )
    }

    override fun getUserResourcesByPermissions(
        userId: String,
        scopeType: String,
        scopeId: String,
        resourceType: AuthResourceType,
        permissions: Set<AuthPermission>,
        systemId: AuthServiceCode,
        supplier: (() -> List<String>)?
    ): Map<AuthPermission, List<String>> {
        logger.info("v3 getUserResourcesByPermissions user[$userId] scopeType[$scopeType] scopeId[$scopeId] resourceType[$resourceType] systemId[$systemId] permission[$permissions] supplier[$supplier]")
        val actionList = mutableListOf<ActionDTO>()
        permissions.map {
            val authType = ActionUtils.buildAction(resourceType, it)
            val actionDTO = ActionDTO()
            actionDTO.id = authType
            actionList.add(actionDTO)
        }
        return mutableMapOf()
    }

    companion object{
        val logger = LoggerFactory.getLogger(this::class.java)
    }
}