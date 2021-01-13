# 开线程跑任务参考如下

###第一种 :synchronized代码快
````
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Component
@EnableScheduling
@RequiredArgsConstructor
public class MsgExpireMap implements CommandLineRunner {

    public static volatile Map<Long, Date> msgMap = new ConcurrentHashMap<>();
    private static volatile ReentrantLock myMsgLock = new ReentrantLock();
    public static final String w = "w";
    private static volatile int expireTime = 15;
    private static int m = 1000 * 60;
    private static int readers = 0;
    private static int writers = 0;
    private static int index = 0;

    SimpleDateFormat timeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

    @Override
    public void run(String... args) {
        new MsgThread().start();
    }

    private class MsgThread extends Thread {

        @Override
        public void run() {
            while (true) synchronized (w) {
                try {
                    Iterator<Map.Entry<Long, Date>> iterator = msgMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        lockRead();
                        Map.Entry<Long, Date> next = iterator.next();
                        Long baseId = next.getKey();
                        try {
                            Date date = new Date();
                            Date sendTime = next.getValue();
                            if (null == sendTime) {
                                iterator.remove();
                            } else {
                                long timeDiff = (date.getTime() - sendTime.getTime()) / m;
//                                log.info("\n——CHECKTIMEDIFF baseId:{} now:{} sendTime:{} timeDiff:{}", baseId, timeFormat.format(date), timeFormat.format(sendTime), timeDiff);
                                if (timeDiff >= expireTime) {
                                    send(baseId);
//                                    log.info("\n等待内部调用结果 baseId:{} now:{} sendTime:{} timeDiff:{}", baseId, timeFormat.format(date), timeFormat.format(sendTime), timeDiff);
                                    iterator.remove();
                                }
                            }
                        } catch (Exception e) {
                            log.error("messageMap.entrySet().iterator() 异常  baseId:{}", baseId, e);
                        }
                        unlockRead();
                    }
                    if (msgMap.size() == 0) {
                        w.wait();
                    } else {
                        w.wait(1000 * 10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SneakyThrows
    public void put(Long baseId, Date sendTime) {
        lockWrite();
        msgMap.put(baseId, sendTime);
        unlockWrite();
    }

    @SneakyThrows
    public void finish(Long baseId) {
        lockWrite();
        msgMap.put(baseId, null);
        unlockWrite();
    }

    private void lockRead() throws InterruptedException {
        while (writers > 0 || readers > 0) synchronized (w) {
            w.wait();
        }
        readers++;
        myMsgLock.lock();
    }

    private void unlockRead() {
        synchronized (w) {
            readers--;
            myMsgLock.unlock();
            w.notifyAll();
        }
    }

    private void lockWrite() throws InterruptedException {
        while (readers > 0 || writers > 0) synchronized (w) {
            w.wait();
        }
        writers++;
        myMsgLock.lock();
    }

    private void unlockWrite() throws InterruptedException {
        synchronized (w) {
            writers--;
            myMsgLock.unlock();
            w.notifyAll();
        }
    }

    private void send(Long baseId) {

    }

}
````

###第二种：Lock->Condition
````
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Component
@EnableScheduling
@RequiredArgsConstructor
public class MsgExpireMap implements CommandLineRunner {

    private static int put = 0;
    private static int finish = 0;
    public static final String p = "w";
    public static final String f = "f";
    private static volatile int expireTime = 5 * 1000 * 60;
    private static volatile Lock lock = new ReentrantLock();
    private static volatile Condition readCon = lock.newCondition();
    public static volatile Map<Long, String> msgMap = new ConcurrentHashMap<>();
    private SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    @Override
    public void run(String... args) {
        new MsgThread().start();
    }

    private class MsgThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    while (put > 0 || finish > 0) {
                        readCon.await();
                    }
                    Iterator<Map.Entry<Long, String>> iterator = msgMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Long, String> next = iterator.next();
                        Long baseId = next.getKey();
                        try {
                            Date date = new Date();
                            String sendTime = next.getValue();
                            log.info("\n遍历id:{}, 放入时间:{}, 当前时间:{}", baseId, sendTime, now.format(date));
                            if (StrUtil.isBlank(sendTime)) {
                                log.info("\n移除id:{}, 当前时间:{}", baseId, now.format(date));
                                iterator.remove();
                            } else {
                                long timeDiff = date.getTime() - ft.parse(sendTime).getTime();
                                if (timeDiff >= expireTime) {
                                    log.info("\n过时id:{}, 放入时间:{}, 当前时间:{}", baseId, sendTime, now.format(date));
                                    send(baseId);
                                    iterator.remove();
                                }
                            }
                            while (put > 0 || finish > 0) {
                                readCon.await();
                            }
                        } catch (Exception e) {
                            log.error("messageMap.entrySet().iterator() 异常  baseId:{}", baseId, e);
                        }
                    }
                    log.info("\n\n\n");
                    if (msgMap.size() == 0) {
                        readCon.await();
                    } else {
                        readCon.await(10, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public void put(Long baseId, Date sendTime) {
        log.info("\nput id:{}, 放入时间:{}, 当前时间:{}", baseId, null == sendTime ? null : ft.format(sendTime), now.format(new Date()));
        try {
            while (put > 0) synchronized (p) {
                p.wait();
            }
            put++;
            lock.lock();
            msgMap.put(baseId, null == sendTime ? "" : ft.format(sendTime));
            put--;
            if (0 < put) {
                p.notify();
            } else {
                readCon.signal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void finish(Long baseId) {
        log.info("\nfinish id:{}, 当前时间:{}", baseId, now.format(new Date()));
        try {
            while (finish > 0) synchronized (f) {
                f.wait();
            }
            finish++;
            lock.lock();
            Thread.sleep(1);
            msgMap.put(baseId, "");
            finish--;
            if (finish > 0) {
                f.notify();
            } else {
                readCon.signal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void send(Long baseId) {
        if (null == baseId) return;
    }

    private static Long l = 156489654895L;
    private static Long j = 255555555553L;

    @Scheduled(cron = "0/20 * * * * ? ")
    public void te() {
        for (int i = 0; i < 2; i++) {
            put(l, new Date());
            l++;
        }
    }

    @Scheduled(cron = "0/30 * * * * ? ")
    public void finish() {
        for (int i = 0; i < 3; i++) {
            finish(j);
            j++;
        }
    }
}
````