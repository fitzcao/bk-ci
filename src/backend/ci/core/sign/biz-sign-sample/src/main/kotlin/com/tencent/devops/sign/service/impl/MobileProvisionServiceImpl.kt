package com.tencent.devops.sign.service.impl

import com.tencent.devops.common.api.pojo.Result
import com.tencent.devops.sign.api.pojo.IpaSignInfo
import com.tencent.devops.sign.service.ArchiveService
import com.tencent.devops.sign.service.MobileProvisionService
import com.tencent.devops.sign.service.SignService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class MobileProvisionServiceImpl : MobileProvisionService {

    companion object {
        private val logger = LoggerFactory.getLogger(MobileProvisionServiceImpl::class.java)
    }

    override fun downloadAllMobileProvision(mobileProvisionDir: File, ipaSignInfo: IpaSignInfo): Boolean {
        TODO("Not yet implemented")
    }

    override fun downloadMobileProvision(mobileProvisionDir: File, mobileProvisionId: String): File {
        TODO("Not yet implemented")
    }

}