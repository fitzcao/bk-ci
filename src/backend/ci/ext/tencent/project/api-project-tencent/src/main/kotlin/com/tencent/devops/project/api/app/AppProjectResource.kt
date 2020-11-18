package com.tencent.devops.project.api.app

import com.tencent.devops.common.api.auth.AUTH_HEADER_DEVOPS_USER_ID
import com.tencent.devops.project.pojo.Result
import com.tencent.devops.project.pojo.app.AppProjectVO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api(tags = ["APP_PROJECT"], description = "项目列表接口")
@Path("/app/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface AppProjectResource {
    @GET
    @Path("/")
    @ApiOperation("查询所有项目")
    fun list(
        @ApiParam("userId", required = true)
        @HeaderParam(AUTH_HEADER_DEVOPS_USER_ID)
        userId: String
    ): Result<List<AppProjectVO>>
}