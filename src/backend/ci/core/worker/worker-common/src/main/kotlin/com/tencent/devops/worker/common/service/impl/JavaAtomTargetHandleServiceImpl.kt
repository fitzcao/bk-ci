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

package com.tencent.devops.worker.common.service.impl

import com.tencent.devops.common.api.enums.OSType
import com.tencent.devops.store.pojo.app.BuildEnv
import com.tencent.devops.store.pojo.common.enums.BuildHostTypeEnum
import com.tencent.devops.worker.common.JAVA_PATH_ENV
import com.tencent.devops.worker.common.service.AtomTargetHandleService
import org.slf4j.LoggerFactory

class JavaAtomTargetHandleServiceImpl : AtomTargetHandleService {

    private val logger = LoggerFactory.getLogger(JavaAtomTargetHandleServiceImpl::class.java)

    override fun handleAtomTarget(
        target: String,
        osType: OSType,
        buildHostType: BuildHostTypeEnum,
        systemEnvVariables: Map<String, String>,
        buildEnvs: List<BuildEnv>
    ): String {
        logger.info("handleAtomTarget target:$target,osType:$osType,buildHostType:$buildHostType")
        logger.info("handleAtomTarget systemEnvVariables:$systemEnvVariables,buildEnvs:$buildEnvs")
        var convertTarget = target
        // java插件先统一采用agent带的jre执行，如果是windows构建机需把target的启动命令替换下
        if (osType == OSType.WINDOWS) {
            convertTarget = target.replace("\$" + JAVA_PATH_ENV, "%$JAVA_PATH_ENV%")
        }
        logger.info("handleAtomTarget convertTarget:$convertTarget")
        return convertTarget
    }
}
