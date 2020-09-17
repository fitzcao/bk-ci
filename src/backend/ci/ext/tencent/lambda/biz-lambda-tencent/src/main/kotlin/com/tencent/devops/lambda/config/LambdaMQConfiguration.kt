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
package com.tencent.devops.lambda.config

import com.tencent.devops.common.event.dispatcher.pipeline.mq.MQ
import com.tencent.devops.common.event.dispatcher.pipeline.mq.MQEventDispatcher
import com.tencent.devops.lambda.listener.LambdaBuildTaskFinishListener
import com.tencent.devops.lambda.listener.LambdaBuildFinishListener
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LambdaMQConfiguration {

    @Bean
    fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    fun pipelineEventDispatcher(rabbitTemplate: RabbitTemplate) = MQEventDispatcher(rabbitTemplate)

    /**
     * 构建结束广播交换机
     */
    @Bean
    fun pipelineBuildFinishFanoutExchange(): FanoutExchange {
        val fanoutExchange = FanoutExchange(MQ.EXCHANGE_PIPELINE_BUILD_FINISH_FANOUT, true, false)
        fanoutExchange.isDelayed = true
        return fanoutExchange
    }

    @Bean
    fun buildFinishLambdaQueue() = Queue(MQ.QUEUE_PIPELINE_BUILD_FINISH_LAMBDA)

    @Bean
    fun buildFinishLambdaQueueBind(
        @Autowired buildFinishLambdaQueue: Queue,
        @Autowired pipelineBuildFinishFanoutExchange: FanoutExchange
    ): Binding {
        return BindingBuilder.bind(buildFinishLambdaQueue).to(pipelineBuildFinishFanoutExchange)
    }

    @Bean
    fun pipelineBuildFinishListenerContainer(
        @Autowired connectionFactory: ConnectionFactory,
        @Autowired buildFinishLambdaQueue: Queue,
        @Autowired rabbitAdmin: RabbitAdmin,
        @Autowired lambdaBuildFinishListener: LambdaBuildFinishListener,
        @Autowired messageConverter: Jackson2JsonMessageConverter
    ): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer(connectionFactory)
        container.setQueueNames(buildFinishLambdaQueue.name)
        container.setConcurrentConsumers(10)
        container.setMaxConcurrentConsumers(10)
        container.setRabbitAdmin(rabbitAdmin)

        val adapter = MessageListenerAdapter(lambdaBuildFinishListener, lambdaBuildFinishListener::execute.name)
        adapter.setMessageConverter(messageConverter)
        container.messageListener = adapter
        return container
    }

    /**
     * 构建结束广播交换机
     */
    @Bean
    fun pipelineBuildElementFinishFanoutExchange(): FanoutExchange {
        val fanoutExchange = FanoutExchange(MQ.EXCHANGE_PIPELINE_BUILD_ELEMENT_FINISH_FANOUT, true, false)
        fanoutExchange.isDelayed = true
        return fanoutExchange
    }

    @Bean
    fun buildElementFinishLambdaQueue() = Queue(MQ.QUEUE_PIPELINE_BUILD_ELEMENT_FINISH_LAMBDA)

    @Bean
    fun buildElementFinishLambdaQueueBind(
        @Autowired buildElementFinishLambdaQueue: Queue,
        @Autowired pipelineBuildElementFinishFanoutExchange: FanoutExchange
    ): Binding {
        return BindingBuilder.bind(buildElementFinishLambdaQueue).to(pipelineBuildElementFinishFanoutExchange)
    }

    @Bean
    fun pipelineBuildElementFinishListenerContainer(
        @Autowired connectionFactory: ConnectionFactory,
        @Autowired buildElementFinishLambdaQueue: Queue,
        @Autowired rabbitAdmin: RabbitAdmin,
        @Autowired lambdaBuildTaskFinishListener: LambdaBuildTaskFinishListener,
        @Autowired messageConverter: Jackson2JsonMessageConverter
    ): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer(connectionFactory)
        container.setQueueNames(buildElementFinishLambdaQueue.name)
        container.setConcurrentConsumers(10)
        container.setMaxConcurrentConsumers(10)
        container.setRabbitAdmin(rabbitAdmin)

        val adapter = MessageListenerAdapter(lambdaBuildTaskFinishListener, lambdaBuildTaskFinishListener::execute.name)
        adapter.setMessageConverter(messageConverter)
        container.messageListener = adapter
        return container
    }
}