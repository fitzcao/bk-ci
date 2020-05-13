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

package com.tencent.devops.environment.service

import com.tencent.devops.common.api.auth.AUTH_HEADER_DEVOPS_PROJECT_ID
import com.tencent.devops.common.api.pojo.OS
import com.tencent.devops.common.api.util.HashUtil
import com.tencent.devops.common.service.config.CommonConfig
import com.tencent.devops.model.environment.tables.records.TEnvironmentThirdpartyAgentRecord

/**
 * 腾讯内部旧版专用Agent下载链接生成服务
 */
class TencentAgentUrlServiceImpl constructor(
    private val commonConfig: CommonConfig
) : AgentUrlService {

    override fun genAgentInstallUrl(agentRecord: TEnvironmentThirdpartyAgentRecord): String {
        val gw = genGateway(agentRecord)
        val agentHashId = HashUtil.encodeLongId(agentRecord.id)
        return "http://$gw/external/agents/$agentHashId/install"
    }

    override fun genAgentUrl(agentRecord: TEnvironmentThirdpartyAgentRecord): String {
        val gw = genGateway(agentRecord)
        val agentHashId = HashUtil.encodeLongId(agentRecord.id)
        return "http://$gw/external/agents/$agentHashId/agent"
    }

    override fun genAgentInstallScript(agentRecord: TEnvironmentThirdpartyAgentRecord): String {
        val url = genAgentInstallUrl(agentRecord)
        return if (agentRecord.os != OS.WINDOWS.name) {
            "curl -H \"$AUTH_HEADER_DEVOPS_PROJECT_ID: ${agentRecord.projectId}\" $url | bash"
        } else {
            ""
        }
    }

    override fun genGateway(agentRecord: TEnvironmentThirdpartyAgentRecord): String {
        val gateway = if (agentRecord.gateway.isNullOrBlank())
            commonConfig.devopsBuildGateway!!
        else
            agentRecord.gateway

        return (when {
            gateway.startsWith("http://") -> gateway.substring(7)
            gateway.startsWith("https://") -> gateway.substring(8)
            else -> gateway
        }
            ).removePrefix("/")
    }
}