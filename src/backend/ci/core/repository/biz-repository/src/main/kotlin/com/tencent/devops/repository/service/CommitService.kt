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

package com.tencent.devops.repository.service

import com.tencent.devops.common.api.enums.RepositoryType
import com.tencent.devops.common.api.util.HashUtil
import com.tencent.devops.common.api.util.timestampmilli
import com.tencent.devops.repository.dao.CommitDao
import com.tencent.devops.repository.dao.RepositoryDao
import com.tencent.devops.repository.pojo.commit.CommitData
import com.tencent.devops.repository.pojo.commit.CommitResponse
import com.tencent.devops.scm.utils.code.git.GitUtils
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommitService @Autowired constructor(
    private val repositoryDao: RepositoryDao,
    private val commitDao: CommitDao,
    private val dslContext: DSLContext
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CommitService::class.java)
    }

    fun getCommit(buildId: String): List<CommitResponse> {
        val commits = commitDao.getBuildCommit(dslContext, buildId)

        val repos = repositoryDao.getRepoByIds(dslContext, commits?.map { it.repoId } ?: listOf())
        val repoMap = repos?.map { it.repositoryId.toString() to it }?.toMap() ?: mapOf()

        return commits?.map {
            val repoUrl = repoMap.get(it.repoId.toString())?.url
            CommitData(
                it.type,
                it.pipelineId,
                it.buildId,
                it.commit,
                it.committer,
                it.commitTime.timestampmilli(),
                it.comment,
                it.repoId?.toString(),
                it.repoName,
                it.elementId,
                if (it.type.toInt() == 2 && repoUrl != null) {
                    val urlAndRepo = GitUtils.getDomainAndRepoName(repoUrl)
                    "https://${urlAndRepo.first}/${urlAndRepo.second}/commit/${it.commit}"
                } else null
            )
        }?.groupBy { it.elementId }?.map {
            val elementId = it.value[0].elementId
            val repoId = it.value[0].repoId
            CommitResponse(
                (repoMap[repoId]?.aliasName ?: "unknown repo"),
                elementId,
                it.value.filter { it.commit.isNotBlank() })
        } ?: listOf()
    }

    fun addCommit(commits: List<CommitData>): Int {
        logger.info("start to add commit: ${commits.firstOrNull()} ... ${commits.lastOrNull()}")
        return commitDao.addCommit(dslContext, commits).size
    }

    fun getLatestCommit(
        projectId: String,
        pipelineId: String,
        elementId: String,
        repositoryId: String,
        repositoryType: RepositoryType?,
        page: Int?,
        pageSize: Int?
    ): List<CommitData> {
        val repoId = if (repositoryType == null || repositoryType == RepositoryType.ID) {
            HashUtil.decodeOtherIdToLong(repositoryId)
        } else {
            repositoryDao.getByName(dslContext, projectId, repositoryId).repositoryId
        }
        val commitList = commitDao.getLatestCommitById(dslContext, pipelineId, elementId, repoId, page, pageSize) ?: return listOf()
        return commitList.map { data ->
            CommitData(
                data.type,
                pipelineId,
                data.buildId,
                data.commit,
                data.committer,
                data.commitTime.timestampmilli(),
                data.comment,
                data.repoId.toString(),
                null,
                data.elementId
            )
        }
    }
}