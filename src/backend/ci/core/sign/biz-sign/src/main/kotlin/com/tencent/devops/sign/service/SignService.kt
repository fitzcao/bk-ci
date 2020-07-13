package com.tencent.devops.sign.service

import com.tencent.devops.common.api.pojo.Result
import com.tencent.devops.sign.api.pojo.IpaSignInfo
import java.io.File
import java.io.InputStream

interface SignService {
    /*
    * 对ipa文件进行签名，并归档
    * */
    fun signIpaAndArchive(
            userId: String,
            ipaSignInfoHeader: String,
            ipaInputStream: InputStream
    ): String?

    /*
    * 对解压后的ipa目录进行签名
    * 对主App，扩展App和框架文件进行签名
    * */
    fun resignIpaPackage(
            ipaPackage: File,
            ipaSignInfo: IpaSignInfo
    ): File?

    /*
    * 解压ipa文件
    * */
    fun unzipIpa(
            ipaFile: File
    ): File

    /*
    * 压缩成ipa文件
    * */
    fun zipIpaFile(
            ipaFile: File
    ): File?

    /*
    * 对框架进行签名
    * */
    fun resignApp(
            appPath: File,
            certId: String,
            bundleId: String?,
            mobileProvision: File?
    ): Boolean


    /*
    * 下载描述文件
    * 返回描述文件所在目录
    * */
    fun downloadMobileProvision(
            ipaFile: File,
            ipaSignInfo: IpaSignInfo
    ): File
}