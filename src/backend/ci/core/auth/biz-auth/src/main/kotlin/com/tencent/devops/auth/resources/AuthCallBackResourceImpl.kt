package com.tencent.devops.auth.resources

import com.tencent.devops.auth.api.callback.AuthCallBackResource
import com.tencent.devops.common.api.pojo.Result

class AuthCallBackResourceImpl: AuthCallBackResource {

    override fun healthz(): Result<Boolean> {
        return Result(true)
    }
}