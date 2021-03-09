package com.example.ios.pushy;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.notnoop.apns.APNS;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class IOSClientPush {
    private static ApnsClient apnsClient = null;
    private static final Semaphore semaphore = new Semaphore(10000);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        IOSClientPush push = new IOSClientPush();
//        FE45E37AAEF5A69762D5A85036DF7610BC66F96FBE8400754A9F580D24C5F6CE,magicA7AD9667-1F25-4963-BCE9-B7E9103F10D3

        String payload = APNS.newPayload().mdm("A13EDF6C-8A06-4273-892B-5AC6106E2E6E").build();
//        FE45E37AAEF5A69762D5A85036DF7610BC66F96FBE8400754A9F580D24C5F6CE,magicA13EDF6C-8A06-4273-892B-5AC6106E2E6E
        push.push(Collections.singletonList("7fe3770c26e140699f32131f48fffe00ed6c1a7162bd593fe11f7d44a7435f6c"), "{\"aps\":{\"alert\":\"消息通知\",\"sound\":\"default\",\"content-available\":\"\",\"badge\":3},\"type\":\"message\"}");

//        push.push(Collections.singletonList("FE45E37AAEF5A69762D5A85036DF7610BC66F96FBE8400754A9F580D24C5F6CE"), payload);


//7fe3770c26e140699f32131f48fffe00ed6c1a7162bd593fe11f7d44a7435f6c

//        |token:7fe3770c26e140699f32131f48fffe00ed6c1a7162bd593fe11f7d44a7435f6c,payload:{"aps":{"alert":"消息通知","sound":"default","content-available":"","badge":1},"type":"message"}

//        token:FE45E37AAEF5A69762D5A85036DF7610BC66F96FBE8400754A9F580D24C5F6CE,magicA13EDF6C-8A06-4273-892B-5AC6106E2E6E
    }

    public void push(List<String> deviceTokens, String payload) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        if (apnsClient == null) {
            try {
                EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
                apnsClient = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                        .setClientCredentials(new File("/Users/yanglidong/Desktop/AppPushCert.p12"), "pekall1234")
//                        .setClientCredentials(new File("/Users/yanglidong/Desktop/push.p12"), "pekall1234")
                        .setConcurrentConnections(4).setEventLoopGroup(eventLoopGroup).build();
            } catch (Exception e) {
                log.error("ios get pushy apns client failed!");
                e.printStackTrace();
            }
        }

        long total = deviceTokens.size();

        final CountDownLatch latch = new CountDownLatch(deviceTokens.size());

        final AtomicLong successCnt = new AtomicLong(0);

        long startPushTime = System.currentTimeMillis();

        for (String deviceToken : deviceTokens) {
//            final String token = TokenUtil.sanitizeTokenString(deviceToken);
//            SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(deviceToken, "", payload, null, null, PushType.MDM);
            SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(deviceToken, "com.pekall.ios.mdm", payload);

            try {
                semaphore.acquire();

            } catch (InterruptedException e) {
                log.error("ios push get semaphore failed, deviceToken:{}", deviceToken);

                e.printStackTrace();

            }
            final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                    future = apnsClient.sendNotification(pushNotification);
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                    future.get();
            if (pushNotificationResponse.isAccepted()) {
                log.info("Push notification accepted by APNs gateway.");
                successCnt.incrementAndGet();
            } else {
                log.info("Notification rejected by the APNs gateway: " +
                        pushNotificationResponse.getRejectionReason());

//                pushNotificationResponse.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
//                    log.info("\t…and the token is invalid as of " + timestamp);
//                });
            }


//            future.whenComplete((response, cause) -> {
//                latch.countDown();
//                semaphore.release();
//
//            });
        }

        try {
            latch.await(20, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            log.error("ios push latch await failed!");

            e.printStackTrace();

        }

        long endPushTime = System.currentTimeMillis();

        log.info("test pushMessage success. [共推送" + total + "个][成功" + (successCnt.get()) + "个]," +
                "totalcost = " + (endPushTime - startTime) + ", pushCost = " + (endPushTime - startPushTime));

    }

}
