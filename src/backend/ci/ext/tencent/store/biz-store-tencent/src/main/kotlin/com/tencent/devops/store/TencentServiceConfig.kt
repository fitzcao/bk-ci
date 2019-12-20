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

package com.tencent.devops.store

import com.tencent.devops.common.client.Client
import com.tencent.devops.store.dao.atom.AtomDao
import com.tencent.devops.store.dao.atom.MarketAtomDao
import com.tencent.devops.store.dao.common.StoreDeptRelDao
import com.tencent.devops.store.dao.common.StoreMemberDao
import com.tencent.devops.store.dao.common.StoreProjectRelDao
import com.tencent.devops.store.dao.template.MarketTemplateDao
import com.tencent.devops.store.service.atom.impl.TxAtomCooperationServiceImpl
import com.tencent.devops.store.service.atom.impl.TxAtomMemberServiceImpl
import com.tencent.devops.store.service.atom.impl.TxAtomNotifyServiceImpl
import com.tencent.devops.store.service.atom.impl.TxAtomReleaseServiceImpl
import com.tencent.devops.store.service.atom.impl.TxAtomServiceImpl
import com.tencent.devops.store.service.atom.impl.TxMarketAtomServiceImpl
import com.tencent.devops.store.service.common.StoreUserService
import com.tencent.devops.store.service.common.StoreVisibleDeptService
import com.tencent.devops.store.service.common.impl.TxStoreLogoServiceImpl
import com.tencent.devops.store.service.common.impl.TxStoreNotifyServiceImpl
import com.tencent.devops.store.service.common.impl.TxStoreUserServiceImpl
import com.tencent.devops.store.service.container.impl.TxContainerServiceImpl
import com.tencent.devops.store.service.template.impl.TemplateVisibleDeptServiceImpl
import com.tencent.devops.store.service.template.impl.TxMarketTemplateServiceImpl
import com.tencent.devops.store.service.template.impl.TxTemplateNotifyServiceImpl
import com.tencent.devops.store.service.template.impl.TxTemplateReleaseServiceImpl
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TencentServiceConfig @Autowired constructor() {

    @Bean
    fun containerService() = TxContainerServiceImpl()

    @Bean
    fun storeUserService() = TxStoreUserServiceImpl()

    @Bean
    fun storeNotifyService() = TxStoreNotifyServiceImpl()

    @Bean
    fun atomService() = TxAtomServiceImpl()

    @Bean
    fun marketAtomService() = TxMarketAtomServiceImpl()

    @Bean
    fun atomMemberService() = TxAtomMemberServiceImpl()

    @Bean
    fun atomReleaseService() = TxAtomReleaseServiceImpl()

    @Bean
    fun atomNotifyService() = TxAtomNotifyServiceImpl()

    @Bean
    fun atomCooperationService() = TxAtomCooperationServiceImpl()

    @Bean
    fun templateNotifyService() = TxTemplateNotifyServiceImpl()

    @Bean
    fun marketTemplateService() = TxMarketTemplateServiceImpl()

    @Bean
    fun templateReleaseService() = TxTemplateReleaseServiceImpl()

    @Bean
    fun storeLogoService() = TxStoreLogoServiceImpl()

    @Bean
    fun templateVisibleDeptService(
        @Autowired dslContext: DSLContext,
        @Autowired client: Client,
        @Autowired storeDeptRelDao: StoreDeptRelDao,
        @Autowired marketTemplateDao: MarketTemplateDao,
        @Autowired marketAtomDao: MarketAtomDao,
        @Autowired atomDao: AtomDao,
        @Autowired storeProjectRelDao: StoreProjectRelDao,
        @Autowired storeMemberDao: StoreMemberDao,
        @Autowired storeVisibleDeptService: StoreVisibleDeptService,
        @Autowired storeUserService: StoreUserService
    ) = TemplateVisibleDeptServiceImpl(
        dslContext, client, storeDeptRelDao, marketTemplateDao, marketAtomDao, atomDao, storeProjectRelDao, storeMemberDao, storeVisibleDeptService, storeUserService
    )
}