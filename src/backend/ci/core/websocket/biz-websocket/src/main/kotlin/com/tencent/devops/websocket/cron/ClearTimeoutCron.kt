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

package com.tencent.devops.websocket.cron

import com.fasterxml.jackson.databind.ObjectMapper
import com.tencent.devops.common.redis.RedisOperation
import com.tencent.devops.common.websocket.utils.RedisUtlis
import com.tencent.devops.websocket.keys.WebsocketKeys
import com.tencent.devops.websocket.servcie.WebsocketService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ClearTimeoutCron(
    private val redisOperation: RedisOperation,
    private val objectMapper: ObjectMapper,
    private val websocketService: WebsocketService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * 每分钟一次，计算session是否已经超时，若超时，剔除该session关联的所有websocket redis信息。
     */
    @Scheduled(cron = "0 */1 * * * ?")
    fun newClearTimeoutCache() {
        longSessionLog()
        clearTimeoutSession()
    }

    private fun longSessionLog() {
        val longSessionList = websocketService.getLongSessionPage()
        longSessionList.forEach {
            logger.warn("this page[$it] sessionSize more 20")
        }
        if (longSessionList.size > 20) {
            websocketService.clearLongSessionPage()
        }
    }

    private fun clearTimeoutSession() {
        val nowTime = System.currentTimeMillis()
        for (bucket in 0..WebsocketKeys.REDIS_MO) {
            val redisData = redisOperation.get(WebsocketKeys.HASH_USER_TIMEOUT_REDIS_KEY + bucket)
            if (redisData != null) {
                var newSessionList: String? = null
                val sessionList = redisData.split(",")
                if (sessionList == null || sessionList.isEmpty()) {
                    logger.info("this bucket is empty,redisKey[${WebsocketKeys.HASH_USER_TIMEOUT_REDIS_KEY + bucket}]")
                    continue
                }
                sessionList.forEach {
                    try {
                        if (it != null) {
                            val timeout: Long = it.substringAfter("&").toLong()
                            val userId = it.substringAfter("#").substringBefore("&")
                            val sessionId = it.substringBefore("#")
                            if (nowTime > timeout) {
                                val sessionPage = RedisUtlis.getPageFromSessionPageBySession(redisOperation, sessionId)
                                RedisUtlis.cleanSessionPageBySessionId(redisOperation, sessionId)
                                if (sessionPage != null) {
                                    RedisUtlis.cleanPageSessionBySessionId(redisOperation, sessionId, sessionPage)
                                }
                                RedisUtlis.cleanUserSessionBySessionId(redisOperation, userId, sessionId)
                                websocketService.removeCacheSession(sessionId)
                                logger.info("[clearTimeOutSession] sessionId:$sessionId,loadPage:$sessionPage,userId:$userId")
                            } else {
                                newSessionList = if (newSessionList == null) {
                                    it
                                } else {
                                    "$newSessionList,$it"
                                }
                            }
                        }
                    } catch (e: Exception) {
                        logger.warn("fail msg: ${e.message}")
                    }
                }
                if (newSessionList != null) {
                    redisOperation.set(
                        WebsocketKeys.HASH_USER_TIMEOUT_REDIS_KEY + bucket,
                        newSessionList!!,
                        null,
                        true
                    )
                }
            }
        }
    }
}