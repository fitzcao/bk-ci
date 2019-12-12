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

package com.tencent.devops.common.pipeline.type

import com.tencent.devops.common.api.pojo.OS

enum class BuildType(
    val value: String,
    val osList: List<OS>,
    val enableApp: Boolean/*是否支持选择对应的构建依赖*/,
    val clickable: Boolean/*是否可点击*/,
    val visable: Boolean? = true // 是否页面可见
) {
    ESXi("蓝盾公共构建资源", listOf(OS.MACOS), true, true, true),
    MACOS("蓝盾公共构建资源(NEW)", listOf(OS.MACOS), true, true, true),
    DOCKER("公共：Docker on Devnet 物理机", listOf(OS.LINUX), true, true, true),
    IDC("公共：Docker on IDC CVM", listOf(OS.LINUX), true, false, true),
    PUBLIC_DEVCLOUD("公共：Docker on DevCloud", listOf(OS.LINUX), true, true, true),
    TSTACK("Windows构建", listOf(), false, true, true), // tstack is deleted
    THIRD_PARTY_AGENT_ID("私有：单构建机", listOf(OS.MACOS, OS.LINUX, OS.WINDOWS), false, true, true),
    THIRD_PARTY_AGENT_ENV("私有：构建集群", listOf(OS.MACOS, OS.LINUX, OS.WINDOWS), false, true, true),
    THIRD_PARTY_PCG("PCG公共构建资源", listOf(OS.LINUX), false, true, true),
    THIRD_PARTY_DEVCLOUD("腾讯自研云（云devnet资源）", listOf(OS.LINUX), false, true, true),
    GIT_CI("工蜂CI", listOf(OS.LINUX), false, false, false)
}